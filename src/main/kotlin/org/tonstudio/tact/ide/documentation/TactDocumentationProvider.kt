package org.tonstudio.tact.ide.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import org.tonstudio.tact.lang.psi.TactFunctionDeclaration
import org.tonstudio.tact.lang.psi.TactNativeFunctionDeclaration

class TactDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?) = when (element) {
        is TactFunctionDeclaration       -> element.generateDoc()
        is TactNativeFunctionDeclaration -> element.generateDoc()
        else                             -> null
    }
}
