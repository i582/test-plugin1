package org.ton.tact.lang.stubs.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.Processor
import com.intellij.util.indexing.IdFilter
import org.ton.tact.lang.TactFileElementType
import org.ton.tact.lang.psi.TactFunctionDeclaration

class TactMethodIndex : StringStubIndexExtension<TactFunctionDeclaration>() {
    companion object {
        val KEY = StubIndexKey.createIndexKey<String, TactFunctionDeclaration>("tact.method")
        
        fun find(
            name: String, project: Project,
            scope: GlobalSearchScope?
        ): Collection<TactFunctionDeclaration> {
            return StubIndex.getElements(KEY, name, project, scope, TactFunctionDeclaration::class.java)
        }

        fun process(
            name: String,
            project: Project,
            scope: GlobalSearchScope?,
            idFilter: IdFilter?,
            processor: Processor<TactFunctionDeclaration>
        ): Boolean {
            return StubIndex.getInstance().processElements(
                KEY, name, project, scope, idFilter,
                TactFunctionDeclaration::class.java, processor
            )
        }
    }

    override fun getVersion() = TactFileElementType.VERSION + 2

    override fun getKey() = KEY
}
