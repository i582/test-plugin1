package org.ton.tact.lang

import com.intellij.lang.Language

object TactLanguage : Language("tact") {
    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Tact"
    private fun readResolve(): Any = TactLanguage
}
