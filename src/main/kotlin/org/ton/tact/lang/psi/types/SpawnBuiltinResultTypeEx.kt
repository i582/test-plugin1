package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import org.ton.tact.configurations.TactConfiguration
import org.ton.tact.lang.psi.TactFile
import org.ton.tact.lang.psi.TactStructType

class TactBuiltinResultTypeEx private constructor(anchor: PsiElement?) : TactStructTypeEx("Result", anchor) {
    override val moduleName: String = "builtin"

    override fun toString() = "Result"

    override fun qualifiedName(): String = "builtin.Result"

    override fun readableName(context: PsiElement, detailed: Boolean) = "Result"

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true

        return when (rhs) {
            is TactBuiltinResultTypeEx -> true
            else                        -> false
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
        fun getInstance(project: Project, structType: TactStructType? = null): TactBuiltinResultTypeEx {
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            if (structType != null) {
                INSTANCE = TactBuiltinResultTypeEx(structType)
                return INSTANCE!!
            }

            val builtin = TactConfiguration.getInstance(project).builtinLocation
            if (builtin != null) {
                val stringFile = builtin.findChild("result.sp")
                if (stringFile != null) {
                    val psiFile = stringFile.findPsiFile(project)
                    if (psiFile is TactFile) {
                        val stringStruct = psiFile.getStructs().find { it.name == "Result" }
                        if (stringStruct != null) {
                            INSTANCE = TactBuiltinResultTypeEx(stringStruct.structType)
                            return INSTANCE!!
                        }
                    }
                }
            }

            return TactBuiltinResultTypeEx(null)
        }

        private var INSTANCE: TactBuiltinResultTypeEx? = null
    }
}
