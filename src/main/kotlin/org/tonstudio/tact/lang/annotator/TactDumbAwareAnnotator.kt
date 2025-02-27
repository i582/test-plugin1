package org.tonstudio.tact.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.tonstudio.tact.ide.colors.TactColor
import org.tonstudio.tact.lang.TactTypes
import org.tonstudio.tact.lang.psi.*
import org.tonstudio.tact.utils.inside

class TactDumbAwareAnnotator : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return

        val color = highlightLeaf(element) ?: return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(color.textAttributesKey).create()
    }

    private fun highlightLeaf(element: PsiElement): TactColor? {
        if (element.elementType == TactTypes.INIT_OF && element.inside<TactInitOfExpr>()) return TactColor.KEYWORD
        if (element.elementType == TactTypes.AT && element.inside<TactAttribute>()) return TactColor.ATTRIBUTE
        if (element.elementType != TactTypes.IDENTIFIER) return null
        if (element.textMatches("self")) return TactColor.KEYWORD
        if (element.textMatches("asm") && element.inside<TactAsmFunctionDeclaration>()) return TactColor.KEYWORD
        if (element.textMatches("bounced") && element.inside<TactMessageFunctionDeclaration>()) return TactColor.KEYWORD
        if (element.textMatches("init") && element.inside<TactContractInitDeclaration>()) return TactColor.KEYWORD

        val parent = element.parent as? TactCompositeElement ?: return null

        return when (parent) {
            is TactPrimitiveType             -> TactColor.PRIMITIVE
            is TactFunctionDeclaration       -> TactColor.PUBLIC_FUNCTION
            is TactAsmFunctionDeclaration    -> TactColor.PUBLIC_FUNCTION
            is TactNativeFunctionDeclaration -> TactColor.PUBLIC_FUNCTION
            is TactStructDeclaration         -> TactColor.PUBLIC_FUNCTION
            is TactFieldDefinition           -> TactColor.PUBLIC_FIELD
            is TactConstDefinition           -> TactColor.PUBLIC_CONSTANT
            is TactAsmInstruction            -> TactColor.KEYWORD
            is TactAttributeIdentifier       -> TactColor.ATTRIBUTE
            is TactFunctionAttribute         -> TactColor.KEYWORD
            else                             -> null
        }
    }
}
