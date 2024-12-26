package org.ton.tact.lang.completion.providers

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.ConstantNode
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.TactTypes
import org.ton.tact.lang.completion.TactCompletionUtil
import org.ton.tact.lang.completion.TactCompletionUtil.toTactLookupElement
import org.ton.tact.lang.completion.TactLookupElementProperties
import org.ton.tact.lang.completion.TactStructLiteralCompletion
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.*
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.LOCAL_RESOLVE
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.NAMED_PARAMETER_COMPLETION
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.PROCESS_PRIVATE_MEMBERS
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.SAFE_ACCESS
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.UNWRAPPED_OPTION_METHODS
import org.ton.tact.lang.psi.types.TactTypeEx

object ReferenceCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val element = parameters.position
        val set = TactCompletionUtil.withCamelHumpPrefixMatcher(result)

        val file = element.containingFile as? TactFile ?: return
        val expression = element.parentOfType<TactReferenceExpressionBase>() ?: return
        val ref = expression.reference
        if (ref is TactReference) {
            val refExpression = ref.element as? TactReferenceExpression
            val variants = TactStructLiteralCompletion.allowedVariants(refExpression, element)

            fillStructFieldNameVariants(parameters, set, variants, refExpression)

            if (variants != TactStructLiteralCompletion.Variants.FIELD_NAME_ONLY) {
                ref.processResolveVariants(MyScopeProcessor(parameters, set, ref.forTypes))
            }
        } else if (ref is TactCachedReference<*>) {
            ref.processResolveVariants(MyScopeProcessor(parameters, set, false))
        }
    }

    private fun fillStructFieldNameVariants(
        parameters: CompletionParameters,
        result: CompletionResultSet,
        variants: TactStructLiteralCompletion.Variants,
        refExpression: TactReferenceExpression?,
    ) {
        if (refExpression == null ||
            variants !== TactStructLiteralCompletion.Variants.FIELD_NAME_ONLY &&
            variants !== TactStructLiteralCompletion.Variants.BOTH
        ) {
            return
        }

        val possiblyLiteralValueExpression = refExpression.parentOfType<TactLiteralValueExpression>()
        val file = refExpression.containingFile as? TactFile ?: return

        if (possiblyLiteralValueExpression == null) {
            val argumentList = refExpression.parentOfType<TactArgumentList>()
            val elementList = argumentList?.elementList ?: emptyList()
            val alreadyAssignedFields = TactStructLiteralCompletion.alreadyAssignedFields(elementList)
            val callExpr = argumentList?.parent as? TactCallExpr
            val resolved = callExpr?.resolve() as? TactSignatureOwner
            val params = resolved?.getSignature()?.parameters?.paramDefinitionList

            if (params != null) {
                val processor = MyScopeProcessor(parameters, result, false)
                for (param in params) {
                    val name = param.name
                    if (name != null && !alreadyAssignedFields.contains(name)) {
                        processor.execute(param, ResolveState.initial().put(NAMED_PARAMETER_COMPLETION, true))
                    }
                }
                return
            }
        }

        val fields = mutableSetOf<Pair<String, TactTypeEx?>>()
        val elementList =
            possiblyLiteralValueExpression?.elementList
                ?: refExpression.parentOfType<TactArgumentList>()?.elementList
                ?: emptyList()

        val alreadyAssignedFields = TactStructLiteralCompletion.alreadyAssignedFields(elementList)

        TactFieldNameReference(refExpression).processResolveVariants(object : MyScopeProcessor(parameters, result, false) {
            override fun execute(element: PsiElement, state: ResolveState): Boolean {
                val structFieldName =
                    when (element) {
                        is TactFieldDefinition -> element.name
                        else                   -> null
                    }

                val structFieldType =
                    when (element) {
                        is TactFieldDefinition -> element.getType(null)
                        else                   -> null
                    }

                val containingFile = element.containingFile as? TactFile ?: return true

                // don't add private fields from other modules
//                if (element is TactFieldDefinition && !isLocalResolve(file, containingFile)) {
//                    return true
//                }

                if (structFieldName != null) {
                    fields.add(structFieldName to structFieldType)
                }

                // При инициализации структуры мы можем использовать приватные поля
                val newState = state.put(LOCAL_RESOLVE, true)

                if (structFieldName != null && alreadyAssignedFields.contains(structFieldName)) {
                    return true
                }

                return super.execute(element, newState)
            }
        })

        if (possiblyLiteralValueExpression != null) {
            val remainingFields = fields.filter { !alreadyAssignedFields.contains(it.first) }
            if (remainingFields.size > 1) {
                val element = LookupElementBuilder.create("")
                    .withPresentableText("Fill all fields…")
                    .withIcon(AllIcons.Actions.RealIntentionBulb)
                    .withInsertHandler(StructFieldsInsertHandler(remainingFields))

                result.addElement(element)
            }
        }
    }

    class StructFieldsInsertHandler(private val fields: List<Pair<String, TactTypeEx?>>) : InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val project = context.project
            val offset = context.editor.caretModel.offset
            val element = context.file.findElementAt(offset) ?: return
            val prevElement = element.prevSibling

            val before = if (prevElement.elementType == TactTypes.LBRACE) "\n" else ""
            val after = if (element.elementType == TactTypes.RBRACE) "\n" else ""

            val templateText = fields.joinToString("\n", before, after) {
                it.first + ": \$field_${it.first}$,"
            }

            val template = TemplateManager.getInstance(project)
                .createTemplate("closures", "spawn", templateText)
            template.isToReformat = true

            fields.forEach {
                template.addVariable(
                    "field_${it.first}",
                    ConstantNode(TactLangUtil.getDefaultValue(element, it.second)),
                    true
                )
            }

            TemplateManager.getInstance(project).startTemplate(context.editor, template)
        }
    }

    open class MyScopeProcessor(
        private val parameters: CompletionParameters,
        private val result: CompletionResultSet,
        private val forTypes: Boolean,
    ) : TactScopeProcessor() {

        private val processedNames = mutableSetOf<String>()

        override fun execute(element: PsiElement, state: ResolveState): Boolean {
            if (accept(element, state, forTypes)) {
                addElement(
                    element,
                    state,
                    forTypes,
                    processedNames,
                    result,
                    parameters,
                )
            }
            return true
        }

        private fun accept(e: PsiElement, state: ResolveState, forTypes: Boolean): Boolean {
            if (forTypes) {
                if (e !is TactNamedElement) return false
                if (e.isBlank()) {
                    return false
                }

                // forbid raw map completion
                if (e is TactStructDeclaration && e.name == "map") {
                    return false
                }

                return e is TactStructDeclaration ||
                        e is TactMessageDeclaration ||
                        e is TactContractDeclaration ||
                        e is TactTraitDeclaration
            }

            if (e is TactFile) {
                return true
            }

            if (e is TactNamedElement) {
                if (e.isBlank()) {
                    return false
                }

                // forbid raw map completion
                if (e is TactStructDeclaration && e.name == "map") {
                    return false
                }

                if ((e is TactFieldDefinition) && state.get(PROCESS_PRIVATE_MEMBERS)) {
                    return true
                }

                return true
            }

            return false
        }

        override fun isCompletion(): Boolean = true

        open fun addElement(
            o: PsiElement,
            state: ResolveState,
            forTypes: Boolean,
            processedNames: MutableSet<String>,
            set: CompletionResultSet,
            parameters: CompletionParameters,
        ) {
            val lookup = createLookupElement(o, state, forTypes, parameters)
            if (lookup != null) {
                val key = lookup.lookupString + o.javaClass
                if (!processedNames.contains(key)) {
                    set.addElement(lookup)
                    processedNames.add(key)
                }
            }
        }
    }

    private fun createLookupElement(
        element: PsiElement,
        state: ResolveState,
        forTypes: Boolean,
        parameters: CompletionParameters,
    ): LookupElement? {
        val elementFile = element.containingFile as? TactFile ?: return null
        val context = parameters.position
        val contextFile = context.containingFile as? TactFile ?: return null
        val isSameModule = TactCodeInsightUtil.sameModule(contextFile, elementFile)

        val contextFunction = context.parentOfType<TactFunctionOrMethodDeclaration>()
        val elementFunction = element.parentOfType<TactFunctionOrMethodDeclaration>()
        val isLocal = contextFunction == elementFunction

        val kind = when (element) {
            is TactFunctionDeclaration -> TactLookupElementProperties.ElementKind.FUNCTION
            is TactStructDeclaration   -> TactLookupElementProperties.ElementKind.STRUCT
            is TactTraitDeclaration    -> TactLookupElementProperties.ElementKind.STRUCT // TODO
            is TactMessageDeclaration  -> TactLookupElementProperties.ElementKind.METHOD // TODO
            is TactConstDefinition     -> TactLookupElementProperties.ElementKind.CONSTANT
            is TactContractDeclaration -> TactLookupElementProperties.ElementKind.CONSTANT // TODO
            is TactFieldDefinition     -> TactLookupElementProperties.ElementKind.FIELD
            is TactModuleVarDefinition -> TactLookupElementProperties.ElementKind.MODULE_VAR
            is TactNamedElement        -> TactLookupElementProperties.ElementKind.OTHER
            else                       -> return null
        }

        val isTypeCompatible = false
        // TODO: Needs stubs enhancement
        // if (element is TactTypeOwner) {
        //     val type = element.getType(null)?.unwrapFunction()
        //     val contextType = TactTypeInferenceUtil.getContextType(context.parent)
        //     isTypeCompatible =
        //         type != null && contextType != null && type !is TactVoidPtrTypeEx && contextType.isAssignableFrom(type, context.project)
        // }

        val inComptimeStubs =
            elementFile.virtualFile != null && elementFile.virtualFile.name == "comptime.sp" && elementFile.virtualFile.path.contains("stubs")

        val lookupElement = when (element) {
            is TactFunctionDeclaration       -> TactCompletionUtil.createFunctionLookupElement(element, state)
            is TactStructDeclaration         -> TactCompletionUtil.createStructLookupElement(element, state, !forTypes)
            is TactFieldDefinition           -> TactCompletionUtil.createFieldLookupElement(element)
            is TactConstDefinition           -> TactCompletionUtil.createConstantLookupElement(element, inComptimeStubs, state)
            is TactModuleVarDefinition       -> TactCompletionUtil.createModuleVariableLikeLookupElement(element, state)
            is TactParamDefinition           -> TactCompletionUtil.createParamLookupElement(element, state)
            is TactNamedElement              -> TactCompletionUtil.createVariableLikeLookupElement(element)
            else                             -> null
        }

        var isContextElement = false
        if (lookupElement is PrioritizedLookupElement<*>) {
            isContextElement = lookupElement.priority.toInt() == TactCompletionUtil.CONTEXT_COMPLETION_PRIORITY
        }

        val isNotDeprecated = element !is TactNamedElement || !element.isDeprecated()

        return lookupElement?.toTactLookupElement(
            TactLookupElementProperties(
                isLocal = isLocal,
                isSameModule = isSameModule,
                elementKind = kind,
                isTypeCompatible = isTypeCompatible,
                isContextElement = isContextElement,
                isNotDeprecated = isNotDeprecated,
                accessedNotViaOption = !state.get(UNWRAPPED_OPTION_METHODS) || state.get(SAFE_ACCESS),
            )
        )
    }
}
