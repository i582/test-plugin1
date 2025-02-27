package org.tonstudio.tact.lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.tonstudio.tact.lang.psi.TactScope
import org.tonstudio.tact.lang.psi.TactScopeHolder
import org.tonstudio.tact.lang.stubs.TactFunctionDeclarationStub

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
