package org.ton.tact.ide.hints

import com.intellij.codeInsight.hints.declarative.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.ton.tact.lang.psi.TactVarDeclaration
import org.ton.tact.lang.psi.TactVarDefinition
import org.ton.tact.lang.psi.types.TactUnknownTypeEx

class TactVariableTypeHintsProvider : InlayHintsProvider {
    override fun createCollector(file: PsiFile, editor: Editor) = Collector()

    class Collector : SharedBypassCollector {
        override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
            if (element is TactVarDefinition) {
                handleVarDefinition(element, sink)
            }
        }

        private fun handleVarDefinition(element: TactVarDefinition, sink: InlayTreeSink) {
            if (element.isBlank()) {
                // don't show a hint for "_" variables
                return
            }

            val parent = element.parent as? TactVarDeclaration
            if (parent?.typeHint != null) {
                // no need to show a hint if there is a type hint
                return
            }

            val type = element.getType(null) ?: return
            if (type is TactUnknownTypeEx) {
                // no need to show a hint if type is unknown
                return
            }

            sink.addPresentation(
                InlineInlayPosition(element.textRange.endOffset, true),
                listOf(),
                null, HintFormat.default.withColorKind(HintColorKind.Parameter).withHorizontalMargin(HintMarginPadding.MarginAndSmallerPadding)
            ) {
                text(": ")
                text(type.readableName(element))
            }
        }
    }
}