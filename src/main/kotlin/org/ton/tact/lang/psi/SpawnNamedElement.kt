package org.ton.tact.lang.psi

import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

class Extern(
    val abi: String,
)

interface TactNamedElement : TactTypeOwner, TactDocumentationOwner, TactCompositeElement, PsiNameIdentifierOwner, NavigationItem {
    fun isBlank(): Boolean
    fun getIdentifier(): PsiElement?
    fun getQualifiedName(): String?
    fun getOwner(): PsiElement?
    fun isDeprecated(): Boolean
    fun getModuleName(): String?
}
