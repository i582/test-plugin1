package org.ton.tact.lang.psi.impl

import com.intellij.psi.PsiElement
import org.ton.tact.lang.psi.TactAttributeIdentifier

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

    companion object {
        fun convertPascalCaseToSnakeCase(name: String): String {
            return name.replace("([A-Z])".toRegex()) { "_${it.value}" }.trim('_').lowercase()
        }
    }
}
