package org.ton.tact.lang

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.ton.tact.ide.colors.TactColor
import org.ton.tact.lang.TactTypes.*
import org.ton.tact.lang.psi.TactDocElementTypes.DOC_COMMENT
import org.ton.tact.lang.psi.TactTokenTypes.BOOL_LITERALS
import org.ton.tact.lang.psi.TactTokenTypes.COMMENTS
import org.ton.tact.lang.psi.TactTokenTypes.KEYWORDS
import org.ton.tact.lang.psi.TactTokenTypes.NUMBERS
import org.ton.tact.lang.psi.TactTokenTypes.OPERATORS
import org.ton.tact.lang.psi.TactTokenTypes.STRING_LITERALS

class TactSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer() = TactHighlightingLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType): TactColor? = when (tokenType) {
            LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY -> TactColor.VALID_STRING_ESCAPE
            DOC_COMMENT                          -> TactColor.LINE_COMMENT

            LPAREN, RPAREN                       -> TactColor.PARENTHESES
            LBRACE, RBRACE                       -> TactColor.BRACES
            LBRACK, RBRACK                       -> TactColor.BRACKETS

            DOT                                  -> TactColor.DOT
            COMMA                                -> TactColor.COMMA

            LONG_TEMPLATE_ENTRY_START            -> TactColor.STRING_INTERPOLATION
            TEMPLATE_ENTRY_END                   -> TactColor.STRING_INTERPOLATION

            CHAR                                 -> TactColor.CHAR

            RAW_STRING                           -> TactColor.RAW_STRING
            C_STRING                             -> TactColor.C_STRING

            in KEYWORDS                          -> TactColor.KEYWORD
            in BOOL_LITERALS                     -> TactColor.KEYWORD
            in STRING_LITERALS                   -> TactColor.STRING
            in NUMBERS                           -> TactColor.NUMBER
            in OPERATORS                         -> TactColor.OPERATOR
            in COMMENTS                          -> TactColor.LINE_COMMENT

            else                                 -> null
        }
    }
}
