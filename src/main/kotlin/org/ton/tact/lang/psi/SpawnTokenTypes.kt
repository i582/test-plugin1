package org.ton.tact.lang.psi

import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.ton.tact.lang.TactTypes.*

object TactTokenTypes {
    @JvmField val LINE_COMMENT = TactTokenType("SPAWN_LINE_COMMENT")
    @JvmField val MULTI_LINE_COMMENT = TactTokenType("SPAWN_MULTI_LINE_COMMENT")

    @JvmField val WS = TactTokenType("SPAWN_WHITESPACE")
    @JvmField val NLS = TactTokenType("SPAWN_WS_NEW_LINES")

    val IDENTIFIERS = TokenSet.create(IDENTIFIER)
    val COMMENTS = TokenSet.create(LINE_COMMENT, MULTI_LINE_COMMENT, TactDocElementTypes.DOC_COMMENT)
    val STRING_LITERALS = TokenSet.create(
        RAW_STRING,
        CHAR,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        BACKTICK,
        OPEN_QUOTE,
        CLOSING_QUOTE,
        LITERAL_STRING_TEMPLATE_ENTRY,
        C_STRING,
    )
    val NUMBERS = TokenSet.create(
        INT,
        FLOAT,
        HEX,
        OCT,
        BIN
    )
    val BOOL_LITERALS = TokenSet.create(TRUE, FALSE)

    val KEYWORDS = TokenSet.create(
        BREAK,
        CONST,
        CONTINUE,
        DEFER,
        ELSE,
        MAP,
        FOR,
        FUN,
        GO,
        GOTO,
        IF,
        FOREACH,
        WHILE,
        UNTIL,
        DO,
        REPEAT,
        IMPORT,
        INTERFACE,
        IN,
        NOT_IN,
        RETURN,
        SELECT,
        STRUCT,
        TYPE_,
        PUB,
        LET,
        MUT,
        IF_COMPILE_TIME,
        ELSE_COMPILE_TIME,
        ASSERT,
        ENUM,
        MATCH,
        NOT_IS,
        FOR_COMPILE_TIME,
        UNION,
        ASM,
        NULL,
        SPAWN,
        COMPTIME,
        VAR,
        TEST,
        CONTRACT,
        TRAIT,
        WITH,
        INIT,
        RECEIVE,
        EXTERNAL,
        VIRTUAL,
        OVERRIDE,
        ABSTRACT,
        MESSAGE,
        PRIMITIVE,
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
        VAR_ASSIGN,
        TILDA,
    )

    val STRING_INTERPOLATION = TokenSet.create(
        LONG_TEMPLATE_ENTRY_START,
        TEMPLATE_ENTRY_END,
        TEMPLATE_ENTRY_START,
    )

    val WHITE_SPACES = TokenSet.create(WS, NLS, TokenType.WHITE_SPACE)
}
