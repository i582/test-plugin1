package org.ton.tact.lang.psi

interface TactMutabilityOwner : TactCompositeElement {
    fun isMutable(): Boolean
    fun makeMutable()
    fun makeImmutable()
}
