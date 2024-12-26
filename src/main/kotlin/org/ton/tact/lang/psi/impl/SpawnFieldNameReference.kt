package org.ton.tact.lang.psi.impl

import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.TactReferenceBase.Companion.LOCAL_RESOLVE
import org.ton.tact.lang.psi.types.TactArrayTypeEx
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.unwrapAlias
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.unwrapGenericInstantiation
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.unwrapReference
import org.ton.tact.lang.psi.types.TactStructTypeEx
import org.ton.tact.lang.psi.types.TactTypeEx

class TactFieldNameReference(element: TactReferenceExpressionBase) :
    TactCachedReference<TactReferenceExpressionBase>(element) {

    private val stubsManager = project.service<TactStubsManager>()

    override fun processResolveVariants(processor: TactScopeProcessor): Boolean {
        val fieldProcessor =
            if (processor is TactFieldProcessor)
                processor
            else
                object : TactFieldProcessor(myElement) {
                    override fun execute(e: PsiElement, state: ResolveState): Boolean {
                        return super.execute(e, state) && processor.execute(e, state)
                    }
                }

        val key = myElement.parentOfType<TactKey>()
        val value = myElement.parentOfType<TactValue>()
        if (key == null && (value == null || PsiTreeUtil.getPrevSiblingOfType(value, TactKey::class.java) != null)) {
            return true
        }

        var type = myElement.parentOfType<TactLiteralValueExpression>()?.getType(null)
        if (type == null) {
            val callExpr = TactCodeInsightUtil.getCallExpr(element)
            val paramTypes = TactCodeInsightUtil.getCalledParams(callExpr)
            type = paramTypes?.lastOrNull { it.unwrapReference().unwrapAlias().unwrapGenericInstantiation() is TactStructTypeEx }?: return true
        }

        val typeToProcess = type.unwrapReference().unwrapAlias().unwrapGenericInstantiation()
        if (typeToProcess is TactArrayTypeEx) {
            return processArrayInitFields(fieldProcessor)
        }

        val typeFile = type.anchor(project)?.containingFile as? TactFile
        val originFile = element.containingFile as TactFile
        val localResolve = typeFile == null || TactReference.isLocalResolve(typeFile, originFile)

        return processStructType(fieldProcessor, typeToProcess, localResolve)
    }

    private fun processArrayInitFields(processor: TactScopeProcessor, ): Boolean {
        val psiFile = stubsManager.findFile("arrays.sp") ?: return true
        val struct = psiFile.getStructs().firstOrNull { it.name == "ArrayInit" } ?: return true
        return processStructType(processor, struct.structType.toEx(), false)
    }

    private fun processChannelInitFields(processor: TactScopeProcessor, ): Boolean {
        val psiFile = stubsManager.findFile("channels.sp") ?: return true
        val struct = psiFile.getStructs().firstOrNull { it.name == "ChanInit" } ?: return true
        return processStructType(processor, struct.structType.toEx(), false)
    }

    private fun processStructType(fieldProcessor: TactScopeProcessor, type: TactTypeEx?, localResolve: Boolean): Boolean {
        if (type !is TactStructTypeEx) return true

        val state = if (localResolve) ResolveState.initial().put(LOCAL_RESOLVE, true) else ResolveState.initial()
        val declaration = type.resolve(project) ?: return true
        val structType = declaration.structType

        val fields = structType.fieldList
        for (field in fields) {
            if (!fieldProcessor.execute(field, state)) return false
        }

        structType.embeddedStructList.forEach {
            if (!processStructType(fieldProcessor, it.type.toEx(), localResolve)) return false
        }

        return true
    }

    override fun resolveInner(): PsiElement? {
        val p = TactFieldProcessor(myElement)
        processResolveVariants(p)
        return p.getResult()
    }

    private open class TactFieldProcessor(element: PsiElement) : TactScopeProcessorBase(element) {
        private val myModule: Module?

        init {
            val containingFile = origin.containingFile
            myModule = ModuleUtilCore.findModuleForPsiElement(containingFile.originalFile)
        }

        override fun crossOff(e: PsiElement): Boolean {
            if (e !is TactFieldDefinition && e !is TactEmbeddedDefinition)
                return true
            val named = e as TactNamedElement
            val originFile = origin.containingFile as TactFile
            val file = e.containingFile as TactFile
            val localResolve = TactReference.isLocalResolve(originFile, file)
            return !e.isValid
        }
    }
}
