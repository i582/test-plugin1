package org.ton.tact.lang.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class TactOperatorReference(element: TactBinaryOpImplMixin) : TactCachedReference<TactBinaryOpImplMixin>(
    element,
    range = element.operator?.textRangeInParent ?: TextRange.from(0, element.textLength)
) {
    override fun resolveInner(): PsiElement? {
        val result = mutableListOf<PsiElement>()
        val p = createResolveProcessor(result)
        processResolveVariants(p)
        return result.firstOrNull()
    }

    override fun processResolveVariants(processor: TactScopeProcessor): Boolean {
        val method = findMethod()
        if (method != null) {
            processor.execute(method, ResolveState.initial())
            return true
        }

        return true
    }

    private fun findMethod(): PsiElement? {
        val opText = element.operator?.text ?: return null
        val overloadName = overloadNameForOperator(opText) ?: return null
        val left = element.left
        val leftType = left.getType(null) ?: return null

        return leftType.findMethod(element.project, overloadName)
    }

    private fun createResolveProcessor(
        result: MutableCollection<PsiElement>,
    ): TactScopeProcessor {
        return object : TactScopeProcessor() {
            override fun execute(element: PsiElement, state: ResolveState): Boolean {
                return !result.add(element)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (other is TactOperatorReference) return element.isEquivalentTo(other.element)
        return false
    }

    override fun hashCode() = element.hashCode()

    companion object {
        fun overloadNameForOperator(operator: String): String? {
            return when (operator) {
                "+"  -> "add"
                "-"  -> "sub"
                "*"  -> "mul"
                "/"  -> "div"
                "%"  -> "rem"
                "==" -> "equal"
                "!=" -> "equal"
                "<"  -> "less"
                ">"  -> "less"
                "<=" -> "less"
                ">=" -> "less"
                else -> null
            }
        }
    }
}
