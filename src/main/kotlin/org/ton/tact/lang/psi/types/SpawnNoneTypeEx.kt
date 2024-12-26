package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class TactNoneTypeEx private constructor() : TactBaseTypeEx() {
    override fun toString(): String = "none"

    override fun qualifiedName(): String = "none"

    override fun readableName(context: PsiElement, detailed: Boolean): String = "none"

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean =
        rhs is TactNoneTypeEx

    override fun isEqual(rhs: TactTypeEx): Boolean = rhs is TactNoneTypeEx

    override fun accept(visitor: TactTypeVisitor) {
        if (!visitor.enter(this)) {
            return
        }
    }

    override fun substituteGenerics(nameMap: Map<String, TactTypeEx>): TactTypeEx = this

    companion object {
        val INSTANCE = TactNoneTypeEx()
    }
}
