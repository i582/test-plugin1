package org.tonstudio.tact.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.impl.source.tree.ICodeFragmentElementType
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.tree.IElementType
import org.tonstudio.tact.lang.TactLanguage
import org.tonstudio.tact.lang.TactParser
import org.tonstudio.tact.lang.TactTypes

class TactCodeFragmentElementType(private val elementType: IElementType, debugName: String) :
    ICodeFragmentElementType(debugName, TactLanguage) {

    override fun parseContents(chameleon: ASTNode): ASTNode? {
        if (chameleon !is TreeElement) return null
        val project = chameleon.manager.project
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon)
        val root = TactParser().parse(elementType, builder)
        return root.firstChildNode
    }

    companion object {
        val EXPR = TactCodeFragmentElementType(TactTypes.EXPRESSION, "SPAWN_EXPRESSION")
        val TYPE = TactCodeFragmentElementType(TactTypes.EXPRESSION, "SPAWN_TYPE")
//        val STMT = TactCodeFragmentElementType(RsElementTypes.STATEMENT_CODE_FRAGMENT_ELEMENT, "RS_STMT_CODE_FRAGMENT")
//        val TYPE_REF = TactCodeFragmentElementType(RsElementTypes.TYPE_REFERENCE_CODE_FRAGMENT_ELEMENT, "RS_TYPE_REF_CODE_FRAGMENT")
//        val TYPE_PATH = TactCodeFragmentElementType(RsElementTypes.TYPE_PATH_CODE_FRAGMENT_ELEMENT, "RS_TYPE_PATH_CODE_FRAGMENT")
//        val VALUE_PATH = TactCodeFragmentElementType(RsElementTypes.VALUE_PATH_CODE_FRAGMENT_ELEMENT, "RS_VALUE_PATH_CODE_FRAGMENT")
//        val REPL = TactCodeFragmentElementType(RsElementTypes.REPL_CODE_FRAGMENT_ELEMENT, "RS_REPL_CODE_FRAGMENT")
    }
}
