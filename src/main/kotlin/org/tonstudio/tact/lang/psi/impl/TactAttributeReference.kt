package org.tonstudio.tact.lang.psi.impl

import com.intellij.psi.PsiElement
import org.tonstudio.tact.lang.psi.TactAttributeIdentifier

class TactAttributeReference(element: TactAttributeIdentifier) : TactCachedReference<TactAttributeIdentifier>(element) {
    override fun resolveInner(): PsiElement? {
        val p = TactVarProcessor(myElement, false)
        processResolveVariants(p)
        return p.getResult()
    }

    override fun processResolveVariants(processor: TactScopeProcessor): Boolean {
        val name = myElement.text
        if (name.isBlank()) {
            return false
        }
        return false
    }
}
