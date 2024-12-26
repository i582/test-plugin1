package org.ton.tact.lang.psi

interface TactSignatureOwner : TactCompositeElement {
    fun getSignature(): TactSignature?
}

fun TactSignatureOwner.getBlockIfAny(): TactBlock? {
    return when (this) {
        is TactFunctionLiteral             -> this.getBlock()
        is TactFunctionOrMethodDeclaration -> this.getBlock()
        else                                -> null
    }
}
