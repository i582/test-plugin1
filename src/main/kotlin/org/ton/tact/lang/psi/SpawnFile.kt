package org.ton.tact.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ArrayFactory
import org.ton.tact.ide.ui.Icons
import org.ton.tact.lang.TactFileType
import org.ton.tact.lang.TactLanguage
import org.ton.tact.lang.TactTypes
import org.ton.tact.lang.psi.impl.ResolveUtil
import org.ton.tact.lang.psi.impl.TactElementFactory
import org.ton.tact.lang.stubs.TactContractDeclarationStub
import org.ton.tact.lang.stubs.TactMessageDeclarationStub
import org.ton.tact.lang.stubs.TactPrimitiveDeclarationStub
import org.ton.tact.lang.stubs.TactTraitDeclarationStub
import org.ton.tact.lang.stubs.types.*

open class TactFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TactLanguage) {
    override fun getFileType() = TactFileType

    override fun toString() = "Tact Language file"

    override fun getIcon(flags: Int) = Icons.Tact

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement,
    ): Boolean {
        return ResolveUtil.processChildren(this, processor, state, lastParent, place)
    }

    fun getModuleQualifiedName(): String = "" // TODO

    fun addImport(path: String, alias: String?): TactImportDeclaration? {
        if (getImportedModulesMap().containsKey(path)) {
            return getImportedModulesMap()[path]!!
        }

        return addImportImpl(path, alias)
    }

    fun getImportedModulesMap(): Map<String, TactImportDeclaration> {
        return CachedValuesManager.getCachedValue(this) {
            val map = mutableMapOf<String, TactImportDeclaration>()
            for (spec in getImports()) {
                val path = spec.path
                map[path] = spec
            }
            CachedValueProvider.Result.create(map, this)
        }
    }

    fun getImportList(): TactImportList? {
        return findChildByClass(TactImportList::class.java)
    }

    fun getImports(): List<TactImportDeclaration> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                getImportsImpl(),
                this,
            )
        }
    }

    private fun getImportsImpl(): List<TactImportDeclaration> {
        return findChildByClass(TactImportList::class.java)
            ?.importDeclarationList
            ?: emptyList()
    }

    fun resolveImportSpec(name: String): TactImportDeclaration? {
        return resolveImportNameAndSpec(name).second
    }

    private fun resolveImportNameAndSpec(name: String): Triple<String?, TactImportDeclaration?, Boolean> {
        val imports = getImports()
        for (import in imports) {
            // TODO
//            if (import.importPath.lastPart == name) {
//                return Triple(import.importPath.qualifiedName, import, false)
//            }
        }

        return Triple(null, null, false)
    }

    fun getFunctions(): List<TactFunctionDeclaration> =
        getNamedElements(TactTypes.FUNCTION_DECLARATION, TactFunctionDeclarationStubElementType.ARRAY_FACTORY)

    fun getStructs(): List<TactStructDeclaration> =
        getNamedElements(TactTypes.STRUCT_DECLARATION, TactStructDeclarationStubElementType.ARRAY_FACTORY)

    fun getMessages(): List<TactMessageDeclaration> =
        getNamedElements(TactTypes.MESSAGE_DECLARATION, TactMessageDeclarationStub.Type.ARRAY_FACTORY)

    fun getContracts(): List<TactContractDeclaration> =
        getNamedElements(TactTypes.CONTRACT_DECLARATION, TactContractDeclarationStub.Type.ARRAY_FACTORY)

    fun getTraits(): List<TactTraitDeclaration> =
        getNamedElements(TactTypes.TRAIT_DECLARATION, TactTraitDeclarationStub.Type.ARRAY_FACTORY)

    fun getPrimitives(): List<TactPrimitiveDeclaration> =
        getNamedElements(TactTypes.PRIMITIVE_DECLARATION, TactPrimitiveDeclarationStub.Type.ARRAY_FACTORY)

    fun getConstants(): List<TactConstDefinition> =
        getNamedElements(TactTypes.CONST_DEFINITION, TactConstDefinitionStubElementType.ARRAY_FACTORY)

    fun getModuleVars(): List<TactModuleVarDefinition> {
        val value = {
            if (stub != null) {
                getChildrenByType(stub!!, TactTypes.MODULE_VAR_DEFINITION) { arrayOfNulls<TactModuleVarDefinition>(it) }
            } else {
                val decls = children.filterIsInstance<TactModuleVarDeclaration>()
                decls.flatMap { it.moduleVarDefinitionList }
            }
        }

        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(value(), this)
        }
    }

    private inline fun <reified T : PsiElement> getNamedElements(elementType: IElementType, arrayFactory: ArrayFactory<T>): List<T> {
        return CachedValuesManager.getCachedValue(this) {
            val stub = stub

            if (stub == null) {
                val elements = mutableListOf<T>()
                this.children.forEach {
                    if (it is T) {
                        elements.add(it)
                    }
                    if (elementType == TactTypes.CONST_DEFINITION && it is TactConstDeclaration) {
                        elements.add(it.constDefinition as T)
                    }
                }
                return@getCachedValue CachedValueProvider.Result.create(elements, this)
            }

            val elements = getChildrenByType(stub, elementType, arrayFactory).toMutableList()

            CachedValueProvider.Result.create(elements, this)
        }
    }

    private fun <E : PsiElement?> getChildrenByType(
        stub: StubElement<out PsiElement>,
        elementType: IElementType,
        f: ArrayFactory<E>,
    ): List<E> {
        return listOf(*stub.getChildrenByType(elementType, f))
    }

    private fun addImportImpl(name: String, alias: String?): TactImportDeclaration {
        val list = getImportList()
        val project = project

        val decl = TactElementFactory.createImportDeclaration(project, name, alias)!!
        if (list == null) {
            var importList = TactElementFactory.createImportList(project, name)!!
            addBefore(TactElementFactory.createDoubleNewLine(project), firstChild)
            importList = addBefore(importList, firstChild) as TactImportList
            return importList.importDeclarationList.first()!!
        }
        return addImportDeclaration(list, decl)
    }

    private fun addImportDeclaration(importList: TactImportList, newImportDeclaration: TactImportDeclaration): TactImportDeclaration {
        val lastImport = importList.importDeclarationList.last()
        val importDeclaration = importList.addAfter(newImportDeclaration, lastImport) as TactImportDeclaration
        val importListNextSibling = importList.nextSibling
        if (importListNextSibling !is PsiWhiteSpace) {
            importList.addAfter(TactElementFactory.createNewLine(importList.project), importDeclaration)
            if (importListNextSibling != null) {
                // double new line if there is something valuable after import list
                importList.addAfter(TactElementFactory.createNewLine(importList.project), importDeclaration)
            }
        }
        importList.addBefore(TactElementFactory.createNewLine(importList.project), importDeclaration)
        return importDeclaration
    }
}