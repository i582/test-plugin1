package org.ton.tact.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.ton.tact.lang.TactTypes.Factory
import org.ton.tact.lang.lexer.TactLexer
import org.ton.tact.lang.psi.TactFile
import org.ton.tact.lang.psi.TactTokenTypes

class TactParserDefinition : ParserDefinition {
    override fun createLexer(project: Project) = TactLexer()

    override fun createParser(project: Project) = TactParser()

    override fun getWhitespaceTokens() = TactTokenTypes.WHITE_SPACES

    override fun getCommentTokens() = TactTokenTypes.COMMENTS

    override fun getStringLiteralElements() = TactTokenTypes.STRING_LITERALS

    override fun getFileNodeType() = TactFileElementType.INSTANCE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = TactFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) = SpaceRequirements.MAY

    override fun createElement(node: ASTNode): PsiElement {
//        if (node.elementType == TactDocElementTypes.DOC_COMMENT) {
//            return TactDocCommentImpl(node.elementType, node.text)
//        }
        return Factory.createElement(node)
    }
}
