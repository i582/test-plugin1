package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import org.ton.tact.configurations.TactConfiguration
import org.ton.tact.lang.psi.TactFile
import org.ton.tact.lang.psi.TactStructType

class TactBuiltinOptionTypeEx private constructor(anchor: PsiElement?) : TactStructTypeEx("Option", anchor) {
    override val moduleName: String = "builtin"

    override fun toString() = "Option"

    override fun qualifiedName(): String = "builtin.Option"

    override fun readableName(context: PsiElement, detailed: Boolean) = "Option"

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true

        return when (rhs) {
            is TactBuiltinOptionTypeEx -> true
            is TactNoneTypeEx          -> true
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
        fun getInstance(project: Project, structType: TactStructType? = null): TactBuiltinOptionTypeEx {
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            if (structType != null) {
                INSTANCE = TactBuiltinOptionTypeEx(structType)
                return INSTANCE!!
            }

            val builtin = TactConfiguration.getInstance(project).builtinLocation
            if (builtin != null) {
                val arrayFile = builtin.findChild("option.sp")
                if (arrayFile != null) {
                    val psiFile = arrayFile.findPsiFile(project)
                    if (psiFile is TactFile) {
                        val arrayStruct = psiFile.getStructs().find { it.name == "Option" }
                        if (arrayStruct != null) {
                            INSTANCE = TactBuiltinOptionTypeEx(arrayStruct.structType)
                            return INSTANCE!!
                        }
                    }
                }
            }

            return TactBuiltinOptionTypeEx(null)
        }

        private var INSTANCE: TactBuiltinOptionTypeEx? = null
    }
}
