package org.tonstudio.tact.lang

import com.intellij.lang.*
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.source.tree.LazyParseableElement
import com.intellij.psi.tree.ICompositeElementType
import com.intellij.psi.tree.ICustomParsingType
import com.intellij.psi.tree.IErrorCounterReparseableElementType
import com.intellij.psi.tree.ILightLazyParseableElementType
import com.intellij.psi.tree.IReparseableElementTypeBase
import com.intellij.util.CharTable
import com.intellij.util.diff.FlyweightCapableTreeStructure
import org.tonstudio.tact.lang.TactTypes.*
import org.tonstudio.tact.lang.lexer.TactLexer
import org.tonstudio.tact.lang.psi.impl.TactBlockImpl

class TactCodeBlockElementType :
    IErrorCounterReparseableElementType("BLOCK", TactLanguage),
    ICompositeElementType,
    ICustomParsingType,
    IReparseableElementTypeBase,
    ILightLazyParseableElementType {

    // Lazy parsed (function body)
    override fun parse(text: CharSequence, table: CharTable): ASTNode = LazyParseableElement(this, text)

    // Non-lazy case (`if` body, etc).
    override fun createCompositeNode(): ASTNode = TactBlockImpl(this, null)

    override fun createNode(text: CharSequence?): ASTNode = TactBlockImpl(this, text)

    override fun parseContents(chameleon: ASTNode): ASTNode {
        val builder = createBuilder(chameleon)
        val parse = TactParser().parse(BLOCK, builder)
        return parse.firstChildNode
    }

    override fun parseContents(chameleon: LighterLazyParseableNode): FlyweightCapableTreeStructure<LighterASTNode> {
        val builder = createBuilder(chameleon)
        TactParser().parseLight(BLOCK, builder)
        return builder.lightTree
    }

    // Restricted to a function body only because it is a well-tested case.
    // Maybe unrestricted to any block in the future
    override fun isReparseable(currentNode: ASTNode, newText: CharSequence, fileLanguage: Language, project: Project): Boolean {
        val parent = currentNode.treeParent?.elementType
        if (!PsiBuilderUtil.hasProperBraceBalance(newText, TactLexer(), LBRACE, RBRACE)) {
            return false
        }

        return parent == FUNCTION_DECLARATION
    }

    override fun getErrorsCount(seq: CharSequence, fileLanguage: Language, project: Project): Int {
        return if (PsiBuilderUtil.hasProperBraceBalance(seq, TactLexer(), LBRACE, RBRACE)) NO_ERRORS else FATAL_ERROR
    }

    // Avoid double lexing
    override fun reuseCollapsedTokens(): Boolean = true

    companion object {
        private fun createBuilder(chameleon: ASTNode): PsiBuilder {
            val project = chameleon.treeParent.psi.project
            return PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, TactLanguage, chameleon.chars)
        }

        private fun createBuilder(chameleon: LighterLazyParseableNode): PsiBuilder {
            val psi = chameleon.containingFile ?: error("`containingFile` must not be null: $chameleon")
            val project = psi.project
            return PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, TactLanguage, chameleon.text)
        }
    }
}
