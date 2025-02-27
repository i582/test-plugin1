package org.tonstudio.tact.lang.psi

interface TactScopeHolder : TactCompositeElement {
    fun scope(): TactScope
}
