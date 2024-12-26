package org.ton.tact.lang.psi

import com.intellij.psi.tree.IElementType
import org.ton.tact.lang.TactLanguage

open class TactTokenType(debugName: String) : IElementType(debugName, TactLanguage) {
    override fun toString() = "TactTokenType." + super.toString()
}
