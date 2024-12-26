package org.ton.tact.lang

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.openapi.util.Key
import gnu.trove.TObjectIntHashMap
import org.ton.tact.lang.TactTypes.*
import org.ton.tact.lang.psi.TactTokenTypes
import java.util.*

@Suppress("DEPRECATION")
object TactParserUtil : GeneratedParserUtilBase() {
    private val MODES_KEY = Key.create<TObjectIntHashMap<String>>("MODES_KEY")
    private val MODES_LIST_KEY = Key.create<Stack<String>>("MODES_KEY")

    private fun getParsingModes(builder: PsiBuilder): TObjectIntHashMap<String> {
        var flags = builder.getUserData(MODES_KEY)
        if (flags == null) {
            flags = TObjectIntHashMap<String>()
            builder.putUserData(MODES_KEY, flags)
        }
        return flags
    }

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
    fun withOn(builder: PsiBuilder, level: Int, mode: String, parser: Parser): Boolean {
        val nowStack = getParsingModesStack(builder)
        val prevStack = Stack<String>()
        prevStack.addAll(nowStack)

        enterMode(builder, level, mode)
        val result = parser.parse(builder, level)
        exitMode(builder, level, mode)

        nowStack.clear()
        nowStack.addAll(prevStack)

        return result
    }

    @JvmStatic
    fun isLastIs(builder: PsiBuilder, mode: String?): Boolean {
        return getParsingModesStack(builder).peek() == mode
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun prevIsNotFunType(builder: PsiBuilder, level: Int): Boolean {
        val marker = builder.latestDoneMarker
        val type = marker?.tokenType
        return type !== FUNCTION_TYPE
    }

    @JvmStatic
    fun keyOrValueExpression(builder: PsiBuilder, level: Int): Boolean {
        val m = enter_section_(builder)
        var r = TactParser.Expression(builder, level + 1, -1)

        if (!r) {
            r = TactParser.LiteralValueExpression(builder, level + 1)
        }

        val type = if (r && builder.tokenType === COLON)
            KEY
        else
            VALUE
        exit_section_(builder, m, type, r)
        return r
    }

    private val identifierRegex = Regex("[a-zA-Z0-9_.]*")

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun checkNoColonIfMap(builder: PsiBuilder, level: Int): Boolean {
        if (!isLastIs(builder, "MAP_KEY_VALUE") || builder.latestDoneMarker?.tokenType == IDENTIFIER) {
            return true
        }

        val text = builder.originalText.substring(builder.latestDoneMarker?.startOffset ?: 0, builder.latestDoneMarker?.endOffset ?: 1)
        if (identifierRegex.matches(text)) {
            return true
        }

        return !consumeToken(builder, COLON)
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun braceRuleMarker(builder: PsiBuilder, level: Int): Boolean {
        return !isLastIs(builder, "noBraces")
    }

    @JvmStatic
    fun beforeBlockExpression(builder: PsiBuilder, level: Int): Boolean {
        val m = builder.mark()
        val r = TactParser.Expression(builder, level + 1, -1)
        if (r && nextTokenIs(builder, LBRACE)) {
            // Значит парсинг можно продолжать
            m.drop()
            return true
        }

        m.rollbackTo()
        (builder as Builder).state.currentFrame.errorReportedAt = -1

        // Otherwise, we try to parse, but without following the rules, that end in {}
        enterMode(builder, level, "noBraces")
        val r1 = TactParser.Expression(builder, level + 1, -1)
        exitMode(builder, level, "noBraces")
        return r1
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun enterMode(builder: PsiBuilder, level: Int, mode: String?): Boolean {
        val flags = getParsingModes(builder)
        if (!flags.increment(mode)) {
            flags.put(mode, 1)
        }
        val stack = getParsingModesStack(builder)
        stack.push(mode)
        return true
    }

    private fun exitMode(builder: PsiBuilder, mode: String, safe: Boolean, all: Boolean = false): Boolean {
        val flags = getParsingModes(builder)
        val count = flags[mode]
        if (count == 1) {
            flags.remove(mode)
        } else if (count > 1) {
            if (all) {
                flags.remove(mode)
            } else {
                flags.put(mode, count - 1)
            }
        } else if (!safe) {
            builder.error("Could not exit inactive '" + mode + "' mode at offset " + builder.currentOffset)
            return false
        }

        val stack = getParsingModesStack(builder)
        if (stack.isEmpty() && !safe) {
            builder.error("Could not exit '" + mode + "' mode at offset " + builder.currentOffset + ": stack is empty")
            return true
        }

        val top = stack.peek()
        if (top == mode) {
            stack.pop()

            if (all) {
                while (!stack.isEmpty() && stack.peek() == mode) {
                    stack.pop()
                }
            }
        }

        return true
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun exitMode(builder: PsiBuilder, level: Int, mode: String): Boolean {
        return exitMode(builder, mode, safe = false)
    }

    @JvmStatic
    fun prevIsType(builder: PsiBuilder): Boolean {
        var tokenBefore = builder.rawLookup(-1)
        if (tokenBefore == TactTokenTypes.WS) {
            tokenBefore = builder.rawLookup(-2)
        }
        if (tokenBefore == ASSIGN) {
            return false
        }
        val marker = builder.latestDoneMarker
        val type = marker?.tokenType
        return type == MAP_TYPE || type == STRUCT_TYPE
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun prevIsNotType(builder: PsiBuilder, level: Int): Boolean {
        return !prevIsType(builder)
    }
}