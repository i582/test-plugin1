package org.ton.tact.lang.psi

import com.intellij.psi.PsiElement

interface TactLazyBlock : TactCompositeElement {
    val lbrace: PsiElement?
    val rbrace: PsiElement?
}
