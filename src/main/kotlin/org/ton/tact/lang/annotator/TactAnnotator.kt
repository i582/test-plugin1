package org.ton.tact.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.ton.tact.ide.colors.TactColor
import org.ton.tact.lang.TactTypes
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.TactModule
import org.ton.tact.lang.psi.impl.TactReference
import org.ton.tact.lang.psi.types.TactPrimitiveTypes

class TactAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return

        val color = highlightLeaf(element) ?: return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(color.textAttributesKey).create()
    }

    private fun highlightLeaf(element: PsiElement): TactColor? {
        val parent = element.parent as? TactCompositeElement ?: return null

        if (element.elementType == TactTypes.IDENTIFIER && parent is TactReferenceExpressionBase) {
            return highlightReference(parent, parent.reference as TactReference)
        }

        return when (element.elementType) {
            TactTypes.IDENTIFIER -> null
            else                 -> null
        }
    }

    private fun highlightReference(
        element: TactReferenceExpressionBase,
        reference: TactReference,
    ): TactColor? {
        if (TactPrimitiveTypes.isPrimitiveType(element.text)) {
            return null
        }

        val resolved = reference.resolve() ?: return null

        return when (resolved) {
            is TactPrimitiveDeclaration           -> TactColor.PRIMITIVE
            is TactFunctionDeclaration            -> TactColor.PUBLIC_FUNCTION
            is TactAsmFunctionDeclaration         -> TactColor.PUBLIC_FUNCTION
            is TactNativeFunctionDeclaration      -> TactColor.PUBLIC_FUNCTION
            is TactStructDeclaration              -> TactColor.PUBLIC_STRUCT
            is TactTraitDeclaration               -> TactColor.PUBLIC_TRAIT
            is TactMessageDeclaration             -> TactColor.PUBLIC_TRAIT
            is TactFieldDefinition                -> TactColor.PUBLIC_FIELD
            is TactParamDefinition                -> TactColor.PARAMETER
            is TactVarDefinition                  -> TactColor.VARIABLE
            is TactModule.TactPomTargetPsiElement -> TactColor.MODULE
            is TactConstDefinition                -> TactColor.PUBLIC_CONSTANT
            else                                  -> null
        }
    }
}
