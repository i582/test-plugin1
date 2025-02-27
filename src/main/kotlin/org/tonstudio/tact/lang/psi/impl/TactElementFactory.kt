package org.tonstudio.tact.lang.psi.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.util.PsiTreeUtil
import org.tonstudio.tact.lang.TactLanguage
import org.tonstudio.tact.lang.psi.*

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

    fun createStringLiteral(project: Project, text: String): TactStringLiteral {
        return PsiTreeUtil.findChildOfType(
            createFileFromText(project, "fn main() { $text }"),
            TactStringLiteral::class.java
        )!!
    }

    fun createNewLine(project: Project): PsiElement {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n")
    }

    fun createDoubleNewLine(project: Project): PsiElement {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n\n")
    }

    fun createImportList(project: Project, name: String): TactImportList? {
        val file = createFileFromText(project, "import \"$name\"")
        return file.getImportList()
    }

    fun createFieldDeclaration(project: Project, name: String, type: String): PsiElement {
        val file = createFileFromText(project, "struct S {\n\t$name: $type;\n}")
        return file.getStructs().firstOrNull()?.structType?.fieldList?.firstOrNull()?.parent
            ?: error("Impossible situation! Parser is broken.")
    }

    fun createNone(project: Project): TactLiteral {
        val file = createFileFromText(project, "fn main() { a := none }")
        return PsiTreeUtil.findChildOfType(file, TactLiteral::class.java)!!
    }

}
