package org.ton.tact.lang.psi.impl

import com.intellij.openapi.fileEditor.impl.NonProjectFileWritingAccessProvider
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.PomRenameableTarget
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.PomTargetPsiElementImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.CommonProcessors
import com.intellij.util.Processor
import org.ton.tact.lang.TactLanguage
import org.ton.tact.lang.psi.TactFile

class TactModule(
    private val project: Project,
    private val name: String,
    val directory: PsiDirectory,
) : PomRenameableTarget<TactModule> {

    private var isValid = true

    override fun navigate(requestFocus: Boolean) {
        directory.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean = directory.canNavigate()

    override fun canNavigateToSource(): Boolean = false

    override fun getName(): String = name

    override fun isWritable(): Boolean {
        val processor: CommonProcessors.FindFirstProcessor<PsiFile> =
            object : CommonProcessors.FindFirstProcessor<PsiFile>() {
                override fun accept(file: PsiFile): Boolean {
                    return !NonProjectFileWritingAccessProvider.isWriteAccessAllowed(file.virtualFile, file.project)
                }
            }
        processFiles(processor)
        return !processor.isFound
    }

    fun toPsi(): TactPomTargetPsiElement = TactPomTargetPsiElement(project, this)

    fun getScope(): GlobalSearchScope {
        val files: MutableSet<VirtualFile> = HashSet()
        processFiles { f ->
            files.add(f.viewProvider.virtualFile)
        }
        return GlobalSearchScope.filesWithLibrariesScope(project, files)
    }

    private fun processFiles(processor: Processor<in PsiFile>) {
        if (!isValid()) {
            return
        }

        val fileIndexFacade = FileIndexFacade.getInstance(project)
        for (psiFile in directory.children) {
            if (psiFile !is PsiFile) continue
            val virtualFile = psiFile.virtualFile ?: continue
            ProgressIndicatorProvider.checkCanceled()

            if (virtualFile.isDirectory || !virtualFile.isValid) continue
            if (fileIndexFacade.isExcludedFile(virtualFile)) continue

            if (!processor.process(psiFile)) return
        }
    }

    override fun setName(newName: String): TactModule {
        if (!isValid() || name == newName) {
            return this
        }

        directory.name = newName
        isValid = false

        return this
    }

    override fun isValid(): Boolean {
        if (!isValid || project.isDisposed) return false
        return directory.isValid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TactModule

        if (name != other.name) return false
        return directory == other.directory
    }

    override fun hashCode(): Int {
        var result = directory.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object {
        fun fromDirectory(directory: PsiDirectory): TactModule {
            var name = directory.name
            for (psiFile in directory.children) {
                if (psiFile !is TactFile) continue
                name = psiFile.getModuleQualifiedName()
                break
            }

            return TactModule(directory.project, name, directory)
        }
    }

    class TactPomTargetPsiElement(project: Project, target: TactModule) : PomTargetPsiElementImpl(project, target),
        PsiNameIdentifierOwner {
        override fun getLanguage() = TactLanguage
        override fun getNameIdentifier() = null
        override fun getTarget() = super.getTarget() as TactModule
        override fun getUseScope() = GlobalSearchScope.allScope(project)
        override fun toString() = "Psi Wrapper[$target]"
    }
}
