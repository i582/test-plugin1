package org.tonstudio.tact.lang.psi.impl.imports

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.SmartPointerManager
import org.tonstudio.tact.lang.psi.TactFile
import org.tonstudio.tact.lang.psi.TactImportDeclaration
import org.tonstudio.tact.lang.psi.impl.*
import org.tonstudio.tact.lang.stubs.index.TactModulesIndex

class TactModuleReference<T : PsiElement>(element: T) : TactCachedReference<T>(element) {
    override fun bindToElement(element: PsiElement): PsiElement {
        return element // TODO
    }

    override fun resolveInner(): PsiElement? {
        val result = mutableListOf<PsiElement>()
        val p = object : TactScopeProcessorBase(element) {
            override fun execute(e: PsiElement, state: ResolveState): Boolean {
                result.add(e)
                return false
            }

            override fun crossOff(e: PsiElement): Boolean {
                return false
            }
        }
        processResolveVariants(p)
        return result.firstOrNull()
    }

    override fun processResolveVariants(processor: TactScopeProcessor): Boolean {
        val state = createContextOnElement(element)

        return processModules(processor, state)
    }

    private fun processModules(processor: TactScopeProcessor, state: ResolveState): Boolean {
        val element = element

        val fqn = when (element) {
            is TactImportDeclaration -> element.path
            else                     -> return true
        }

        val mods = TactImportResolver().resolve(element.project, fqn, element.containingFile, state)
        if (!mods.isNullOrEmpty()) {
            return !processor.execute(mods.first().toPsi(), state.put(TactReferenceBase.ACTUAL_NAME, fqn))
        }

        val modules = TactModulesIndex.get(fqn, element.project, null, null)
        if (modules.isEmpty()) {
            return findModuleDirectory(fqn, processor, state)
        }

        return processModules(modules, processor, state, fqn)
    }

    private fun processModules(
        modules: Collection<TactFile>,
        processor: TactScopeProcessor,
        state: ResolveState,
        name: String?,
    ) = modules.any {
        val dir = it.parent ?: return@any false
        val module = TactModule.fromDirectory(dir)
        !processor.execute(module.toPsi(), state.put(TactReferenceBase.ACTUAL_NAME, name))
    }

    private fun findModuleDirectory(
        name: String,
        processor: TactScopeProcessor,
        state: ResolveState,
    ): Boolean {
        // Since the index only stores module files, if a module doesn't
        // contain files, it won't be in the index.
        // Therefore, we need to find the nearest existing file in this
        // module and from there find the folder that contains the original
        // module we're looking for.

        val submodules = TactModulesIndex.getSubmodulesOfAnyDepth(project, name)
        val minSubmodule = submodules.minByOrNull { it.name } ?: return true

        val countMinSubmoduleDots = minSubmodule.getModuleQualifiedName().count { it == '.' }
        val countDotsInSearchName = name.count { it == '.' }

        var parent = minSubmodule.parent
        for (i in 0 until countMinSubmoduleDots - countDotsInSearchName) {
            parent = parent?.parent
        }

        if (parent != null) {
            val module = TactModule.fromDirectory(parent)
            return !processor.execute(module.toPsi(), state.put(TactReferenceBase.ACTUAL_NAME, name))
        }

        return true
    }

    private fun createContextOnElement(element: PsiElement): ResolveState {
        return ResolveState.initial().put(
            TactPsiImplUtil.CONTEXT,
            SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)
        )
    }
}
