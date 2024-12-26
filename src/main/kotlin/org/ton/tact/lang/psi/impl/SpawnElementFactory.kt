package org.ton.tact.lang.psi.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import org.ton.tact.lang.TactLanguage
import org.ton.tact.lang.doc.psi.TactDocComment
import org.ton.tact.lang.psi.*

object TactElementFactory {
    fun createFileFromText(project: Project, text: String): TactFile {
        return PsiFileFactory.getInstance(project).createFileFromText("dummy.sp", TactLanguage, text) as TactFile
    }

    fun createVariableDeclarationStatement(
        project: Project,
        name: String,
        expr: PsiElement? = null,
        isMutable: Boolean = false,
    ): TactStatement {
        val modifier = if (isMutable) "mut " else ""
        val file = createFileFromText(project, "fn main() { $modifier$name := ${expr?.text} }")
        return PsiTreeUtil.findChildOfType(file, TactSimpleStatement::class.java)!!
    }

    fun createType(project: Project, text: String): TactType {
        val fieldDeclaration = createFieldDeclaration(project, "a", text) as TactFieldDeclaration
        return fieldDeclaration.type!!
    }

    fun createReferenceExpression(project: Project, name: String): TactReferenceExpression {
        val file = createFileFromText(project, "fn main() { a := $name }")
        return PsiTreeUtil.findChildOfType(file, TactReferenceExpression::class.java)!!
    }

    fun createQualifierExpression(project: Project, qualifier: String, sel: String): TactReferenceExpression {
        val file = createFileFromText(project, "fn main() { $qualifier.$sel }")
        return PsiTreeUtil.findChildOfType(file, TactReferenceExpression::class.java)!!
    }

    fun createSafeQualifierExpression(project: Project, qualifier: String, sel: String): TactReferenceExpression {
        val file = createFileFromText(project, "fn main() { $qualifier?.$sel }")
        return PsiTreeUtil.findChildOfType(file, TactReferenceExpression::class.java)!!
    }

    fun createExpression(project: Project, name: String): TactExpression {
        val file = createFileFromText(project, "fn main() { a := $name }")
        return PsiTreeUtil.findChildOfType(file, TactExpression::class.java)!!
    }

    fun createIdentifier(project: Project, text: String): PsiElement {
        val file = createFileFromText(project, "let $text = 10;")
        return PsiTreeUtil.findChildOfType(file, TactVarDeclaration::class.java)!!.varDefinition!!.getIdentifier()
    }

    fun createStatement(project: Project, text: String): TactSimpleStatement {
        val file = createFileFromText(project, text)
        return PsiTreeUtil.findChildOfType(file, TactSimpleStatement::class.java)!!
    }

    fun createImportDeclaration(project: Project, name: String, alias: String?): TactImportDeclaration? {
        return createImportList(project, name)?.importDeclarationList?.firstOrNull()
    }

    fun createFunctionResult(project: Project, text: String): TactResult {
        val file = createFileFromText(project, "fn main() $text {}")
        return PsiTreeUtil.findChildOfType(file, TactResult::class.java)!!
    }

    fun createElseBranch(project: Project, text: String = "else {\n\t\n}"): TactElseBranch {
        val file = createFileFromText(project, "fn main() { if true {} $text")
        return PsiTreeUtil.findChildOfType(file, TactElseBranch::class.java)!!
    }

    fun createStringLiteral(project: Project, text: String): TactStringLiteral {
        return PsiTreeUtil.findChildOfType(
            createFileFromText(project, "fn main() { $text }"),
            TactStringLiteral::class.java
        )!!
    }

    fun createReference(project: Project, text: String): TactReferenceExpression {
        val children = PsiTreeUtil.findChildrenOfType(
            createFileFromText(project, text),
            TactReferenceExpression::class.java
        )
        return children.last()
    }

    fun createDocComment(project: Project, text: String): TactDocComment {
        return PsiTreeUtil.findChildOfType(
            createFileFromText(project, text),
            TactDocComment::class.java
        )!!
    }

    fun createNewLine(project: Project): PsiElement {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n")
    }

    fun createSpace(project: Project): PsiElement {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText(" ")
    }

    fun createTab(project: Project): PsiElement {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\t")
    }

    fun createDoubleNewLine(project: Project): PsiElement {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n\n")
    }

    fun createImportList(project: Project, name: String): TactImportList? {
        val file = createFileFromText(project, "import \"$name\"")
        return file.getImportList()
    }

    fun createFieldDeclaration(project: Project, name: String, type: String): PsiElement {
        val file = createFileFromText(project, "struct S {\n\t$name $type\n}")
        return file.getStructs().firstOrNull()?.structType?.fieldList?.firstOrNull()?.parent
            ?: error("Impossible situation! Parser is broken.")
    }

    fun createMethodCall(project: Project, expr: PsiElement, methodName: String, vararg args: PsiElement): TactCallExpr {
        val text = buildString {
            append("fn main() {\n")
            append(expr.text)
            append(".")
            append(methodName)
            append("(")
            append(args.joinToString(", ") { it.text })
            append(")")
            append("\n}")
        }
        val file = createFileFromText(project, text)
        return PsiTreeUtil.findChildOfType(file, TactCallExpr::class.java)!!
    }

    fun createCallWithGenericParameters(project: Project, called: String, generics: List<String>, args: String): TactCallExpr {
        val text = buildString {
            append("$called[")
            append(generics.joinToString(", "))
            append("]$args")
        }
        val file = createFileFromText(project, text)
        return PsiTreeUtil.findChildOfType(file, TactCallExpr::class.java)!!
    }

    fun createNone(project: Project): TactLiteral {
        val file = createFileFromText(project, "fn main() { a := none }")
        return PsiTreeUtil.findChildOfType(file, TactLiteral::class.java)!!
    }

    fun createNil(project: Project): TactLiteral {
        val file = createFileFromText(project, "fn main() { a := nil }")
        return PsiTreeUtil.findChildOfType(file, TactLiteral::class.java)!!
    }

    fun createTrue(project: Project): TactLiteral {
        val file = createFileFromText(project, "fn main() { a := true }")
        return PsiTreeUtil.findChildOfType(file, TactLiteral::class.java)!!
    }

    fun createSignature(project: Project, signature: TactSignature, newReturnType: TactType): TactSignature {
        val text = buildString {
            append("fn")
            append(signature.text)
            append(" -> ")
            append(newReturnType.text)
            append(" {}")
        }
        val file = createFileFromText(project, text)
        return PsiTreeUtil.findChildOfType(file, TactSignature::class.java)!!
    }

    fun createTypeCodeFragment(project: Project, text: String, context: TactCompositeElement): TactTypeCodeFragment {
        return TactTypeCodeFragment(project, text, context)
    }
}
