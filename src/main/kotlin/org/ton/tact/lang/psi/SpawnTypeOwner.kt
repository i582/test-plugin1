package org.ton.tact.lang.psi

import com.intellij.psi.ResolveState
import org.ton.tact.lang.psi.types.TactTypeEx

interface TactTypeOwner : TactCompositeElement {
    fun getType(context: ResolveState?): TactTypeEx?
}
