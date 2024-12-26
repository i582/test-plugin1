package org.ton.tact.lang.psi

import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.util.SimpleModificationTracker
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiTreeChangeEventImpl
import com.intellij.psi.impl.PsiTreeChangePreprocessor
import com.intellij.util.containers.ContainerUtil

class TactPsiTreeChangeProcessor : PsiTreeChangePreprocessor {
    private val myPackageTrackers = ContainerUtil.createConcurrentWeakValueMap<PsiDirectory, SimpleModificationTracker>()

    override fun treeChanged(event: PsiTreeChangeEventImpl) {
        incPackageTrackers(event)
    }

    private fun incPackageTrackers(event: PsiTreeChangeEventImpl) {
        val file = getFile(event)
        if (file == null || !file.isValid) return
        if (file is TactFile) {
            val directory = file.containingDirectory
            if (directory != null) {
                getTracker(directory).incModificationCount()
            }
        }
    }

    fun getTracker(directory: PsiDirectory): SimpleModificationTracker {
        return myPackageTrackers.computeIfAbsent(directory) { SimpleModificationTracker() }
    }

    private fun getFile(event: PsiTreeChangeEventImpl): PsiFile? {
        return event.file ?: return event.child as? PsiFile
    }
}

fun moduleModificationTracker(element: PsiElement): ModificationTracker {
    val directory = if (element is PsiDirectory) {
        element
    } else {
        val file = element.containingFile
        file?.containingDirectory
    }
    if (directory == null) {
        return element.manager.modificationTracker
    }
    return PsiTreeChangePreprocessor.EP.findExtensionOrFail(TactPsiTreeChangeProcessor::class.java, element.project).getTracker(directory)
}
