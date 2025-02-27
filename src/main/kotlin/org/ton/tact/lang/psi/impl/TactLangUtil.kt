package org.ton.tact.lang.psi.impl

import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.types.*
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.stubs.index.TactMethodIndex

object TactLangUtil {
    private fun getStubFile(name: String, stubDir: VirtualFile, psiManager: PsiManager): TactFile? {
        val stubFile = stubDir.findChild(name) ?: return null
        return psiManager.findFile(stubFile) as? TactFile ?: return null
    }

    fun importTypesFromSignature(signature: TactSignature, context: TactFile) {
        val rawTypes = signature.parameters.paramDefinitionList.mapNotNull { it.type } + signature.result?.type
        val types = findTypesForImport(rawTypes.map { it.toEx() }, context.getModuleQualifiedName())
        if (types.isEmpty()) {
            return
        }

        types.forEach { context.addImport(it.module(), null) }
    }

    private fun findTypesForImport(types: List<TactTypeEx>, currentModule: String): MutableSet<TactTypeEx> {
        val result = mutableSetOf<TactTypeEx>()
        types.forEach { type ->
            type.accept(object : TactTypeVisitor {
                override fun enter(type: TactTypeEx): Boolean {
                    if (type is TactImportableTypeEx) {
                        // type from current module no need to import
                        if (currentModule == type.module() || type.isBuiltin()) {
                            return true
                        }

                        result.add(type)
                    }

                    return true
                }
            })
        }

        return result
    }

    /**
     * Use TactTypeEx.methodsList(project, visited)
     */
    fun getMethodList(project: Project, type: TactTypeEx): List<TactNamedElement> {
        return CachedValuesManager.getManager(project).getCachedValue(type) {
            CachedValueProvider.Result.create(
                calcMethods(project, type), PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    private fun calcMethods(project: Project, type: TactTypeEx): List<TactNamedElement> {
        val typeName = getTypeName(type)
        val moduleName = if (typeName == "Array" || typeName == "Map") "builtin" else type.module()
        if (moduleName.isEmpty() || typeName.isEmpty()) return emptyList()
        val key = "$moduleName.$typeName"

        val dir = type.containingModule(project)
        val scope = if (dir != null && dir.isValid && moduleName != "stubs") {
            val module = TactModule.fromDirectory(dir)
            module.getScope()
        } else {
            GlobalSearchScope.allScope(project)
        }

        val declarations = TactMethodIndex.find(key, project, scope)
        if (declarations.isEmpty()) return emptyList()
        if (declarations.size == 1 || type !is TactResolvableTypeEx<*>) {
            return declarations.toList()
        }
        return declarations.toList()
    }

    fun getMethodListNative(project: Project, type: TactType): List<TactNamedElement> {
        val spec = CompletionUtil.getOriginalOrSelf(type)
        return CachedValuesManager.getCachedValue(spec) {
            CachedValueProvider.Result.create(
                calcMethodsNative(project, spec), moduleModificationTracker(spec)
            )
        }
    }

    private fun calcMethodsNative(project: Project, type: TactType): List<TactNamedElement> {
        val typeName = getTypeNameNative(type)

        val typeFile = type.containingFile as TactFile
        val moduleName = typeFile.getModuleQualifiedName()
        if (typeName.isEmpty()) return emptyList()
        val key = typeName

        val dir = typeFile.containingDirectory
        val scope = if (dir != null && dir.isValid && moduleName != "stubs") {
            val module = TactModule.fromDirectory(dir)
            module.getScope()
        } else {
            GlobalSearchScope.allScope(project)
        }

        val declarations = TactMethodIndex.find(key, project, scope)
        if (declarations.isEmpty()) return emptyList()
        if (declarations.size == 1 || type !is TactResolvableTypeEx<*>) {
            return declarations.toList()
        }

        return declarations.toList()
    }

    private fun getTypeNameNative(o: TactType): String {
        if (o is TactMapType) {
            return "map"
        }

        return o.identifier?.text ?: ""
    }

    private fun getTypeName(o: TactTypeEx): String {
        if (o is TactOptionTypeEx) {
            return getTypeName(o.inner)
        }

        return o.toString()
    }

    fun getDefaultValue(element: PsiElement, type: TactTypeEx?): String = when (type) {
        is TactPrimitiveTypeEx -> {
            when (type.name) {
                TactPrimitiveTypes.BOOL    -> "false"
                TactPrimitiveTypes.STRING  -> "\"\""
                TactPrimitiveTypes.NULL    -> "null"
                TactPrimitiveTypes.INT     -> "0"
                TactPrimitiveTypes.VOID    -> ""
                TactPrimitiveTypes.BUILDER -> "beginCell()"
                TactPrimitiveTypes.CELL    -> "emptyCell()"
                TactPrimitiveTypes.ADDRESS -> "address()"
                TactPrimitiveTypes.SLICE   -> "emptySlice()"
            }
        }

        is TactStringTypeEx    -> "\"\""
        is TactMapTypeEx       -> "emptyMap()"
        is TactFunctionTypeEx  -> "null"
        is TactStructTypeEx    -> type.readableName(element) + "{}"
        is TactTraitTypeEx     -> type.readableName(element) + "{}"
        is TactNullTypeEx      -> "null"
        is TactOptionTypeEx    -> "null"
        is TactAnyTypeEx       -> "0"
        else                   -> "0"
    }
}
