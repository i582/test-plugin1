package org.ton.tact.lang.stubs.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.Processor
import org.ton.tact.lang.TactFileElementType
import org.ton.tact.lang.psi.TactNamedElement

class TactClassLikeIndex : StringStubIndexExtension<TactNamedElement>() {
    companion object {
        val KEY = StubIndexKey.createIndexKey<String, TactNamedElement>("tact.class.like")

        fun find(
            name: String,
            project: Project,
            scope: GlobalSearchScope? = GlobalSearchScope.allScope(project),
        ): Collection<TactNamedElement> {
            return StubIndex.getElements(KEY, name, project, scope, TactNamedElement::class.java)
        }

        fun process(
            name: String,
            project: Project,
            scope: GlobalSearchScope?,
            processor: Processor<TactNamedElement>,
        ): Boolean {
            return StubIndex.getInstance().processElements(
                KEY, name, project, scope,
                TactNamedElement::class.java, processor
            )
        }
    }

    override fun getVersion() = TactFileElementType.VERSION + 3

    override fun getKey() = KEY
}
