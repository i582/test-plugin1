package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class TactBuiltinMapTypeEx private constructor() : TactStructTypeEx("Map", null) {
    override val moduleName: String = "builtin"

    override fun toString() = "Map"

    override fun qualifiedName(): String = "builtin.Map"

    override fun readableName(context: PsiElement, detailed: Boolean) = "Map"

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true

        return when (rhs) {
            is TactBuiltinMapTypeEx -> true
            is TactMapTypeEx        -> true
            else                     -> false
        }
    }

    override fun isEqual(rhs: TactTypeEx): Boolean = true

    override fun accept(visitor: TactTypeVisitor) {
        if (!visitor.enter(this)) {
            return
        }
    }

    override fun substituteGenerics(nameMap: Map<String, TactTypeEx>): TactTypeEx = this

    companion object {
        val INSTANCE = TactBuiltinMapTypeEx()
    }
}
