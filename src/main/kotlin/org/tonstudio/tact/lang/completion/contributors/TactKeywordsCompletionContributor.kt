package org.tonstudio.tact.lang.completion.contributors

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.template.impl.ConstantNode
import com.intellij.psi.util.parentOfTypes
import com.intellij.util.ProcessingContext
import org.tonstudio.tact.ide.ui.Icons
import org.tonstudio.tact.lang.completion.TactCompletionPatterns.onExpression
import org.tonstudio.tact.lang.completion.TactCompletionPatterns.onIfElse
import org.tonstudio.tact.lang.completion.TactCompletionPatterns.onStatement
import org.tonstudio.tact.lang.completion.TactCompletionPatterns.onTopLevel
import org.tonstudio.tact.lang.completion.TactCompletionUtil.KEYWORD_PRIORITY
import org.tonstudio.tact.lang.completion.TactCompletionUtil.StringInsertHandler
import org.tonstudio.tact.lang.completion.TactCompletionUtil.TemplateStringInsertHandler
import org.tonstudio.tact.lang.completion.TactCompletionUtil.showCompletion
import org.tonstudio.tact.lang.completion.TactCompletionUtil.toTactLookupElement
import org.tonstudio.tact.lang.completion.TactCompletionUtil.withPriority
import org.tonstudio.tact.lang.completion.TactLookupElementProperties
import org.tonstudio.tact.lang.completion.sort.withTactSorter
import org.tonstudio.tact.lang.psi.TactContractDeclaration
import org.tonstudio.tact.lang.psi.TactTraitDeclaration

class TactKeywordsCompletionContributor : CompletionContributor() {
    init {
        // Top Level
        extend(CompletionType.BASIC, onTopLevel(), ConstCompletionProvider)
        extend(CompletionType.BASIC, onTopLevel(), CompletionAfterContextKeywordsCompletionProvider("import"))

        extend(
            CompletionType.BASIC,
            onTopLevel(),
            ContextKeywordsCompletionProvider(
                "fun",
                needSpace = true,
            )
        )
        extend(
            CompletionType.BASIC,
            onTopLevel(),
            ClassLikeSymbolCompletionProvider(
                "struct",
                "trait",
                "message",
                "contract",
            )
        )

        extend(
            CompletionType.BASIC,
            onStatement(),
            ConditionBlockKeywordCompletionProvider("if")
        )
        extend(
            CompletionType.BASIC,
            onExpression(),
            KeywordsCompletionProvider(
                "null",
                "true",
                "false",
                needSpace = false,
            )
        )

        extend(
            CompletionType.BASIC,
            onExpression(),
            CellCompletionProvider,
        )

        extend(
            CompletionType.BASIC,
            onExpression(),
            SelfCompletionProvider,
        )

        // Other
        extend(
            CompletionType.BASIC,
            onIfElse(),
            ElseIfKeywordCompletionProvider(),
        )
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, withTactSorter(parameters, result))
    }

    private val elseElement = LookupElementBuilder.create("else")
        .bold()
        .withTailText(" {...}")
        .withInsertHandler(StringInsertHandler(" {  }", 3))
        .withPriority(KEYWORD_PRIORITY)

    private object ConstCompletionProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val constElement = LookupElementBuilder.create("const")
                .withTailText(" name: Type = value")
                .withInsertHandler(
                    TemplateStringInsertHandler(
                        " \$name$: \$type$ = \$value$", true,
                        "name" to ConstantNode("name"),
                        "type" to ConstantNode("Int"),
                        "value" to ConstantNode("0"),
                    )
                )
                .bold()
                .withPriority(KEYWORD_PRIORITY)

            result.addElement(constElement)
        }
    }

    private object CellCompletionProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val constElement = LookupElementBuilder.create("")
                .withLookupString("cell")
                .withPresentableText("cell")
                .withTailText(" create new cell")
                .withInsertHandler(
                    TemplateStringInsertHandler(
                        "beginCell()\$code$.endCell()", true,
                        "code" to ConstantNode(""),
                    )
                )
                .bold()
                .withPriority(KEYWORD_PRIORITY)

            result.addElement(constElement)
        }
    }

    private object SelfCompletionProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val owner = parameters.position.parentOfTypes(TactTraitDeclaration::class, TactContractDeclaration::class) ?: return

            val icon = if (owner is TactTraitDeclaration) Icons.Trait else Icons.Contract

            val constElement = LookupElementBuilder.create("self")
                .withTypeText(owner.getType(null)?.qualifiedName() ?: "")
                .withIcon(icon)
                .bold()
                .withPriority(KEYWORD_PRIORITY)

            result.addElement(constElement)
        }
    }

    private inner class ElseIfKeywordCompletionProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val elseIfElement = LookupElementBuilder.create("else if")
                .bold()
                .withTailText(" expr {...}")
                .withInsertHandler(
                    TemplateStringInsertHandler(" \$expr$ { \$END$ }", true, "expr" to ConstantNode("expr"))
                )
                .withPriority(KEYWORD_PRIORITY)

            result.addElement(elseElement)
            result.addElement(elseIfElement)
        }
    }

    open class ContextKeywordsCompletionProvider(
        vararg keywords: String,
        needSpace: Boolean = false,
    ) : KeywordsCompletionProvider(
        *keywords,
        needSpace = needSpace,
        properties = TactLookupElementProperties(
            isContextElement = true,
        ),
    )

    open class KeywordsCompletionProvider(
        private vararg val keywords: String,
        private val needSpace: Boolean = false,
        private val properties: TactLookupElementProperties = TactLookupElementProperties(),
    ) : CompletionProvider<CompletionParameters>() {

        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet,
        ) {
            val elements = keywords.map { keyword ->
                LookupElementBuilder.create(keyword)
                    .withInsertHandler(StringInsertHandler(" ", 1, needSpace))
                    .bold()
                    .withPriority(KEYWORD_PRIORITY)
                    .toTactLookupElement(properties)
            }
            result.addAllElements(elements)
        }
    }

    private inner class ConditionBlockKeywordCompletionProvider(private vararg val keywords: String) :
        CompletionProvider<CompletionParameters>() {

        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val elements = keywords.map {
                LookupElementBuilder.create(it)
                    .withTailText(" expr {...}")
                    .withInsertHandler(
                        TemplateStringInsertHandler(
                            " \$expr$ {\n\$END$\n}", true,
                            "expr" to ConstantNode("expr")
                        )
                    )
                    .bold()
                    .withPriority(KEYWORD_PRIORITY)
            }

            result.addAllElements(elements)
        }
    }

    private inner class ClassLikeSymbolCompletionProvider(private vararg val keywords: String) :
        CompletionProvider<CompletionParameters>() {

        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val elements = keywords.map { keyword ->
                LookupElementBuilder.create(keyword)
                    .withTailText(" Name {...}")
                    .withInsertHandler(
                        TemplateStringInsertHandler(
                            " \$name$ {\n\$END$\n}", true,
                            "name" to ConstantNode("Name")
                        )
                    )
                    .bold()
                    .withPriority(KEYWORD_PRIORITY)
                    .toTactLookupElement(
                        TactLookupElementProperties(
                            isContextElement = true,
                        )
                    )
            }

            result.addAllElements(elements)
        }
    }

    class CompletionAfterContextKeywordsCompletionProvider(vararg keywords: String) :
        CompletionAfterKeywordsCompletionProvider(*keywords, properties = TactLookupElementProperties(isContextElement = true))

    open class CompletionAfterKeywordsCompletionProvider(
        private vararg val keywords: String,
        private val properties: TactLookupElementProperties = TactLookupElementProperties(),
    ) :
        CompletionProvider<CompletionParameters>() {

        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val elements = keywords.map { keyword ->
                LookupElementBuilder.create(keyword)
                    .withInsertHandler { ctx, item ->
                        StringInsertHandler(" ", 1).handleInsert(ctx, item)
                        showCompletion(ctx.editor)
                    }
                    .bold()
                    .withPriority(KEYWORD_PRIORITY)
                    .toTactLookupElement(properties)
            }

            result.addAllElements(elements)
        }
    }
}
