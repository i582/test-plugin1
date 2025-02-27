package org.ton.tact.lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.ton.tact.lang.psi.TactScope
import org.ton.tact.lang.psi.TactScopeHolder
import org.ton.tact.lang.stubs.TactFunctionDeclarationStub

abstract class TactFunctionDeclarationWithScopeHolder : TactFunctionOrMethodDeclarationImpl<TactFunctionDeclarationStub>, TactScopeHolder {
    private var scope: TactScope? = null

    constructor(stub: TactFunctionDeclarationStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)
    constructor(node: ASTNode) : super(node)

    override fun scope(): TactScope {
        if (scope == null) {
            scope = TactScopeImpl(this)
        }

        return scope!!
    }
}
