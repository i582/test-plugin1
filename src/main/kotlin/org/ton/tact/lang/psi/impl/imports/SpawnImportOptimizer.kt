package org.ton.tact.lang.psi.impl.imports

import com.intellij.lang.ImportOptimizer
import com.intellij.openapi.util.EmptyRunnable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.TactTypes
import org.ton.tact.lang.psi.*

class TactImportOptimizer : ImportOptimizer {
    override fun supports(file: PsiFile) = file is TactFile

    override fun processFile(file: PsiFile): Runnable {
        if (file !is TactFile) {
            return EmptyRunnable.getInstance()
        }

        val imports = file.getImports()
        val importsToDelete = TactCodeInsightUtil.findDuplicateImports(imports)
        val unusedImports = collectUnusedImports(file, imports)
        importsToDelete += unusedImports.unusedImports

        return Runnable {
            if (importsToDelete.isNotEmpty()) {
                val manager = PsiDocumentManager.getInstance(file.project)
                val document = manager.getDocument(file)
                if (document != null) {
                    manager.commitDocument(document)
                }
            }
            for (importEntry in importsToDelete) {
                if (!importEntry.isValid) continue
                deleteImportSpec(importEntry)
            }
        }
    }

    private fun deleteImportSpec(importSpec: TactImportDeclaration?) {
        val importDeclaration = importSpec?.parent ?: return
        importDeclaration.delete()
    }

    companion object {
        data class UnusedImports(
            val unusedImports: MutableSet<TactImportDeclaration>,
        )

        fun collectUnusedImports(file: TactFile, imports: List<TactImportDeclaration>): UnusedImports {
            val usedImports = mutableMapOf<String, TactImportDeclaration>()
            val importsToDelete = mutableSetOf<TactImportDeclaration>()

            file.accept(object : TactRecursiveElementVisitor() {
                override fun visitReferenceExpression(o: TactReferenceExpression) {
                    super.visitReferenceExpression(o)
                    checkReferenceExpression(o)
                }

                override fun visitTypeReferenceExpression(o: TactTypeReferenceExpression) {
                    super.visitTypeReferenceExpression(o)
                    checkReferenceExpression(o)
                }

                private fun checkReferenceExpression(o: TactReferenceExpressionBase) {
                    var qualifier = (o.getQualifier() ?: o) as? TactReferenceExpressionBase
                    if (qualifier != null) {
                        var prevQualifier: TactReferenceExpressionBase?
                        while (true) {
                            prevQualifier = qualifier?.getQualifier() as? TactReferenceExpressionBase
                            if (prevQualifier == null) {
                                break
                            }

                            qualifier = prevQualifier
                        }

                        val name = qualifier?.getIdentifier()?.text ?: return
                        if (usedImports.contains(name)) {
                            return
                        }

                        val importSpec = imports.firstOrNull {
                            it.path == name
                        }

                        if (importSpec != null) {
                            usedImports[name] = importSpec
                        }
                    }
                }
            })

            imports.forEach { spec ->
                if (!usedImports.contains(spec.path)) {
                    importsToDelete.add(spec)
                }
            }

            return UnusedImports(importsToDelete)
        }
    }
}
