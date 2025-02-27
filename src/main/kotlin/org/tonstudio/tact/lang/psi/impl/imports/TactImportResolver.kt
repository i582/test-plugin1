package org.tonstudio.tact.lang.psi.impl.imports

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.ResolveState
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.containers.ConcurrentFactoryMap
import com.intellij.util.containers.ContainerUtil
import org.tonstudio.tact.lang.psi.impl.TactModule
import org.tonstudio.tact.toolchain.TactToolchainService.Companion.toolchainSettings
import org.tonstudio.tact.configurations.TactConfiguration
import java.util.concurrent.ConcurrentMap

class TactImportResolver {
    fun resolve(project: Project, name: String, containingFile: PsiFile, resolveState: ResolveState): List<TactModule>? {

        val cacheHolder = project

        val cachedMap: ConcurrentMap<String, List<TactModule>> =
            CachedValuesManager.getManager(project).getCachedValue(cacheHolder) {
                createResolveMap(project, containingFile)
            }
        return cachedMap[name]
    }

    private fun createResolveMap(project: Project, file: PsiFile): CachedValueProvider.Result<ConcurrentMap<String, List<TactModule>>>? {
        if (project.isDisposed) return null

        val map = ConcurrentFactoryMap.create<String, List<TactModule>>(
            { innerResolve(it, project, file) },
            { ContainerUtil.createConcurrentWeakValueMap() })

        val structure = VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS
        return CachedValueProvider.Result.create(map, structure)
    }

    private fun innerResolve(name: String, project: Project, file: PsiFile): List<TactModule>? {
        val toolchain = project.toolchainSettings.toolchain()
        val stdlibRoot = toolchain.stdlibDir()
        val projectRoot = project.guessProjectDir()
        val srcRoot = TactConfiguration.getInstance(project).srcLocation
        val localModules = TactConfiguration.getInstance(project).localModulesLocation
        val stubs = TactConfiguration.getInstance(project).stubsLocation

        val roots = listOfNotNull(stdlibRoot, projectRoot, srcRoot, localModules, stubs)

        val manager = PsiManager.getInstance(project)

        val parts = name.split('.')

        for (root in roots) {
            val moduleDir = findModuleDirectory(root, parts)
            if (moduleDir != null) {
                val psiDirectory = manager.findDirectory(moduleDir) ?: return null
                return listOf(TactModule.fromDirectory(psiDirectory))
            }
        }

        return emptyList()
    }

    private fun findModuleDirectory(root: VirtualFile, parts: List<String>): VirtualFile? {
        var currentDir: VirtualFile? = root

        for (part in parts) {
            currentDir = currentDir?.findChild(part)
            if (currentDir == null || !currentDir.isDirectory) {
                return null
            }
        }

        return currentDir
    }
}