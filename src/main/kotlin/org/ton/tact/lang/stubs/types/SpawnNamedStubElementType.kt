package org.ton.tact.lang.stubs.types

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubIndexKey
import org.ton.tact.lang.psi.TactNamedElement
import org.ton.tact.lang.psi.TactPsiTreeUtil.parentStubOfType
import org.ton.tact.lang.stubs.TactFileStub
import org.ton.tact.lang.stubs.TactFunctionDeclarationStub
import org.ton.tact.lang.stubs.TactNamedStub
import org.ton.tact.lang.stubs.index.TactNamesIndex

abstract class TactNamedStubElementType<S : TactNamedStub<T>, T : TactNamedElement>(debugName: String) :
    TactStubElementType<S, T>(debugName) {

    override fun shouldCreateStub(node: ASTNode): Boolean {
        if (!super.shouldCreateStub(node)) return false
        val psi = node.psi as? TactNamedElement ?: return false
        val name = psi.name ?: return false
        return name.isNotEmpty()
    }

    override fun indexStub(stub: S, sink: IndexSink) {
        val name = stub.name ?: return
        if (shouldIndex() && name.isNotEmpty()) {
            val file = stub.parentStubOfType<TactFileStub>()
            val moduleName = file?.getModuleQualifiedName() ?: ""
            val indexingName = if (moduleName.isNotEmpty()) "$moduleName.$name" else name

            sink.occurrence(TactNamesIndex.KEY, indexingName)

            for (key in getExtraIndexKeys()) {
                sink.occurrence(key, indexingName)
            }
        }
    }

    private fun shouldIndex() = true

    protected open fun getExtraIndexKeys() = emptyList<StubIndexKey<String, out TactNamedElement>>()
}
