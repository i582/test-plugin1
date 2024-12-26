package org.ton.tact.lang.psi.impl

import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Conditions
import com.intellij.openapi.util.RecursionManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.intellij.util.ArrayUtil
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.TactPsiImplUtil.processNamedElements
import org.ton.tact.lang.psi.impl.imports.TactImportResolver
import org.ton.tact.lang.psi.types.*
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.stubs.StubWithText
import org.ton.tact.lang.stubs.index.TactModulesIndex
import org.ton.tact.lang.stubs.index.TactNamesIndex
import org.ton.tact.toolchain.TactToolchainService.Companion.toolchainSettings

class TactReference(el: TactReferenceExpressionBase, val forTypes: Boolean = false) :
    TactReferenceBase<TactReferenceExpressionBase>(
        el,
        TextRange.from(
            el.getIdentifierBounds()?.first ?: 0,
            el.getIdentifierBounds()?.second ?: el.textLength,
        )
    ) {

    companion object {
        private val MY_RESOLVER: ResolveCache.PolyVariantResolver<TactReference> =
            ResolveCache.PolyVariantResolver { ref, _ -> ref.resolveInner() }

        private fun getContextElement(state: ResolveState?): PsiElement? {
            val context = state?.get(TactPsiImplUtil.CONTEXT)
            return context?.element
        }

        fun getContextFile(state: ResolveState): PsiFile? {
            return getContextElement(state)?.containingFile
        }

        fun isLocalResolve(origin: TactFile, external: TactFile?): Boolean {
            if (external == null) return true

            val originModule = origin.getModuleQualifiedName()
            val externalModule = external.getModuleQualifiedName()
            return originModule == externalModule
        }
    }

    private val stubsManager = project.service<TactStubsManager>()

    private val identifier: PsiElement?
        get() = myElement?.getIdentifier()

    private val identifierText: String?
        get() {
            val element = myElement
            if (element is StubBasedPsiElementBase<*>) {
                val stub = element.stub
                if (stub != null) {
                    if (stub is StubWithText) {
                        return stub.getText()
                    }
                }
            }

            return identifier?.text
        }

    private fun resolveInner(): Array<ResolveResult> {
        if (!myElement.isValid) return ResolveResult.EMPTY_ARRAY
        val result = mutableSetOf<ResolveResult>()
        processResolveVariants(createResolveProcessor(result, myElement))
        return result.toTypedArray()
    }

    fun processResolveVariants(processor: TactScopeProcessor): Boolean {
        val element = myElement
        val file = element.containingFile as? TactFile ?: return false

        val state = if (file is TactCodeFragment) {
            createContextOnElement(element).put(PROCESS_PRIVATE_MEMBERS, true)
        } else {
            createContextOnElement(element)
        }

        val qualifier = element.getQualifier()
        return if (qualifier != null) {
            processQualifierExpression(qualifier, processor, state)
        } else {
            processUnqualifiedResolve(file, processor, state)
        }
    }

    private fun processQualifierExpression(
        qualifier: TactCompositeElement,
        processor: TactScopeProcessor,
        state: ResolveState,
    ): Boolean {
        if (qualifier is TactExpression) {
            val type = qualifier.getType(null)

            if (type != null) {
                if (!processType(type, processor, state)) {
                    return false
                }
            }

            if (qualifier is TactReferenceExpression) {
                if (!processReferenceQualifier(qualifier, processor, state)) return false
            }
        }

        if (qualifier is TactTypeReferenceExpression) {
            if (!processReferenceQualifier(qualifier, processor, state)) return false
        }

        return true
    }

    private fun processReferenceQualifier(
        qualifier: TactReferenceExpressionBase,
        processor: TactScopeProcessor,
        state: ResolveState,
    ): Boolean {
        val project = project
        val resolved = qualifier.resolve()

        if (resolved is TactModule.TactPomTargetPsiElement) {
            val target = resolved.target

            val containingFile = qualifier.containingFile as TactFile
            val containingModule = ""
            if (containingModule != null && qualifier.textMatches(containingModule)) {
                if (!processFileEntities(containingFile, processor, state, true)) return false
            }

            if (!processModule(target.name, processor, state)) return false
        }

        val file = qualifier.containingFile as? TactFile
        val contextFile = getContextFile(state) ?: myElement.containingFile
        if (contextFile !is TactFile) {
            return true
        }

        return true
    }

    // TODO
    private fun processImportPath(
        importPath: TactImportDeclaration,
        processor: TactScopeProcessor,
        state: ResolveState,
    ): Boolean {
        val moduleName = importPath.path
        return processModule(moduleName, processor, state)
    }

    private fun processModule(moduleName: String, processor: TactScopeProcessor, state: ResolveState): Boolean {
        val mods = TactImportResolver().resolve(element.project, moduleName, element.containingFile, state)
        if (!mods.isNullOrEmpty()) {
            val newState = state
                .put(NEED_QUALIFIER_NAME, false)

            if (!processDirectory(mods.first().directory, processor, newState, false)) {
                return false
            }
        }

        val (moduleFile, moduleDir) =
            TactModulesIndex.findDir(moduleName, myElement.project, GlobalSearchScope.allScope(myElement.project)) ?: return true

        val newState = state
            .put(MODULE_NAME, moduleFile.getModuleQualifiedName())
            .put(NEED_QUALIFIER_NAME, false)
        return !processDirectory(moduleDir, processor, newState, false)
    }

    private fun processType(type: TactTypeEx, processor: TactScopeProcessor, state: ResolveState): Boolean {
        val anchor = type.anchor(project)
        if (anchor != null && !anchor.isValid) {
            return true
        }

        val result = RecursionManager.doPreventingRecursion(type, true) {
            if (!processExistingType(type, processor, state)) return@doPreventingRecursion false

            true
        }
        return result == true
    }

    private fun processExistingType(typ: TactTypeEx, processor: TactScopeProcessor, state: ResolveState): Boolean {
        val project = project
        val anchor = typ.anchor(project)
        val file = anchor?.containingFile as? TactFile
        val contextFile = getContextFile(state) ?: myElement.containingFile
        if (contextFile !is TactFile) {
            return true
        }

        val localResolve = isLocalResolve(contextFile, file)
        val newState = state.put(LOCAL_RESOLVE, localResolve)

        if (typ is TactContractTypeEx) {
            val declaration = typ.resolve(project) ?: return true
            val contractType = declaration.contractType

            if (!processNamedElements(processor, newState, contractType.fieldList, localResolve)) return false
            if (!processNamedElements(processor, newState, contractType.methodsList, localResolve)) return false
            if (!processNamedElements(processor, newState, contractType.constantsList, localResolve)) return false
        }

        if (typ is TactStructTypeEx) {
            val declaration = typ.resolve(project) ?: return true
            val structType = declaration.structType

            if (!processNamedElements(processor, newState, structType.fieldList, localResolve)) return false
            if (!processMethods(typ, processor, newState, localResolve)) return false
        }

        if (typ is TactMessageTypeEx) {
            val declaration = typ.resolve(project) ?: return true
            val messageType = declaration.messageType

            if (!processNamedElements(processor, newState, messageType.fieldList, localResolve)) return false
            if (!processMethods(typ, processor, newState, localResolve)) return false
        }

        if (typ is TactTraitTypeEx) {
            val declaration = typ.resolve(project) ?: return true
            val traitType = declaration.traitType

            if (!processNamedElements(processor, newState, traitType.fieldList, localResolve)) return false
            if (!processNamedElements(processor, newState, traitType.methodsList, localResolve)) return false
            if (!processNamedElements(processor, newState, traitType.constantsList, localResolve)) return false

            if (typ.qualifiedName() != "BaseTrait") {
                val baseTrait = TactNamesIndex.find("BaseTrait", project, null).firstOrNull() as? TactTraitDeclaration ?: return true
                if (!processExistingType(baseTrait.traitType.toEx(), processor, state)) return false
            }

            val superTypes = traitType.withClause?.typeListNoPin?.typeList ?: return true
            if (superTypes.isEmpty()) return true

            for (superType in superTypes) {
                if (!processExistingType(superType.toEx(), processor, state)) return false
            }
        }

        if (!processMethods(typ, processor, newState, localResolve)) return false

        return true
    }

    private fun processMethods(type: TactTypeEx, processor: TactScopeProcessor, state: ResolveState, localResolve: Boolean): Boolean {
        if (state.get(NOT_PROCESS_METHODS) == true) return true
        return processNamedElements(processor, state, type.methodsList(project), localResolve)
    }

    private fun processNativeMethods(type: TactType, processor: TactScopeProcessor, state: ResolveState, localResolve: Boolean): Boolean {
        if (state.get(NOT_PROCESS_METHODS) == true) return true
        val methods = TactLangUtil.getMethodListNative(project, type)
        return processNamedElements(processor, state, methods, localResolve)
    }

    private fun processUnqualifiedResolve(
        file: TactFile,
        processor: TactScopeProcessor,
        state: ResolveState,
    ): Boolean {
        val identText = identifierText!!

        if (identText == "_") {
            return processor.execute(myElement, state)
        }

        when (myElement.parent) {
            is TactFieldName -> {
                if (!processNamedParams(processor, state)) return false
                if (!processLiteralValueField(processor, state)) return false

                return true
            }
        }

        if (!processBlock(file, processor, state, true)) return false
//        if (!processImportSpec(file, processor, state)) return false
        if (!processImportedModulesForCompletion(file, processor, state)) return false
        if (!processImportedModules(file, processor, state, myElement)) return false
        if (!processFileEntities(file, processor, state, true)) return false
        if (!processDirectory(file.originalFile.parent, processor, state, true)) return false
        if (!processBuiltin(processor, state)) return false
        if (!processModulesEntities(file, processor, state)) return false

        if (identText == "self") {
            val owner = identifier!!.parentOfTypes(TactTraitDeclaration::class, TactContractDeclaration::class) ?: return true
            if (!processor.execute(owner, state.put(SEARCH_NAME, owner.name))) return false
        }

        return true
    }

    private fun processBuiltin(processor: TactScopeProcessor, state: ResolveState): Boolean {
        val builtin = myElement.project.toolchainSettings.toolchain().stdlibDir() ?: return true
        val psiManager = PsiManager.getInstance(myElement.project)
        builtin.children
            .map { psiManager.findFile(it) }
            .filterIsInstance<TactFile>()
            .forEach {
                if (!processFileEntities(it, processor, state, false))
                    return false
            }

        val modules = myElement.project.toolchainSettings.toolchain().rootDir()?.findChild("libs") ?: return true
        modules.children
            .map { psiManager.findFile(it) }
            .filterIsInstance<TactFile>()
            .forEach {
                if (!processFileEntities(it, processor, state, false))
                    return false
            }

        return true
    }

    private fun processDirectory(
        dir: PsiDirectory?,
        processor: TactScopeProcessor,
        state: ResolveState,
        localProcessing: Boolean,
    ): Boolean {
        if (dir == null) {
            return true
        }

        for (f in dir.files) {
            if (f !is TactFile) {
                continue
            }

            if (!processFileEntities(f, processor, state, localProcessing)) {
                return false
            }
        }

        return true
    }

    private fun processStubFile(name: String, processor: TactScopeProcessor, state: ResolveState): Boolean {
        val file = stubsManager.findFile(name) ?: return true
        return processFileEntities(file, processor, state, false)
    }

    // TODO: redone
    private fun processImportedModules(
        file: TactFile,
        processor: TactScopeProcessor,
        state: ResolveState,
        element: TactCompositeElement,
    ): Boolean {
        val searchName = identifier!!.text
        val spec = file.resolveImportSpec(searchName)
        if (spec == null) {
            val parentReferenceExpression = element.parent as? TactReferenceExpressionBase
            val qualifier = parentReferenceExpression?.getQualifier()

            // element: cat
            // parentReferenceExpression: cat.foo
            if (qualifier != null && PsiTreeUtil.isAncestor(qualifier, element, false)) {
                // when use `cat.new()` in `cat` module.
                val currentModule = ""
                if (currentModule != null && searchName == currentModule && file.containingDirectory != null) {
                    val module = TactModule.fromDirectory(file.containingDirectory!!)
                    if (!processor.execute(module.toPsi(), state.put(ACTUAL_NAME, searchName))) return false
                }
            }

            return true
        }

        // TODO
//        val resolved = spec.resolve()
//        return resolved.any { module ->
//            processor.execute(module.toPsi(), state.put(ACTUAL_NAME, searchName))
//        }
        return false
    }

    private fun processModulesEntities(file: TactFile, processor: TactScopeProcessor, state: ResolveState): Boolean {
        if (!processor.isCompletion() || file is TactDebuggerExpressionCodeFragment) {
            // This method is only for autocompletion when a user writes
            // a symbol (from another module) name, and we want to import
            // the symbol, and the module that contains it.
            return true
        }

        if (identifier?.textMatches(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED) == true) {
            return true
        }

        val modules = TactModulesIndex.getAll(element.project)
        for (moduleFile in modules) {

            if (!processFileEntities(moduleFile, processor, state.put(MODULE_NAME, moduleFile.getModuleQualifiedName()), false)) {
                return false
            }
        }

        return true
    }

    private fun processImportedModulesForCompletion(file: TactFile, processor: TactScopeProcessor, state: ResolveState): Boolean {
        if (!processor.isCompletion()) {
            // This method is only for autocompletion when a user writes
            // a symbol (from another module) name, and we want to import
            // the symbol, and the module that contains it.
            return true
        }

        val currentModule = file.getModuleQualifiedName()
        val imports = file.getImports()

        // TODO
//        imports
//            .filter { it.importAlias == null }
//            .map { it.importPath.lastPart }
//            .flatMap { TactModulesFingerprintIndex.find(it, element.project, null) }
//            .forEach {
//                if (it.getModuleQualifiedName() == currentModule) return@forEach
//                if (!processor.execute(it, state)) return false
//            }
//
//        imports
//            .mapNotNull { it.importAlias }
//            .forEach {
//                if (!processor.execute(it, state)) return false
//            }
//
//        imports
//            .mapNotNull { it.selectiveImportList?.referenceExpressionList }
//            .flatten()
//            .mapNotNull { it.resolve() }
//            .forEach {
//                if (!processor.execute(it, state)) return false
//            }

        return true
    }

    // TODO:
//    private fun processImportSpec(file: TactFile, processor: TactScopeProcessor, state: ResolveState): Boolean {
//        if (identifier?.parentOfType<TactImportSpec>() == null) {
//            return true
//        }
//
//        val spec = file.resolveImportSpec(identifier!!.text) ?: return false
//        val resolved = spec.resolve()
//
//        return resolved.any { module ->
//            processor.execute(module.toPsi(), state.put(ACTUAL_NAME, module.name))
//        }
//    }

    private fun processFileEntities(
        file: TactFile,
        processor: TactScopeProcessor,
        state: ResolveState,
        localProcessing: Boolean,
    ): Boolean {
        if (!processNamedElements(
                processor,
                state,
                file.getStructs(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getTraits(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getMessages(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getFunctions(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getAsmFunctions(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getNativeFunctions(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getModuleVars(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getContracts(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        if (!processNamedElements(
                processor,
                state,
                file.getPrimitives(),
                Conditions.alwaysTrue(),
                localProcessing,
                false
            )
        ) return false

        return processNamedElements(
            processor,
            state,
            file.getConstants(),
            Conditions.alwaysTrue(),
            localProcessing,
            false
        )
    }

    private fun processBlock(file: TactFile, processor: TactScopeProcessor, state: ResolveState, localResolve: Boolean): Boolean {
        val context = if (file is TactCodeFragment) file.context else myElement

        val newState = if (file is TactCodeFragment)
            state.put(PROCESS_PRIVATE_MEMBERS, true)
        else
            state

        val delegate = createDelegate(processor, file is TactCodeFragment)
        ResolveUtil.treeWalkUp(context, delegate)
        return processNamedElements(processor, newState.put(NOT_PROCESS_EMBEDDED_DEFINITION, true), delegate.getVariants(), localResolve)
    }

    private fun processNamedParams(processor: TactScopeProcessor, state: ResolveState): Boolean {
        val callExpr = TactCodeInsightUtil.getCallExpr(myElement) ?: return true
        val resolved = callExpr.resolve() as? TactSignatureOwner ?: return true
        val params = resolved.getSignature()?.parameters?.paramDefinitionList ?: return true
        return processNamedElements(processor, state, params, true)
    }

    private fun processLiteralValueField(processor: TactScopeProcessor, state: ResolveState): Boolean {
        val initExpr = element.parentOfType<TactLiteralValueExpression>()
        val type = initExpr?.type?.toEx() ?: return true
        return processType(type, processor, state)
    }

    private fun createDelegate(processor: TactScopeProcessor, isCodeFragment: Boolean = false): TactVarProcessor {
        return object : TactVarProcessor(identifier!!, myElement, processor.isCompletion(), isCodeFragment) {
            override fun crossOff(e: PsiElement): Boolean {
                return if (e is TactFieldDeclaration)
                    true
                else
                    super.crossOff(e)
            }
        }
    }

    private fun createContextOnElement(element: PsiElement): ResolveState {
        return ResolveState.initial().put(
            TactPsiImplUtil.CONTEXT,
            SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)
        )
    }

    private fun createResolveProcessor(
        result: MutableCollection<ResolveResult>,
        reference: TactReferenceExpressionBase,
    ): TactScopeProcessor {
        return object : TactScopeProcessor() {
            override fun execute(element: PsiElement, state: ResolveState): Boolean {
                if (element == reference) {
                    return !result.add(PsiElementResolveResult(element))
                }

                val name = state.get(ACTUAL_NAME) ?: when (element) {
                    is PsiNamedElement -> element.name
                    else               -> null
                }

                val ident = state.get(SEARCH_NAME) ?: reference.getIdentifier()?.text ?: return true

                if (name != null && ident == name) {
                    result.add(PsiElementResolveResult(element))
                    return false
                }
                return true
            }
        }
    }

    override fun isReferenceTo(element: PsiElement) = couldBeReferenceTo(element, myElement) && super.isReferenceTo(element)

    private fun couldBeReferenceTo(definition: PsiElement, reference: PsiElement): Boolean {
        if (definition is PsiDirectory && reference is TactReferenceExpressionBase) return true

        val definitionFile = definition.containingFile ?: return true
        val referenceFile = reference.containingFile

        val inSameFile = definitionFile.isEquivalentTo(referenceFile)
        if (inSameFile) return true
        return if (TactCodeInsightUtil.sameModule(referenceFile, definitionFile))
            true
        else
            reference !is TactNamedElement
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult?> {
        if (!myElement.isValid) return ResolveResult.EMPTY_ARRAY

        return ResolveCache.getInstance(myElement.project)
            .resolveWithCaching(this, MY_RESOLVER, false, false)
    }

    override fun getVariants(): Array<Any> = ArrayUtil.EMPTY_OBJECT_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement? {
        identifier?.replace(TactElementFactory.createIdentifier(myElement.project, newElementName))
        return myElement
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TactReference

        return element == other.element
    }

    override fun hashCode(): Int = element.hashCode()
}
