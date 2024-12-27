package org.ton.tact.ide.codeInsight

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.ton.tact.lang.psi.*
import org.ton.tact.utils.parentNth
import org.ton.tact.utils.parentOfTypeWithStop

object TactCodeInsightUtil {
    private const val MAIN_MODULE = "main"
    const val STUBS_MODULE = "stubs"

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

    fun takeZeroArguments(owner: TactSignatureOwner): Boolean {
        return owner.getSignature()?.parameters?.paramDefinitionList?.isEmpty() ?: false
    }

    fun takeSingleArgument(owner: TactSignatureOwner): Boolean {
        return owner.getSignature()?.parameters?.paramDefinitionList?.size == 1
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

        if (name.startsWith("$MAIN_MODULE.")) {
            return name.removePrefix("$MAIN_MODULE.")
        }

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
