package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnchor
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.ton.tact.configurations.TactConfiguration

class TactPrimitiveTypeEx(val name: TactPrimitiveTypes, anchor: PsiElement? = null) : TactBaseTypeEx(anchor) {
    override fun module(): String = "builtin"

    override fun toString(): String = name.value

    override fun qualifiedName(): String = name.value

    override fun readableName(context: PsiElement, detailed: Boolean): String = name.value

    fun isNumeric(): Boolean {
        return name.numeric
    }

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        return when (rhs) {
            is TactAnyTypeEx       -> true
            is TactUnknownTypeEx   -> true
            is TactPrimitiveTypeEx -> {
                if (name == rhs.name) {
                    return true
                }

//                val lhsType = name
//                val rhsType = rhs.name

//                val lhsName = lhsType.value
//                val rhsName = rhsType.value
//
//                if (lhsType == TactPrimitiveTypes.INT_LITERAL) {
//                    if (rhsName.startsWith("i") || rhsName.startsWith("u")) {
//                        return true
//                    }
//                }
//
//                if (rhsType == TactPrimitiveTypes.INT_LITERAL) {
//                    if (lhsName.startsWith("i") || lhsName.startsWith("u")) {
//                        return true
//                    }
//                }
//
//                if (lhsName.startsWith("i") && rhsName.startsWith("i")) {
//                    return rhs.name.size <= name.size
//                }
//
//                if (lhsName.startsWith("u") && rhsName.startsWith("u")) {
//                    return rhs.name.size <= name.size
//                }
//
//                if (lhsName.startsWith("f") && rhsName.startsWith("f")) {
//                    return rhs.name.size <= name.size
//                }

                // for now
                true
            }

            else                   -> false
        }
    }

    override fun isEqual(rhs: TactTypeEx): Boolean {
        if (rhs !is TactPrimitiveTypeEx) {
            return false
        }
        return name.value == rhs.name.value
    }

    override fun accept(visitor: TactTypeVisitor) {
        if (!visitor.enter(this)) {
            return
        }
    }

    override fun substituteGenerics(nameMap: Map<String, TactTypeEx>): TactTypeEx = this

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TactPrimitiveTypeEx

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun anchor(project: Project): PsiElement? {
        return anchor
    }

    override fun containingModule(project: Project): PsiDirectory? {
        return getModuleDirectory(project)
    }

    companion object {
        val BOOL = TactPrimitiveTypeEx(TactPrimitiveTypes.BOOL)
        val INT = TactPrimitiveTypeEx(TactPrimitiveTypes.INT)
        val UINT = TactPrimitiveTypeEx(TactPrimitiveTypes.UINT)
        val NULL = TactPrimitiveTypeEx(TactPrimitiveTypes.NULL)

        fun get(name: String): TactPrimitiveTypeEx? {
            return when (name) {
                "bool" -> BOOL
                "i32"  -> INT
                "u32"  -> UINT
                "null" -> NULL
                else   -> null
            }
        }

        fun getModuleDirectory(project: Project): PsiDirectory? {
            if (NATIVE_TYPE_MODULE != null) {
                return NATIVE_TYPE_MODULE!!
            }

            val builtin = TactConfiguration.getInstance(project).builtinLocation ?: return null
            NATIVE_TYPE_MODULE = PsiManager.getInstance(project).findDirectory(builtin)
            return NATIVE_TYPE_MODULE
        }

        private var NATIVE_TYPE_MODULE: PsiDirectory? = null
    }
}
