package org.ton.tact.lang.psi

interface TactScopeHolder : TactCompositeElement {
    fun scope(): TactScope
}
