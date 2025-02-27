package org.tonstudio.tact.lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveState
import org.tonstudio.tact.lang.psi.TactBinaryExpr
import org.tonstudio.tact.lang.psi.TactTypeOwner
import org.tonstudio.tact.lang.psi.types.TactTypeEx

abstract class TactBinaryOpImplMixin(node: ASTNode) : TactCompositeElementImpl(node), TactTypeOwner, TactBinaryExpr {
    override fun getReference(): PsiReference = TactOperatorReference(this)

    override fun getType(context: ResolveState?): TactTypeEx? {
        return TactPsiImplUtil.getType(this, context)
    }
}
