package org.ton.tact.lang.psi

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiFile

open class TactRecursiveVisitor : TactVisitor() {
    override fun visitCompositeElement(o: TactCompositeElement) {
        super.visitCompositeElement(o)
        for (psiElement in o.children) {
            psiElement.accept(this)
            ProgressIndicatorProvider.checkCanceled()
        }
    }

    override fun visitFile(file: PsiFile) {
        super.visitFile(file)
        for (psiElement in file.children) {
            psiElement.accept(this)
            ProgressIndicatorProvider.checkCanceled()
        }
    }
}
