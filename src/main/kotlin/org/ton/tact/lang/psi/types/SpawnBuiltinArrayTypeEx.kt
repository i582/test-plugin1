package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import org.ton.tact.configurations.TactConfiguration
import org.ton.tact.lang.psi.TactFile
import org.ton.tact.lang.psi.TactStructType

class TactBuiltinArrayTypeEx private constructor(anchor: PsiElement?) : TactStructTypeEx("Array", anchor) {
    override val moduleName: String = "builtin"

    override fun toString() = "Array"

    override fun qualifiedName(): String  = "builtin.Array"

    override fun readableName(context: PsiElement, detailed: Boolean) = "Array"

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true

        return when (rhs) {
            is TactBuiltinArrayTypeEx -> true
            is TactArrayTypeEx        -> true
            else                       -> false
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
        fun getInstance(project: Project, structType: TactStructType? = null): TactBuiltinArrayTypeEx {
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            if (structType != null) {
                INSTANCE = TactBuiltinArrayTypeEx(structType)
                return INSTANCE!!
            }

            val builtin = TactConfiguration.getInstance(project).builtinLocation
            if (builtin != null) {
                val arrayFile = builtin.findChild("array.sp")
                if (arrayFile != null) {
                    val psiFile = arrayFile.findPsiFile(project)
                    if (psiFile is TactFile) {
                        val arrayStruct = psiFile.getStructs().find { it.name == "Array" }
                        if (arrayStruct != null) {
                            INSTANCE = TactBuiltinArrayTypeEx(arrayStruct.structType)
                            return INSTANCE!!
                        }
                    }
                }
            }

            return TactBuiltinArrayTypeEx(null)
        }

        private var INSTANCE: TactBuiltinArrayTypeEx? = null
    }
}
