package org.ton.tact.lang.psi.types

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.TactLangUtil

@Suppress("PropertyName")
abstract class TactBaseTypeEx(protected val anchor: PsiElement? = null) : UserDataHolderBase(), TactTypeEx {
    protected val UNKNOWN_TYPE = "unknown"
    protected val ANON = "anon"
    protected val containingFile = anchor?.containingFile as? TactFile
    protected open val moduleName = containingFile?.getModuleQualifiedName() ?: ""

    override fun anchor(project: Project) = anchor

    override fun module() = moduleName

    override fun name(): String {
        return qualifiedName().removePrefix(moduleName).removePrefix(".")
    }

    override fun isBuiltin() = moduleName == TactCodeInsightUtil.BUILTIN_MODULE || moduleName == TactCodeInsightUtil.STUBS_MODULE

    override fun containingModule(project: Project): PsiDirectory? {
        val anchor = anchor ?: return null
        return anchor.containingFile.containingDirectory
    }

    protected fun String?.safeAppend(str: String?): String {
        if (str == null) return this ?: ""
        return if (this == null) str else this + str
    }

    protected fun String?.safeAppendIf(cond: Boolean, str: String?): String {
        if (!cond) return this ?: ""
        if (str == null) return this ?: ""
        return if (this == null) str else this + str
    }

    protected fun String?.safeAppend(type: TactTypeEx?): String {
        return this.safeAppend(type?.toString())
    }

    protected fun TactTypeEx?.safeAppend(str: String): String {
        return this?.toString().safeAppend(str)
    }

    protected fun prioritize(context: PsiElement?, variants: Collection<TactNamedElement>): TactNamedElement? {
        val containingFile = context?.containingFile?.originalFile
        val containingDir = containingFile?.containingDirectory
        val priorityMap = mutableMapOf<Int, TactNamedElement>()

        variants.forEach { variant ->
            val variantContainingFile = variant.containingFile?.originalFile as? TactFile ?: return@forEach
            val variantContainingDir = variantContainingFile.containingDirectory

            val priority = when {
                variantContainingFile == containingFile                     -> 1000 // local variant has the highest priority
                variantContainingDir == containingDir                       -> 100 // same directory variant has the second-highest priority
                variantContainingFile.virtualFile.path.contains("examples") -> 10
                else                                                        -> 0 // other variants have the lowest priority
            }

            priorityMap[priority] = variant
        }

        // find the highest priority
        val maxPriority = priorityMap.keys.maxOrNull() ?: 0
        return priorityMap[maxPriority]
    }

    override fun ownMethodsList(project: Project): List<TactNamedElement> {
        val anchor = anchor
        if (anchor is TactType) {
            return TactLangUtil.getMethodListNative(project, anchor)
        }
        val resolvedAnchor = anchor(project)
        if (resolvedAnchor is TactType) {
            return TactLangUtil.getMethodListNative(project, resolvedAnchor)
        }

        return TactLangUtil.getMethodList(project, this)
    }

    override fun methodsList(project: Project, visited: MutableSet<TactTypeEx>): List<TactNamedElement> {
        if (this in visited) return emptyList()
        val ownMethods = this.ownMethodsList(project)
        val unwrapped = unwrapReference().unwrapAlias()
        val inheritedMethods = if (unwrapped != this) unwrapped.methodsList(project, visited) else emptyList()

        visited.add(this)
        return ownMethods + inheritedMethods
    }

    override fun findMethod(project: Project, name: String): TactNamedElement? {
        return methodsList(project).find { it.name == name }
    }

    companion object {
        val TactTypeEx?.isAny: Boolean
            get() = when (this) {
                is TactAnyTypeEx     -> true
                is TactUnknownTypeEx -> true
                else                 -> false
            }

        fun TactTypeEx.unwrapReference(): TactTypeEx {
            return this
        }

        fun TactTypeEx.unwrapGenericInstantiation(): TactTypeEx {
            return this
        }

        fun TactTypeEx.unwrapAlias(): TactTypeEx {
            return this
        }

        fun TactType?.toEx(visited: MutableMap<TactType, TactTypeEx> = mutableMapOf()): TactTypeEx {
            if (this == null) {
                return TactUnknownTypeEx.INSTANCE
            }

            if (this in visited) {
                return visited[this]!!
            }

            if (visited.isEmpty()) {
                return toExNoVisited()
            }

            val type = toExInner(visited)

            visited[this] = type
            return type
        }

        private fun TactType.toExNoVisited(): TactTypeEx {
            return CachedValuesManager.getCachedValue(this) {
                val value = toExNoVisitedImpl()
                CachedValueProvider.Result.create(value, this)
            }
        }

        private fun TactType.toExNoVisitedImpl(): TactTypeEx {
            return toExInner(mutableMapOf())
        }

        private fun TactType.toExInner(visited: MutableMap<TactType, TactTypeEx>): TactTypeEx {
            val type = resolveType()
            if (type is TactStructType && type.parent is TactStructDeclaration) {
                return TactStructTypeEx(parentName(type), type)
            }

            return when (type) {
                is TactTraitType     -> TactTraitTypeEx(parentName(type), type)
                is TactMessageType   -> TactMessageTypeEx(parentName(type), type)
                is TactPrimitiveType -> TactPrimitiveTypeEx(TactPrimitiveTypes.find(type.text) ?: TactPrimitiveTypes.INT)
                is TactOptionType    -> TactOptionTypeEx(type.type.toEx(visited), type)
                is TactMapType       -> TactMapTypeEx(type.keyType.toEx(visited), type.valueType.toEx(visited), type)
                is TactTupleType     -> TactTupleTypeEx(
                    (type.typeListNoPin?.typeList ?: emptyList()).map { it.toEx(visited) },
                    type
                )

                is TactFunctionType  -> TactFunctionTypeEx.from(type) ?: TactUnknownTypeEx.INSTANCE
                else                 -> {
                    if (type.text == "void") {
                        return TactVoidTypeEx.INSTANCE
                    }

                    val primitive = TactPrimitiveTypes.find(type.text)
                    if (primitive != null) {
                        return TactPrimitiveTypeEx(primitive, type)
                    }

                    // only for tests
                    // TODO: remove
                    if (type.text == "string") {
                        return TactPrimitiveTypeEx.STRING
                    }
                    if (type.text == "Int") {
                        return TactPrimitiveTypeEx.INT
                    }
                    if (type.text == "bool") {
                        return TactPrimitiveTypeEx.BOOL
                    }
                    TactUnknownTypeEx.INSTANCE
                }
            }
        }

        private fun parentName(type: PsiElement) = (type.parent as TactNamedElement).name!!
    }
}
