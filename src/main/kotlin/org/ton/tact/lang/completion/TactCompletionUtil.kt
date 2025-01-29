package org.ton.tact.lang.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.ConstantNode
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.ide.ui.Icons
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.MODULE_NAME
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.NAMED_PARAMETER_COMPLETION
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.NEED_QUALIFIER_NAME
import javax.swing.Icon

object TactCompletionUtil {
    const val KEYWORD_PRIORITY = 0
    const val CONTEXT_COMPLETION_PRIORITY = 20
    const val CONTEXT_KEYWORD_PRIORITY = 0
    private const val NOT_IMPORTED_METHOD_PRIORITY = 0
    private const val METHOD_PRIORITY = NOT_IMPORTED_METHOD_PRIORITY + 0
    private const val NOT_IMPORTED_FUNCTION_PRIORITY = 0
    private const val FUNCTION_PRIORITY = NOT_IMPORTED_FUNCTION_PRIORITY + 0
    private const val NOT_IMPORTED_STRUCT_PRIORITY = 0
    private const val STRUCT_PRIORITY = NOT_IMPORTED_STRUCT_PRIORITY + 0
    private const val NOT_IMPORTED_TYPE_ALIAS_PRIORITY = 0
    private const val TYPE_ALIAS_PRIORITY = NOT_IMPORTED_TYPE_ALIAS_PRIORITY + 0
    private const val UNION_PRIORITY = NOT_IMPORTED_TYPE_ALIAS_PRIORITY + 0
    private const val NOT_IMPORTED_CONSTANT_PRIORITY = 0
    private const val CONSTANT_PRIORITY = NOT_IMPORTED_CONSTANT_PRIORITY + 0
    private const val NOT_IMPORTED_VAR_PRIORITY = 0
    private const val VAR_PRIORITY = NOT_IMPORTED_VAR_PRIORITY + 0
    private const val FIELD_PRIORITY = 16
    private const val MODULE_PRIORITY = 0

    fun withCamelHumpPrefixMatcher(resultSet: CompletionResultSet): CompletionResultSet {
        return resultSet.withPrefixMatcher(createPrefixMatcher(resultSet.prefixMatcher.prefix))
    }

    private fun createPrefixMatcher(prefix: String) = CamelHumpMatcher(prefix, false)

    fun LookupElementBuilder.withPriority(priority: Int): LookupElement {
        return PrioritizedLookupElement.withPriority(this, priority.toDouble())
    }

    fun LookupElement.toTactLookupElement(properties: TactLookupElementProperties): LookupElement {
        return TactLookupElement(this, properties)
    }

    fun shouldSuppressCompletion(element: PsiElement): Boolean {
        val parent = element.parent
        val grand = parent.parent
        if (grand is TactVarDeclaration && PsiTreeUtil.isAncestor(grand, element, false)) {
            return true
        }

        if (parent is TactFunctionDeclaration) {
            return true
        }

        return parent is TactStructType || parent is TactTraitDeclaration || parent is TactMessageDeclaration
    }

    fun createVariableLikeLookupElement(element: TactNamedElement): LookupElement? {
        val name = element.name
        if (name.isNullOrEmpty()) {
            return null
        }
        return createVariableLikeLookupElement(
            element, name
        )
    }

    fun createParamLookupElement(element: TactParamDefinition, state: ResolveState): LookupElement? {
        val name = element.name
        if (name.isNullOrEmpty()) {
            return null
        }
        val namedCompletion = state.get(NAMED_PARAMETER_COMPLETION) ?: false
        val insertHandler = if (namedCompletion) ParamInCallInsertHandler() else null
        return createParameterLookupElement(
            element, name,
            insertHandler = insertHandler,
            forNamedCompletion = namedCompletion
        )
    }

    fun createFunctionLookupElement(element: TactFunctionDeclaration, state: ResolveState): LookupElement? {
        val name = element.name
        if (name.isEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createFunctionLookupElement(
            element, name, moduleName, state,
            insertHandler = FunctionInsertHandler(element, moduleName),
        )
    }

    fun createAsmFunctionLookupElement(element: TactAsmFunctionDeclaration, state: ResolveState): LookupElement? {
        val name = element.name
        if (name.isNullOrEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createFunctionLookupElement(
            element, name, moduleName, state,
            insertHandler = FunctionInsertHandler(element, moduleName),
        )
    }

    fun createNativeFunctionLookupElement(element: TactNativeFunctionDeclaration, state: ResolveState): LookupElement? {
        val name = element.name
        if (name.isNullOrEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createFunctionLookupElement(
            element, name, moduleName, state,
            insertHandler = FunctionInsertHandler(element, moduleName),
        )
    }

    fun createConstantLookupElement(element: TactNamedElement, isComptime: Boolean, state: ResolveState): LookupElement? {
        var name = element.name
        if (name.isNullOrEmpty()) {
            return null
        }
        if (isComptime) {
            name = "\$$name"
        }

        val moduleName = state.get(MODULE_NAME)
        return createConstantLookupElement(
            element, name, moduleName, state,
            insertHandler = ConstantInsertHandler(moduleName),
        )
    }

    fun createFieldLookupElement(element: TactNamedElement): LookupElement? {
        val name = element.name
        if (name.isNullOrEmpty()) {
            return null
        }

        return createFieldLookupElement(
            element, name,
            priority = FIELD_PRIORITY,
            insertHandler = FieldInsertHandler()
        )
    }

    fun createStructLookupElement(element: TactStructDeclaration, state: ResolveState, needBrackets: Boolean): LookupElement? {
        val name = element.name
        if (name.isEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createStructLookupElement(
            element, name, moduleName, state,
            insertHandler = StructInsertHandler(moduleName, needBrackets)
        )
    }

    fun createTraitLookupElement(element: TactTraitDeclaration, state: ResolveState): LookupElement? {
        val name = element.name
        if (name.isEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createClassLikeLookupElement(
            element, name, Icons.Trait, moduleName, state, false,
        )
    }

    fun createMessageLookupElement(element: TactMessageDeclaration, state: ResolveState, needBrackets: Boolean): LookupElement? {
        val name = element.name
        if (name.isEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createClassLikeLookupElement(
            element, name, Icons.Message, moduleName, state, needBrackets
        )
    }

    fun createPrimitiveLookupElement(element: TactPrimitiveDeclaration, state: ResolveState): LookupElement? {
        val name = element.name
        if (name.isEmpty()) {
            return null
        }

        val moduleName = state.get(MODULE_NAME)
        return createClassLikeLookupElement(
            element, name, Icons.Primitive, moduleName, state, false,
        )
    }

    private fun createClassLikeLookupElement(
        element: TactNamedElement, lookupString: String,
        icon: Icon, moduleName: String?, state: ResolveState, needBrackets: Boolean,
    ): LookupElement {
        val qualifiedName = createQualifiedName(state, lookupString)
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(qualifiedName, element)
                .withRenderer(ClassLikeRenderer(icon, moduleName))
                .withInsertHandler(ClassLikeInsertHandler(moduleName, needBrackets)), STRUCT_PRIORITY.toDouble()
        )
    }

    private fun createStructLookupElement(
        element: TactStructDeclaration, lookupString: String, moduleName: String?, state: ResolveState,
        insertHandler: InsertHandler<LookupElement>? = null,
    ): LookupElement {
        val qualifiedName = createQualifiedName(state, lookupString)
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(qualifiedName, element)
                .withRenderer(ClassLikeRenderer(Icons.Struct, moduleName))
                .withInsertHandler(insertHandler), STRUCT_PRIORITY.toDouble()
        )
    }

    private fun createFieldLookupElement(
        element: TactNamedElement, lookupString: String,
        insertHandler: InsertHandler<LookupElement>? = null,
        priority: Int = 0,
    ): LookupElement {
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(lookupString, element)
                .withRenderer(FIELD_RENDERER)
                .withInsertHandler(insertHandler), priority.toDouble()
        )
    }

    private fun createFunctionLookupElement(
        element: TactNamedElement, lookupString: String, moduleName: String?, state: ResolveState,
        insertHandler: InsertHandler<LookupElement>? = null,
    ): LookupElement {
        val qualifiedName = createQualifiedName(state, lookupString)
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(qualifiedName, element)
                .withRenderer(FunctionRenderer(moduleName))
                .withInsertHandler(insertHandler), FUNCTION_PRIORITY.toDouble()
        )
    }

    private fun createQualifiedName(state: ResolveState, lookupString: String): String {
        val needQualifier = state.get(NEED_QUALIFIER_NAME) ?: true
        if (!needQualifier) {
            return lookupString
        }

        val moduleName = state.get(MODULE_NAME)
        val lastPart = moduleName?.substringAfterLast('.') ?: moduleName
        return if (moduleName != null) "$lastPart.$lookupString" else lookupString
    }

    private fun createConstantLookupElement(
        element: TactNamedElement, lookupString: String, moduleName: String?, state: ResolveState,
        insertHandler: InsertHandler<LookupElement>? = null,
    ): LookupElement {
        val qualifiedName = createQualifiedName(state, lookupString)
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(qualifiedName, element)
                .withRenderer(ConstantRenderer(moduleName))
                .withInsertHandler(insertHandler), CONSTANT_PRIORITY.toDouble()
        )
    }

    private fun createModuleLookupElement(
        element: TactFile, lookupString: String,
        insertHandler: InsertHandler<LookupElement>? = null,
    ): LookupElement {
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(lookupString, element)
                .withRenderer(MODULE_RENDERER)
                .withInsertHandler(insertHandler), MODULE_PRIORITY.toDouble()
        )
    }

    private fun createVariableLikeLookupElement(
        element: TactNamedElement, lookupString: String,
        insertHandler: InsertHandler<LookupElement>? = null,
    ): LookupElement {
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(lookupString, element)
                .withRenderer(VARIABLE_RENDERER)
                .withInsertHandler(insertHandler), MODULE_PRIORITY.toDouble()
        )
    }

    private fun createParameterLookupElement(
        element: TactNamedElement, lookupString: String,
        insertHandler: InsertHandler<LookupElement>? = null,
        forNamedCompletion: Boolean,
    ): LookupElement {
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.createWithSmartPointer(lookupString, element)
                .withRenderer(ParameterRenderer(forNamedCompletion))
                .withInsertHandler(insertHandler), VAR_PRIORITY.toDouble()
        )
    }

    fun showCompletion(editor: Editor) {
        AutoPopupController.getInstance(editor.project!!).autoPopupMemberLookup(editor, null)
    }

    abstract class ElementInsertHandler(private val moduleName: String?) : InsertHandler<LookupElement> {
        open fun handleInsertion(context: InsertionContext, item: LookupElement) {}

        final override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val file = context.file as TactFile

            context.commitDocument()

            handleInsertion(context, item)

            context.commitDocument()

            if (!moduleName.isNullOrEmpty()) {
                file.addImport(moduleName, null)
            }
        }
    }

    open class FunctionInsertHandler(
        private val function: TactSignatureOwner,
        moduleName: String?,
    ) : ElementInsertHandler(moduleName) {
        override fun handleInsertion(context: InsertionContext, item: LookupElement) {
            val caretOffset = context.editor.caretModel.offset
            val element = context.file.findElementAt(caretOffset - 1)

            val parent = element?.parent as? TactReferenceExpression
            val takeZeroArguments = TactCodeInsightUtil.takeZeroArguments(function)
            val methodCall = parent?.getQualifier() != null && TactCodeInsightUtil.takeSingleArgument(function)
            val cursorAfterParens = takeZeroArguments || methodCall

            val prevChar = context.document.charsSequence.getOrNull(caretOffset)
            val withParenAfterCursor = prevChar == '('

            if (!withParenAfterCursor) {
                try {
                    context.document.insertString(caretOffset, "()")
                } catch (e: Exception) {
                    return
                }
            }

            if (cursorAfterParens) {
                // move after ()
                context.editor.caretModel.moveToOffset(caretOffset + 2)
                return
            }
            context.editor.caretModel.moveToOffset(caretOffset + 1)

            // invoke parameter info to automatically show parameter info popup
            AutoPopupController.getInstance(context.project).autoPopupParameterInfo(context.editor, null)
        }
    }

    class ConstantInsertHandler(moduleName: String?) : ElementInsertHandler(moduleName)

    class StructInsertHandler(
        moduleName: String?,
        private val needBrackets: Boolean = true,
    ) : ElementInsertHandler(moduleName) {

        override fun handleInsertion(context: InsertionContext, item: LookupElement) {
            val caretOffset = context.editor.caretModel.offset

            if (needBrackets) {
                context.document.insertString(caretOffset, "{}")
                context.editor.caretModel.moveToOffset(caretOffset + 1)

                showCompletion(context.editor)
                return
            }

            val file = context.file as? TactFile ?: return
            val offset = context.startOffset
            val at = file.findElementAt(offset) ?: return
            handleIncompleteParameter(context, at)
        }
    }

    class ClassLikeInsertHandler(moduleName: String?, private val needBrackets: Boolean) : ElementInsertHandler(moduleName) {
        override fun handleInsertion(context: InsertionContext, item: LookupElement) {
            val file = context.file as? TactFile ?: return
            context.commitDocument()

            val caretOffset = context.editor.caretModel.offset

            if (needBrackets) {
                context.document.insertString(caretOffset, "{}")
                context.editor.caretModel.moveToOffset(caretOffset + 1)

                showCompletion(context.editor)
                return
            }

            val offset = context.startOffset
            val at = file.findElementAt(offset) ?: return
            handleIncompleteParameter(context, at)
        }
    }

    fun handleIncompleteParameter(context: InsertionContext, element: PsiElement) {
        val paramDefinition = element.parentOfType<TactParamDefinition>() ?: return
        val name = paramDefinition.name
        if (name == null) {
            // run only for parameters without a name
            val caretOffset = paramDefinition.type.textOffset
            context.editor.caretModel.moveToOffset(caretOffset)

            val template = TemplateManager.getInstance(context.project)
                .createTemplate("templateInsertHandler", "spawn", "\$expr\$\$END\$ ")

            template.addVariable("expr", ConstantNode("param"), true)

            TemplateManager.getInstance(context.project).startTemplate(context.editor, template)
        }
    }

    class StringInsertHandler(val string: String, private val shift: Int, private val enable: Boolean = true) :
        InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            if (!enable) return

            val caretOffset = context.editor.caretModel.offset

            context.document.insertString(caretOffset, string)
            context.editor.caretModel.moveToOffset(caretOffset + shift)
        }
    }

    class TemplateStringInsertHandler(
        private val string: String,
        private val reformat: Boolean = true,
        vararg val variables: Pair<String, Expression>,
    ) : InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val template = TemplateManager.getInstance(context.project)
                .createTemplate("templateInsertHandler", "spawn", string)
            template.isToReformat = reformat

            variables.forEach { (name, expression) ->
                template.addVariable(name, expression, true)
            }

            TemplateManager.getInstance(context.project).startTemplate(context.editor, template)
        }
    }

    open class FieldInsertHandler : SingleCharInsertHandler(':', needSpace = true) {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val file = context.file as? TactFile ?: return
            context.commitDocument()

            val offset = context.startOffset
            val at = file.findElementAt(offset)

            val ref = PsiTreeUtil.getParentOfType(at, TactValue::class.java, TactReferenceExpression::class.java)
            if (ref is TactReferenceExpression && (ref.getQualifier() != null)) {
                return
            }

            val value = PsiTreeUtil.getParentOfType(at, TactValue::class.java)
            if (value == null || PsiTreeUtil.getPrevSiblingOfType(value, TactKey::class.java) != null) {
                return
            }
            super.handleInsert(context, item)
        }
    }

    class ParamInCallInsertHandler : FieldInsertHandler()

    private val MODULE_RENDERER = object : LookupElementRenderer<LookupElement>() {
        override fun renderElement(element: LookupElement, p: LookupElementPresentation) {
            val elem = element.psiElement as? TactFile ?: return
            val moduleName = "" // elem.getModuleName()
            val qualifier = elem.getModuleQualifiedName().substringBeforeLast('.', "")

            p.icon = Icons.Directory
            p.tailText = " $qualifier"
            p.isTypeGrayed = true
            p.itemText = moduleName
        }
    }

    private val VARIABLE_RENDERER = object : LookupElementRenderer<LookupElement>() {
        override fun renderElement(element: LookupElement, p: LookupElementPresentation) {
            val elem = element.psiElement as? TactNamedElement ?: return
            val type = elem.getType(null)?.readableName(elem)
            val icon = when (elem) {
                is TactVarDefinition       -> Icons.Variable
                is TactParamDefinition     -> Icons.Parameter
                else                       -> null
            }

            p.icon = icon
            p.typeText = type
            p.isTypeGrayed = true
            p.itemText = element.lookupString
            p.isStrikeout = elem.isDeprecated()
        }
    }

    class ParameterRenderer(private val forNamedCompletion: Boolean) : LookupElementRenderer<LookupElement>() {
        override fun renderElement(element: LookupElement, p: LookupElementPresentation) {
            val elem = element.psiElement as? TactParamDefinition ?: return

            val suffix = if (forNamedCompletion) ": <value>" else ""

            p.icon = Icons.Parameter
            p.typeText = elem.getType(null)?.readableName(elem)
            p.isTypeGrayed = true
            p.tailText = suffix
            p.itemText = element.lookupString
            p.isStrikeout = elem.isDeprecated()
        }
    }

    private val FIELD_RENDERER = object : LookupElementRenderer<LookupElement>() {
        override fun renderElement(element: LookupElement, p: LookupElementPresentation) {
            val elem = element.psiElement as? TactNamedElement ?: return
            val type = elem.getType(null)?.readableName(elem)
            val icon = Icons.Field

            // TODO: show fqn?
            val parent = elem.parentOfType<TactNamedElement>()
            p.tailText = " of " + parent?.name

            p.icon = icon
            p.typeText = type
            p.isTypeGrayed = true
            p.itemText = element.lookupString
            p.isStrikeout = elem.isDeprecated()
        }
    }

    class ClassLikeRenderer(private val icon: Icon, moduleName: String?) : ElementRenderer(moduleName) {
        override fun render(element: LookupElement, p: LookupElementPresentation) {
            p.icon = icon
            p.itemText = element.lookupString
        }
    }

    class FunctionRenderer(moduleName: String?) : ElementRenderer(moduleName) {
        override fun render(element: LookupElement, p: LookupElementPresentation) {
            val elem = element.psiElement as? TactSignatureOwner ?: return
            p.icon = Icons.Function

            val signature = elem.getSignature()
            val parameters = signature?.parameters?.text ?: ""
            val result = signature?.result?.type?.text

            p.tailText = parameters
            p.itemText = element.lookupString
            p.typeText = result
        }
    }

    class ConstantRenderer(moduleName: String?) : ElementRenderer(moduleName) {
        override fun render(element: LookupElement, p: LookupElementPresentation) {
            val elem = element.psiElement as? TactConstDefinition ?: return
            p.icon = Icons.Constant
            p.itemText = element.lookupString

            val valueText = elem.expressionText
            p.tailText = " = $valueText"
            p.typeText = elem.expressionType
            p.isStrikeout = elem.isDeprecated()
        }
    }

    abstract class ElementRenderer(private val moduleName: String?) : LookupElementRenderer<LookupElement>() {
        abstract fun render(element: LookupElement, p: LookupElementPresentation)

        override fun renderElement(element: LookupElement, p: LookupElementPresentation) {
            render(element, p)

            if (moduleName != null) {
                if (p.tailText.isNullOrEmpty()) {
                    p.tailText = " from $moduleName"
                } else {
                    p.tailText += " from $moduleName"
                }
            }
        }
    }
}
