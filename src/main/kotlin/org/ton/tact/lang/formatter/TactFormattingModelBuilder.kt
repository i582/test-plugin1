package org.ton.tact.lang.formatter

import com.intellij.formatting.*
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.ton.tact.lang.TactLanguage
import org.ton.tact.lang.TactTypes.*

class TactFormattingModelBuilder : FormattingModelBuilder {
    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
        return SpacingBuilder(settings, TactLanguage)
            .around(ASSIGN)
            .spaceIf(settings.getCommonSettings(TactLanguage.id).SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .around(FUNCTION_DECLARATION).blankLines(settings.BLANK_LINES_AROUND_METHOD)
            .between(LPAREN, RPAREN).spacing(0, 0, 0, false, 0)
            .between(LBRACE, RBRACE).spacing(0, 0, 0, false, 0)
            .between(LBRACK, RBRACK).spacing(0, 0, 0, false, 0)
            .after(LBRACE).lineBreakInCode()
            .after(RBRACE).spaces(1)
    }

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings
        return FormattingModelProvider
            .createFormattingModelForPsiFile(
                formattingContext.containingFile,
                TactFormattingBlock(
                    formattingContext.node,
                    spacingBuilder = createSpaceBuilder(codeStyleSettings),
                    withIdent = false,
                ),
                codeStyleSettings
            )
    }
}
