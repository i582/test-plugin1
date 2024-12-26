package org.ton.tact.lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.tree.IElementType
import org.ton.tact.lang.TactTypes.LBRACE
import org.ton.tact.lang.TactTypes.RBRACE
import org.ton.tact.lang.psi.*

abstract class TactLazyBlockImpl : LazyParseablePsiElement, TactLazyBlock {
    constructor(type: IElementType, text: CharSequence?) : super(type, text)

    constructor(node: ASTNode) : super(node.elementType, node.text)

    override val lbrace: PsiElement?
        get() = findChildByType(LBRACE)?.psi

    override val rbrace: PsiElement?
        get() = findChildByType(RBRACE)?.psi

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement,
    ): Boolean {
        if (this is TactBlock ||
            this is TactIfStatement ||
            this is TactForEachStatement ||
            this is TactUntilStatement ||
            this is TactWhileStatement ||
            this is TactFunctionLiteral
        ) {
            if (!processor.execute(this, state)) {
                return false
            }
        }

        return TactCompositeElementImpl.processDeclarationsDefault(this, processor, state, lastParent, place)
    }

    override fun toString(): String = node.elementType.toString()
}
