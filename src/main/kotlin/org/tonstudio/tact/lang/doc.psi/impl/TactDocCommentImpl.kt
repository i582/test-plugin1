package org.tonstudio.tact.lang.doc.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import org.tonstudio.tact.lang.doc.psi.TactDocCodeFence
import org.tonstudio.tact.lang.doc.psi.TactDocComment
import org.tonstudio.tact.lang.doc.psi.TactDocLinkDefinition
import org.tonstudio.tact.lang.psi.TactConstDeclaration
import org.tonstudio.tact.lang.psi.TactFieldDeclaration
import org.tonstudio.tact.lang.psi.TactNamedElement

/**
 * @param text a text for lazy parsing. `null` value means that the element is parsed ([isParsed] is `true`)
 */
class TactDocCommentImpl(type: IElementType, text: CharSequence?) : LazyParseablePsiElement(type, text), TactDocComment {
    override fun getTokenType(): IElementType = elementType

    /** Needed for URL references ([com.intellij.openapi.paths.WebReference]) */
    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }

    // Needed for TactFoldingBuilder
    override fun accept(visitor: PsiElementVisitor) {
        visitor.visitComment(this)
    }

    override fun toString(): String {
        return "PsiComment($elementType)"
    }

    override fun getOwner(): TactNamedElement? {
        val element = skipSiblingsForward(this, PsiComment::class.java, PsiWhiteSpace::class.java) ?: return null

        if (element is TactConstDeclaration) {
            return element.constDefinition
        }

        if (element is TactFieldDeclaration) {
            return element.fieldDefinition
        }

        return element as? TactNamedElement
    }

    override fun getOwnerNameRangeInElement(): TextRange? {
        if (!isValidDoc()) return null
        val commentText = text
        val firstWordStart = commentText.indexOfFirst { !it.isWhitespace() && it != '/' }
        if (firstWordStart == -1) return null
        val firstWordEnd = commentText.substring(firstWordStart).indexOfFirst { it.isWhitespace() || it == '\n' }
        if (firstWordEnd == -1) return null

        return TextRange(firstWordStart, firstWordStart + firstWordEnd)
    }

    override fun isValidDoc(): Boolean {
        val owner = owner ?: return true
        val commentText = text
        val preparedCommentText = commentText.trim().removePrefix("//").trim()
        val endOfFirstWord = preparedCommentText.indexOfFirst { it.isWhitespace() || it == '\n' }
        val firstWord = if (endOfFirstWord != -1) {
            preparedCommentText.substring(0, endOfFirstWord).trim()
        } else {
            preparedCommentText
        }

        val ownerName = owner.name ?: return true
        return ownerName == firstWord
    }

    private fun skipSiblingsForward(element: PsiElement?, vararg elementClasses: Class<out PsiElement?>): PsiElement? {
        if (element != null) {
            var e = element.nextSibling
            while (e != null) {
                if (e is PsiWhiteSpace && e.text.startsWith("\n\n")) {
                    return null
                }

                if (!PsiTreeUtil.instanceOf(e, *elementClasses)) {
                    return e
                }
                e = e.nextSibling
            }
        }
        return null
    }

    private fun skipSiblingsBackward(element: PsiElement?, vararg elementClasses: Class<out PsiElement?>): PsiElement? {
        if (element != null) {
            var e = element.prevSibling
            while (e != null) {
                if (e is PsiComment) {
                    return null
                }

                if (!PsiTreeUtil.instanceOf(e, *elementClasses)) {
                    return e
                }
                e = e.prevSibling
            }
        }
        return null
    }

    override val codeFences: List<TactDocCodeFence>
        get() = childrenOfType()

    override val linkDefinitions: List<TactDocLinkDefinition>
        get() = childrenOfType()

    override val linkReferenceMap: Map<String, TactDocLinkDefinition>
        get() = CachedValuesManager.getCachedValue(this) {
            val result = linkDefinitions.associateBy { it.linkLabel.markdownValue }
            CachedValueProvider.Result(result, containingFile)
        }
}
