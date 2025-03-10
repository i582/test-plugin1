package org.tonstudio.tact.ide.documentation

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asKeyword
import org.tonstudio.tact.ide.documentation.DocumentationUtils.line
import org.tonstudio.tact.ide.documentation.DocumentationUtils.part
import org.tonstudio.tact.ide.documentation.DocumentationUtils.appendNotNull
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asAttribute
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asParameter
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asParen
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asType
import org.tonstudio.tact.ide.documentation.DocumentationUtils.colorize
import org.tonstudio.tact.lang.psi.*
import org.tonstudio.tact.lang.psi.types.*
import org.tonstudio.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import kotlin.math.max
import io.ktor.util.*
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asBuiltin
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asFunction
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asMessage
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asNativeFunction
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asNumber
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asOperator
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asString
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asStruct
import org.tonstudio.tact.ide.documentation.DocumentationUtils.asTrait
import org.tonstudio.tact.lang.TactSyntaxHighlighter

fun TactFunctionDeclaration.generateDoc(): String {
    val parameters = getSignature().parameters
    val returnType = getSignature().result

    return buildString {
        append(DocumentationMarkup.DEFINITION_START)
        line(attributes?.generateDoc())

        append(functionAttributeList.generateDoc())

        part("fun", asKeyword)
        colorize(name, asFunction)

        append(parameters.generateDoc())
        if (returnType != null) {
            append(": ")
            appendNotNull(returnType.generateDoc())
        }

        append(DocumentationMarkup.DEFINITION_END)
//        generateCommentsPart(this@generateDoc)
    }
}

fun TactNativeFunctionDeclaration.generateDoc(): String? {
    val signature = getSignature() ?: return null
    val parameters = signature.parameters
    val returnType = signature.result

    return buildString {
        append(DocumentationMarkup.DEFINITION_START)
        line(attributes?.generateDoc())

        append(functionAttributeList.generateDoc())

        part("native", asKeyword)
        colorize(name ?: "", asNativeFunction)

        append(parameters.generateDoc())
        if (returnType != null) {
            append(": ")
            appendNotNull(returnType.generateDoc())
        }

        append(DocumentationMarkup.DEFINITION_END)
//        generateCommentsPart(this@generateDoc)
    }
}

private fun List<TactFunctionAttribute>.generateDoc(): String {
    return buildString {
        this@generateDoc.forEach {
            part(it.text, asKeyword)
        }
    }
}

private fun TactResult.generateDoc(): String {
    val type = type.toEx()
    return type.generateDoc(this)
}

private fun TactParameters.generateDoc(): String {
    val params = paramDefinitionList

    if (params.isEmpty()) {
        return buildString { colorize("()", asParen) }
    }

    if (params.size == 1) {
        val param = params.first()
        return buildString {
            colorize("(", asParen)
            append("    ")
            append(param.generateDocForMethod())
            colorize(")", asParen)
        }
    }

    val paramNameMaxWidth = params.maxOfOrNull { it.name?.length ?: 0 } ?: 0

    return buildString {
        colorize("(", asParen)
        append("\n")
        append(
            params.joinToString(",\n") { param ->
                buildString {
                    val name = param.name
                    if (name != null) {
                        colorize(name, asParameter)
                    }
                    append(": ")
                    val nameLength = name?.length ?: 0
                    append("".padEnd(max(paramNameMaxWidth - nameLength, 0)))
                    append(param.type.toEx().generateDoc(this@generateDoc))
                }
            } + ","
        )
        append("\n")
        colorize(")", asParen)
    }
}

private fun TactParamDefinition.generateDocForMethod(): String {
    return buildString {
        val name = name
        if (name != null) {
            if (name == "self") {
                colorize(name, asKeyword)
            } else {
                colorize(name, asParameter)
            }
            append(": ")
        }
        append(type.toEx().generateDoc(this@generateDocForMethod))
    }
}

private fun TactAttributes.generateDoc(): String {
    return attributeList.joinToString("\n") { attr ->
        attr.generateDoc()
    }
}

private fun TactAttribute.generateDoc(): String {
    return colorize("@", asAttribute) + (attributeExpression?.text ?: "")
}

fun TactExpression.generateDoc(): String {
    val text = text
    val highlighter = TactSyntaxHighlighter()
    val lexer = highlighter.highlightingLexer
    val builder = StringBuilder()
    lexer.start(text)
    while (lexer.tokenType != null) {
        val type = lexer.tokenType
        val tokenText = lexer.tokenText
        val keyword = TactTokenTypes.KEYWORDS.contains(type)
        val number = TactTokenTypes.NUMBERS.contains(type)
        val string = TactTokenTypes.STRING_LITERALS.contains(type)
        val operators = TactTokenTypes.OPERATORS.contains(type)
        val booleanLiteral = tokenText == "true" || tokenText == "false"
        val primitiveType = TactPrimitiveTypes.isPrimitiveType(tokenText)

        if (tokenText.contains("\n")) {
            builder.append("...")
            break
        }

        builder.append(
            when {
                keyword        -> colorize(tokenText, asKeyword)
                number         -> colorize(tokenText, asNumber)
                string         -> colorize(tokenText, asString)
                operators      -> colorize(tokenText, asOperator)
                booleanLiteral -> colorize(tokenText, asKeyword)
                primitiveType  -> colorize(tokenText, asBuiltin)
                else           -> tokenText
            }
        )
        lexer.advance()
    }

    return builder.toString()
}

fun TactTypeEx.generateDoc(anchor: PsiElement): String {
    when (this) {
        is TactMapTypeEx       -> return this.generateDoc(anchor)
        is TactOptionTypeEx    -> return this.generateDoc(anchor)
        is TactStructTypeEx    -> return this.generateDoc(anchor)
        is TactMessageTypeEx   -> return this.generateDoc(anchor)
        is TactTraitTypeEx     -> return this.generateDoc(anchor)
        is TactFunctionTypeEx  -> return this.generateDoc(anchor)
        is TactTupleTypeEx     -> return this.generateDoc(anchor)
        is TactPrimitiveTypeEx -> return this.generateDoc(anchor)
    }
    return colorize(this.readableName(anchor).escapeHTML(), asType)
}

fun TactStructTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        append(generateFqnTypeDoc(readableName(anchor), asStruct))
    }
}

fun TactTraitTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        append(generateFqnTypeDoc(readableName(anchor), asTrait))
    }
}

fun TactMessageTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        append(generateFqnTypeDoc(readableName(anchor), asMessage))
    }
}

fun TactMapTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        colorize("map", asKeyword)
        append("<")
        appendNotNull(key.generateDoc(anchor))
        append(", ")
        appendNotNull(value.generateDoc(anchor))
        append(">")
    }
}

fun TactOptionTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        appendNotNull(inner.generateDoc(anchor))
        append("?")
    }
}

fun TactFunctionTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        colorize("fun", asKeyword)
        append(" (")
        params.forEachIndexed { index, param ->
            if (index > 0) {
                append(", ")
            }
            appendNotNull(param.generateDoc(anchor))
        }
        colorize(")", asParen)
        if (result != null) {
            append(": ")
            appendNotNull(result.generateDoc(anchor))
        }
    }
}

fun TactTupleTypeEx.generateDoc(anchor: PsiElement): String {
    return buildString {
        colorize("(", asParen)
        types.forEachIndexed { index, param ->
            if (index > 0) {
                append(", ")
            }
            appendNotNull(param.generateDoc(anchor))
        }
        colorize(")", asParen)
    }
}

fun TactPrimitiveTypeEx.generateDoc(anchor: PsiElement): String {
    return colorize(readableName(anchor), asBuiltin)
}

private fun generateFqnTypeDoc(fqn: String, color: TextAttributes): String {
    val parts = fqn.split(".")
    if (parts.size == 1) {
        return colorize(parts[0], color)
    }

    return parts.subList(0, parts.size - 1).joinToString(".") {
        it
    } + "." + colorize(parts.last(), color)
}
