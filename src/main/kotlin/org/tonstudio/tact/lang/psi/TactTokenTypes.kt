package org.tonstudio.tact.lang.psi

import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.tonstudio.tact.lang.TactTypes.*

object TactTokenTypes {
    @JvmField val LINE_COMMENT = TactTokenType("TACT_LINE_COMMENT")
    @JvmField val MULTI_LINE_COMMENT = TactTokenType("TACT_MULTI_LINE_COMMENT")

    @JvmField val WS = TactTokenType("TACT_WHITESPACE")
    @JvmField val NLS = TactTokenType("TACT_WS_NEW_LINES")

    val COMMENTS = TokenSet.create(LINE_COMMENT, MULTI_LINE_COMMENT, TactDocElementTypes.DOC_COMMENT)
    val STRING_LITERALS = TokenSet.create(
        CHAR,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        STRING_ENTRY,
        CLOSING_QUOTE,
        BACKTICK,
        OPEN_QUOTE,
    )
    val NUMBERS = TokenSet.create(
        INT,
        HEX,
        OCT,
        BIN
    )
    val BOOL_LITERALS = TokenSet.create(TRUE, FALSE)

    val KEYWORDS = TokenSet.create(
        CONST,
        ELSE,
        MAP,
        FOR,
        FUN,
        IF,
        FOREACH,
        WHILE,
        UNTIL,
        DO,
        REPEAT,
        IMPORT,
        IN,
        RETURN,
        STRUCT,
        TYPE_,
        PUB,
        LET,
        ENUM,
        ASM,
        NULL,
        CONTRACT,
        TRAIT,
        WITH,
        RECEIVE,
        EXTERNAL,
        VIRTUAL,
        OVERRIDE,
        ABSTRACT,
        MESSAGE,
        PRIMITIVE,
        EXTENDS,
        NATIVE,
        MUTATES,
        INLINE,
        AS,
    )

    val OPERATORS = TokenSet.create(
        EQ,
        ASSIGN,
        NOT_EQ,
        NOT,
        PLUS_PLUS,
        PLUS_ASSIGN,
        PLUS,
        MINUS_MINUS,
        MINUS_ASSIGN,
        MINUS,
        COND_OR,
        BIT_OR_ASSIGN,
        BIT_OR,
        BIT_CLEAR_ASSIGN,
        BIT_CLEAR,
        COND_AND,
        BIT_AND_ASSIGN,
        BIT_AND,
        SHIFT_LEFT_ASSIGN,
        SHIFT_LEFT,
        SEND_CHANNEL,
        LESS_OR_EQUAL,
        LESS,
        BIT_XOR_ASSIGN,
        BIT_XOR,
        MUL_ASSIGN,
        MUL,
        QUOTIENT_ASSIGN,
        QUOTIENT,
        REMAINDER_ASSIGN,
        REMAINDER,
        SHIFT_RIGHT_ASSIGN,
        SHIFT_RIGHT,
        GREATER_OR_EQUAL,
        GREATER,
    )

    val WHITE_SPACES = TokenSet.create(WS, NLS, TokenType.WHITE_SPACE)
}
