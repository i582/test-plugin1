package org.tonstudio.tact.ide.documentation

import com.intellij.lang.documentation.DocumentationSettings
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import io.ktor.util.*
import org.tonstudio.tact.ide.colors.TactColor

object DocumentationUtils {
    private fun loadKey(key: TextAttributesKey): TextAttributes =
        EditorColorsManager.getInstance().globalScheme.getAttributes(key)!!

    val asOperator = loadKey(TactColor.OPERATOR.textAttributesKey)
    val asKeyword = loadKey(TactColor.KEYWORD.textAttributesKey)
    val asIdentifier = loadKey(DefaultLanguageHighlighterColors.IDENTIFIER)
    val asBuiltin = loadKey(TactColor.BUILTIN_TYPE.textAttributesKey)
    val asType = loadKey(TactColor.BUILTIN_TYPE.textAttributesKey)
    val asParameter = loadKey(TactColor.PARAMETER.textAttributesKey)
    val asAttribute = loadKey(TactColor.ATTRIBUTE.textAttributesKey)
    val asString = loadKey(TactColor.STRING.textAttributesKey)
    val asNumber = loadKey(TactColor.NUMBER.textAttributesKey)
    val asField = loadKey(TactColor.PUBLIC_FIELD.textAttributesKey)
    val asComment = loadKey(TactColor.LINE_COMMENT.textAttributesKey)
    val asParen = loadKey(TactColor.PARENTHESES.textAttributesKey)
    val asBraces = loadKey(TactColor.BRACES.textAttributesKey)
    val asBrackets = loadKey(TactColor.BRACKETS.textAttributesKey)

    val asFunction = loadKey(TactColor.PUBLIC_FUNCTION.textAttributesKey)
    val asNativeFunction = loadKey(TactColor.PUBLIC_NATIVE_FUNCTION.textAttributesKey)
    val asAsmFunction = loadKey(TactColor.PUBLIC_ASM_FUNCTION.textAttributesKey)
    val asStruct = loadKey(TactColor.PUBLIC_STRUCT.textAttributesKey)
    val asMessage = loadKey(TactColor.PUBLIC_MESSAGE.textAttributesKey)
    val asTrait = loadKey(TactColor.PUBLIC_TRAIT.textAttributesKey)
    val asConst = loadKey(TactColor.PUBLIC_CONSTANT.textAttributesKey)

    @Suppress("UnstableApiUsage")
    fun StringBuilder.colorize(text: String, attrs: TextAttributes, noHtml: Boolean = false) {
        if (noHtml) {
            append(text)
            return
        }

        HtmlSyntaxInfoUtil.appendStyledSpan(
            this, attrs, text.escapeHTML(),
            DocumentationSettings.getHighlightingSaturation(false)
        )
    }

    fun StringBuilder.part(text: String?) {
        if (text.isNullOrEmpty()) {
            return
        }
        append(text)
        append(" ")
    }

    fun StringBuilder.part(text: String?, attrs: TextAttributes) {
        if (text == null) {
            return
        }
        colorize(text, attrs)
        append(" ")
    }

    fun StringBuilder.appendNotNull(text: String?) {
        if (text == null) {
            return
        }
        append(text)
    }

    fun StringBuilder.line(text: String?) {
        if (text == null) {
            return
        }
        append(text)
        append("\n")
    }

    fun StringBuilder.monospaced(text: String, attrs: TextAttributes? = null, noHtml: Boolean = false) {
        append("<code>")
        if (attrs != null) {
            colorize(text, attrs, noHtml)
        } else {
            append(text)
        }
        append("</code>")
    }

    fun colorize(text: String, attrs: TextAttributes, noHtml: Boolean = false): String {
        val sb = StringBuilder()
        sb.colorize(text, attrs, noHtml)
        return sb.toString()
    }
}
