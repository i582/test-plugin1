package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.psi.TactContractDeclaration
import org.ton.tact.lang.stubs.index.TactNamesIndex

open class TactContractTypeEx(private val name: String, anchor: PsiElement?) :
    TactResolvableTypeEx<TactContractDeclaration>(anchor), TactImportableTypeEx {

    override fun toString() = name

    override fun qualifiedName(): String {
        if (moduleName.isEmpty()) {
            return name
        }
        return "$moduleName.$name"
    }

    override fun readableName(context: PsiElement, detailed: Boolean) =
        TactCodeInsightUtil.getQualifiedName(context, anchor!!, qualifiedName())

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true
        if (rhs is TactContractTypeEx) {
            return this.qualifiedName() == rhs.qualifiedName()
        }
        return false
    }

    override fun isEqual(rhs: TactTypeEx): Boolean {
        return rhs is TactContractTypeEx && qualifiedName() == rhs.qualifiedName()
    }

    override fun accept(visitor: TactTypeVisitor) {
        visitor.enter(this)
    }

    override fun substituteGenerics(nameMap: Map<String, TactTypeEx>): TactTypeEx = this

    override fun resolveImpl(project: Project): TactContractDeclaration? {
        val anchor = anchor(project)
        if (anchor != null && anchor.isValid) {
            val parent = anchor.parent
            if (parent is TactContractDeclaration && parent.isValid) {
                return parent
            }
        }

        val variants = TactNamesIndex.find(qualifiedName(), project, null)
        if (variants.size == 1) {
            return variants.first() as? TactContractDeclaration
        }

        return prioritize(containingFile, variants) as? TactContractDeclaration
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TactContractDeclaration

        return name == other.name
    }

    override fun hashCode(): Int = name.hashCode()
}
