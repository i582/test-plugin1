package org.tonstudio.tact.ide.colors

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default

enum class TactColor(readableName: @NlsContexts.AttributeDescriptor String, default: TextAttributesKey? = null) {
    KEYWORD("Keywords//Keyword", Default.KEYWORD),

    // Declarations
    PUBLIC_FUNCTION("Functions//Public function", Default.FUNCTION_DECLARATION),
    PUBLIC_NATIVE_FUNCTION("Functions//Public native function", Default.FUNCTION_DECLARATION),
    PUBLIC_ASM_FUNCTION("Functions//Public asm function", Default.FUNCTION_DECLARATION),
    PUBLIC_STRUCT("Types//Public struct", Default.CLASS_NAME),
    PUBLIC_TRAIT("Types//Public trait", Default.CLASS_NAME),
    PUBLIC_MESSAGE("Types//Public message", Default.CLASS_NAME),
    PRIMITIVE("Types//Primitive", Default.CLASS_NAME),

    PUBLIC_FIELD("Fields//Public field", Default.INSTANCE_FIELD),

    PUBLIC_CONSTANT("Constants//Public constant", Default.CONSTANT),

    // Variable like
    VARIABLE("Variables//Variable", Default.LOCAL_VARIABLE),
    PARAMETER("Variables//Parameter", Default.PARAMETER),

    // Types
    BUILTIN_TYPE("Types//Builtin type", Default.KEYWORD),

    // Comments
    LINE_COMMENT("Comments//Line comments", Default.LINE_COMMENT),
    BLOCK_COMMENT("Comments//Block comments", Default.BLOCK_COMMENT),

    // Literals
    NUMBER("Literals//Number", Default.NUMBER),

    // Strings
    STRING("Literals//Strings//String literals", Default.STRING),
    VALID_STRING_ESCAPE("Literals//Strings//Valid string escape", Default.VALID_STRING_ESCAPE),
    // Literals END

    // Braces and operators
    BRACES("Braces and Operators//Braces", Default.BRACES),
    BRACKETS("Braces and Operators//Brackets", Default.BRACKETS),
    OPERATOR("Braces and Operators//Operators", Default.OPERATION_SIGN),
    DOT("Braces and Operators//Dot", Default.DOT),
    COMMA("Braces and Operators//Comma", Default.COMMA),
    PARENTHESES("Braces and Operators//Parentheses", Default.PARENTHESES),
    NOT_NULL_OPERATOR("Braces and Operators//!! operators", Default.KEYWORD),

    // Attributes
    ATTRIBUTE("Attributes//Attribute", Default.METADATA),

    // Docs
    DOC_COMMENT("Doc//Comment", Default.DOC_COMMENT),
    DOC_HEADING("Doc//Heading", Default.DOC_COMMENT_TAG),
    DOC_LINK("Doc//Link", Default.DOC_COMMENT_TAG_VALUE),
    DOC_EMPHASIS("Doc//Italic"),
    DOC_STRONG("Doc//Bold"),
    DOC_CODE("Doc//Code", Default.DOC_COMMENT_MARKUP),
    COMMENT_REFERENCE("Doc//Comment reference", Default.DOC_COMMENT),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("org.tonstudio.tact.$name", default)
    val attributesDescriptor = AttributesDescriptor(readableName, textAttributesKey)
    val testSeverity: HighlightSeverity = HighlightSeverity(name, HighlightSeverity.INFORMATION.myVal)
}
