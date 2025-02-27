package org.ton.tact.lang

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.openapi.util.Key
import org.ton.tact.lang.TactTypes.*
import java.util.*

object TactParserUtil : GeneratedParserUtilBase() {
    private val MODES_LIST_KEY = Key.create<Stack<String>>("MODES_KEY")

    private fun getParsingModesStack(builder: PsiBuilder): Stack<String> {
        var flags = builder.getUserData(MODES_LIST_KEY)
        if (flags == null) {
            flags = Stack<String>()
            flags.push("DEFAULT_MODE")
            builder.putUserData(MODES_LIST_KEY, flags)
        }
        return flags
    }

    @JvmStatic
    fun isLastIs(builder: PsiBuilder, mode: String?): Boolean {
        return getParsingModesStack(builder).peek() == mode
    }

    @JvmStatic
    fun keyOrValueExpression(builder: PsiBuilder, level: Int): Boolean {
        val m = enter_section_(builder)
        var r = TactParser.Expression(builder, level + 1, -1)

        if (!r) {
            r = TactParser.LiteralValueExpression(builder, level + 1)
        }

        val type = if (r && builder.tokenType === COLON) KEY else VALUE
        exit_section_(builder, m, type, r)
        return r
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun braceRuleMarker(builder: PsiBuilder, level: Int): Boolean {
        return !isLastIs(builder, "noBraces")
    }
}