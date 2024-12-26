package org.ton.tact.ide.codeInsight

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.psi.types.TactTypeEx
import org.ton.tact.utils.parentNth
import org.ton.tact.utils.parentOfTypeWithStop

object TactCodeInsightUtil {
    private const val MAIN_MODULE = "main"
    const val BUILTIN_MODULE = "builtin"
    const val STUBS_MODULE = "stubs"
    private const val ERR_VARIABLE = "err"

    fun getCallExpr(element: PsiElement): TactCallExpr? {
        if (element.parent is TactFieldName) {
            return element.parent.parentNth(4)
        }

        val parentValue = element.parentOfTypeWithStop<TactValue>(TactSignatureOwner::class)
        if (parentValue != null) {
            return parentValue.parentNth(3) ?: element.parentOfTypeWithStop(TactSignatureOwner::class)
        }
        return element.parentOfTypeWithStop(TactSignatureOwner::class)
    }

    fun getCalledParams(callExpr: TactCallExpr?): List<TactTypeEx>? {
        val resolved = callExpr?.resolve() as? TactSignatureOwner ?: return null
        val params = resolved.getSignature()?.parameters?.paramDefinitionList
        return params?.map { it.type.toEx() }
    }

    fun takeZeroArguments(owner: TactSignatureOwner): Boolean {
        return owner.getSignature()?.parameters?.paramDefinitionList?.isEmpty() ?: false
    }

    fun findDuplicateImports(imports: List<TactImportDeclaration>): MutableSet<TactImportDeclaration> {
        val importsToDelete = mutableSetOf<TactImportDeclaration>()
        val importsAsSet = imports.map { it.path to it }

        importsAsSet.forEach { (importName, spec) ->
            if (importsToDelete.contains(spec)) {
                return@forEach
            }

            val importsWithSameName = imports.filter { it.path == importName }
            if (importsWithSameName.size > 1) {
                importsWithSameName.subList(1, importsWithSameName.size).forEach { specToDelete ->
                    importsToDelete.add(specToDelete)
                }
            }
        }

        return importsToDelete
    }

    fun getQualifiedName(context: PsiElement, anchor: PsiElement, name: String): String {
        if (!context.isValid || !anchor.isValid) return name

//        val contextFile = context.containingFile as TactFile
//        val contextModule = contextFile.getModuleQualifiedName()
//
//        val elementFile = anchor.containingFile as TactFile
//        val elementModule = elementFile.getModuleQualifiedName()

        if (name.startsWith("$BUILTIN_MODULE.")) {
            return name.removePrefix("$BUILTIN_MODULE.")
        }

        if (name.startsWith("$MAIN_MODULE.")) {
            return name.removePrefix("$MAIN_MODULE.")
        }

//        if (contextModule == elementModule) {
//            return name.removePrefix("$contextModule.")
//        }
//
//        if (name.startsWith("$contextModule.")) {
//            return name.removePrefix("$contextModule.")
//        }

        val parts = name.split(".")
        if (parts.size < 3) {
            return name
        }

        return parts.subList(parts.size - 2, parts.size).joinToString(".")
    }

    fun sameModule(firstFile: PsiFile, secondFile: PsiFile): Boolean {
        if (firstFile == secondFile) return true

        val containingDirectory = firstFile.containingDirectory
        if (containingDirectory == null || containingDirectory != secondFile.containingDirectory) {
            return false
        }
        // TODO:
//        if (firstFile is TactFile && secondFile is TactFile) {
//            val referenceModule = firstFile.getModuleQualifiedName()
//            val definitionModule = secondFile.getModuleQualifiedName()
//            return referenceModule == definitionModule
//        }
        return true
    }

//    fun sameModule(first: TactCompositeElement, second: TactCompositeElement): Boolean {
//        val firstFile = first.containingFile as TactFile
//        val firstModule = firstFile.getModuleQualifiedName()
//
//        val secondFile = second.containingFile as TactFile
//        val secondModule = secondFile.getModuleQualifiedName()
//
//        return firstModule == secondModule
//    }

}
