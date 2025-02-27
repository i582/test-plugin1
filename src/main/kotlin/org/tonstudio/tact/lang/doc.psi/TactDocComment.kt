package org.tonstudio.tact.lang.doc.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocCommentBase
import org.tonstudio.tact.lang.psi.TactNamedElement

interface TactDocComment : PsiDocCommentBase {
    override fun getOwner(): TactNamedElement?

    /**
     * [getOwnerNameRangeInElement] returns the range of the name of the owner element in the comment
     * or `null` if the comment is invalid.
     */
    fun getOwnerNameRangeInElement(): TextRange?

    /**
     * [isValidDoc] checks if the comment starts with name of the owner element
     *
     * For example, in the following code:
     *
     * ```tact
     * // foo
     * fn foo() {}
     * ```
     *
     * `foo` is the name of the owner element and the comment is **valid**
     *
     * In the following code:
     *
     * ```tact
     * // bar
     * fn foo() {}
     * ```
     *
     * `bar` is not the name of the owner element and the comment is **invalid**
     */
    fun isValidDoc(): Boolean

    val codeFences: List<TactDocCodeFence>

    val linkDefinitions: List<TactDocLinkDefinition>

    val linkReferenceMap: Map<String, TactDocLinkDefinition>
}
