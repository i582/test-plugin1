package org.tonstudio.tact.lang.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiComment
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.tonstudio.tact.lang.TactTypes
import org.tonstudio.tact.lang.psi.*
import org.tonstudio.tact.utils.line

class TactFormattingBlock(
    node: ASTNode,
    wrap: Wrap? = Wrap.createWrap(WrapType.NONE, false),
    alignment: Alignment? = null,
    private val withIdent: Boolean = false,
    private val spacingBuilder: SpacingBuilder,
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<AbstractBlock> {
        val blocks = mutableListOf<AbstractBlock>()
        val parent = node.psi ?: return emptyList()

        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType == TokenType.WHITE_SPACE) {
                child = child.treeNext
                continue
            }

            var needIdent = when (parent) {
                is TactBlock                  -> true
                is TactTraitType              -> true
                is TactStructType             -> true
                is TactContractType           -> true
                is TactMessageType            -> true
                is TactConstDeclaration       -> true
                is TactLiteralValueExpression -> true
                is TactArgumentList           -> true
                else                          -> false
            } && (child !is LeafPsiElement || child is PsiComment)

            if (child is LeafPsiElement && child.elementType == TactTypes.DOT) {
                val prevLeaf = PsiTreeUtil.prevCodeLeaf(child)
                if (child.line() != prevLeaf?.line()) {
                    needIdent = true
                }
            }

            val block = TactFormattingBlock(child, spacingBuilder = spacingBuilder, withIdent = needIdent)
            blocks.add(block)

            child = child.treeNext
        }

        return blocks
    }

    override fun getSpacing(child1: Block?, child2: Block) = spacingBuilder.getSpacing(this, child1, child2)

    override fun getIndent(): Indent? = if (withIdent)
        Indent.getNormalIndent()
    else
        Indent.getNoneIndent()

    override fun isLeaf() = node.firstChildNode == null
}
