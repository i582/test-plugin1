package org.ton.tact.ide.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import org.ton.tact.lang.psi.TactFunctionDeclaration
import org.ton.tact.lang.psi.TactNativeFunctionDeclaration

class TactDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?) = when (element) {
        is TactFunctionDeclaration       -> element.generateDoc()
        is TactNativeFunctionDeclaration -> element.generateDoc()
        else                             -> null
    }
}
