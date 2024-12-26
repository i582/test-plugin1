package org.ton.tact.lang.psi

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.psi.types.TactStructTypeEx
import org.ton.tact.lang.psi.types.TactTypeEx

@Service(Service.Level.PROJECT)
class TactStubsManager(private val project: Project) {
    private val resolvedFiles: MutableMap<String, TactFile> = LinkedHashMap(20)
    private val resolvedTypes: MutableMap<String, TactTypeEx> = LinkedHashMap(20)
    private val isUnitTest = ApplicationManager.getApplication().isUnitTestMode

    fun findFile(name: String): TactFile? {
        if (resolvedFiles.containsKey(name) && !isUnitTest) {
            return resolvedFiles[name]
        }
        // TODO:
//        val stubDir = TactConfiguration.getInstance(project).stubsLocation

//        val parts = name.split("/")
//        var virtualFile = stubDir
//        for (part in parts) {
//            virtualFile = virtualFile?.findChild(part) ?: return null
//        }
//
//        if (virtualFile == null) {
//            return null
//        }
//
//        val file = PsiManager.getInstance(project).findFile(virtualFile) as? TactFile ?: return null
//        resolvedFiles[name] = file
        return null
    }

    fun findStructType(fileName: String, name: String): TactStructTypeEx? {
        if (resolvedTypes.containsKey(name) && !isUnitTest) {
            return resolvedTypes[name] as? TactStructTypeEx
        }
        val psiFile = findFile(fileName) ?: return null
        val struct = psiFile.getStructs()
            .firstOrNull { it.name == name } ?: return null
        val type = struct.structType.toEx() as? TactStructTypeEx ?: return null
        resolvedTypes[name] = type
        return type
    }

    fun getErrVariableDefinition(): TactModuleVarDefinition? {
        val stubFile = findFile("errors.sp") ?: return null
        return stubFile.getModuleVars().firstOrNull { it.name == "err" }
    }

    companion object {
        fun getInstance(project: Project) = project.service<TactStubsManager>()
    }
}
