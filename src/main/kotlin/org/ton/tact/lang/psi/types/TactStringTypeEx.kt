package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import org.ton.tact.configurations.TactConfiguration
import org.ton.tact.lang.psi.TactFile
import org.ton.tact.lang.psi.impl.TactElementFactory

class TactStringTypeEx private constructor(anchor: PsiElement) : TactStructTypeEx("string", anchor) {
    override val moduleName: String = "builtin"

    override fun toString() = "string"

    override fun qualifiedName(): String = "builtin.string"

    override fun readableName(context: PsiElement, detailed: Boolean) = "string"

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true
        return rhs is TactStringTypeEx
    }

    override fun isEqual(rhs: TactTypeEx): Boolean = rhs is TactStringTypeEx

    override fun accept(visitor: TactTypeVisitor) {
        if (!visitor.enter(this)) {
            return
        }
    }

    override fun substituteGenerics(nameMap: Map<String, TactTypeEx>): TactTypeEx = this

    companion object {
        fun getInstance(project: Project): TactStringTypeEx {
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            val builtin = TactConfiguration.getInstance(project).builtinLocation
            if (builtin != null) {
                val stringFile = builtin.findChild("string.sp")
                if (stringFile != null) {
                    val psiFile = stringFile.findPsiFile(project)
                    if (psiFile is TactFile) {
                        val stringStruct = psiFile.getStructs().find { it.name == "string" }
                        if (stringStruct != null) {
                            INSTANCE = TactStringTypeEx(stringStruct.structType)
                            return INSTANCE!!
                        }
                    }
                }
            }

            val pseudoType = TactElementFactory.createType(project, "string")
            return TactStringTypeEx(pseudoType)
        }

        private var INSTANCE: TactStringTypeEx? = null
    }
}
