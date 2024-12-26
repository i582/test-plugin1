package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.ton.tact.configurations.TactConfiguration
import org.ton.tact.lang.psi.TactFile
import org.ton.tact.lang.psi.TactType

open class TactArrayTypeEx(val inner: TactTypeEx, anchor: PsiElement) : TactBaseTypeEx(anchor) {
    override fun toString() = "[]".safeAppend(inner)

    override fun qualifiedName(): String = "[]".safeAppend(inner.qualifiedName())

    override fun readableName(context: PsiElement, detailed: Boolean) = "[]".safeAppend(inner.readableName(context))

    override fun module() = inner.module()

    override fun isAssignableFrom(project: Project, rhs: TactTypeEx, kind: AssignableKind): Boolean {
        if (rhs.isAny) return true

        if (rhs is TactArrayTypeEx) {
            return inner.isEqual(rhs.inner)
        }

        return false
    }

    override fun isEqual(rhs: TactTypeEx): Boolean {
        return rhs is TactArrayTypeEx && inner.isEqual(rhs.inner)
    }

    override fun accept(visitor: TactTypeVisitor) {
        if (!visitor.enter(this)) {
            return
        }

        inner.accept(visitor)
    }

    override fun substituteGenerics(nameMap: Map<String, TactTypeEx>): TactTypeEx {
        return TactArrayTypeEx(inner.substituteGenerics(nameMap), anchor!!)
    }

    override fun containingModule(project: Project): PsiDirectory? {
        return getModuleDirectory(project)
    }

    override fun anchor(project: Project): PsiElement? {
        if (anchor is TactType) {
            return anchor
        }
        return resolveAnchor(project)
    }

    private fun resolveAnchor(project: Project): PsiElement? {
        val builtin = TactConfiguration.getInstance(project).builtinLocation
        if (builtin != null) {
            val stringFile = builtin.findChild("array.sp")
            if (stringFile != null) {
                val psiFile = stringFile.findPsiFile(project)
                if (psiFile is TactFile) {
                    val struct = psiFile.getStructs().find { it.name == "Array" }
                    return struct?.structType
                }
            }
        }
        return null
    }

    companion object {
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
