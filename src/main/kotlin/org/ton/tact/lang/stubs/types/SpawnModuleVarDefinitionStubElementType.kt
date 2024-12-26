package org.ton.tact.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.ArrayFactory
import org.ton.tact.lang.psi.TactModuleVarDefinition
import org.ton.tact.lang.psi.impl.TactModuleVarDefinitionImpl
import org.ton.tact.lang.stubs.TactModuleVarDefinitionStub

class TactModuleVarDefinitionStubElementType(name: String) : TactNamedStubElementType<TactModuleVarDefinitionStub, TactModuleVarDefinition>(name) {
    override fun createPsi(stub: TactModuleVarDefinitionStub): TactModuleVarDefinition {
        return TactModuleVarDefinitionImpl(stub, this)
    }

    override fun createStub(psi: TactModuleVarDefinition, parentStub: StubElement<*>?): TactModuleVarDefinitionStub {
//        val type = psi.expression?.getType(null)?.qualifiedName() ?: ""
        val value = psi.expression?.text ?: ""
        return TactModuleVarDefinitionStub(parentStub, this, psi.name, true, value, "")
    }

    override fun serialize(stub: TactModuleVarDefinitionStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
        dataStream.writeBoolean(stub.isPublic)
        dataStream.writeName(stub.value)
        dataStream.writeName(stub.type)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactModuleVarDefinitionStub {
        return TactModuleVarDefinitionStub(
            parentStub,
            this,
            dataStream.readName(),
            dataStream.readBoolean(),
            dataStream.readNameString() ?: "",
            dataStream.readNameString() ?: ""
        )
    }

    companion object {
        private val EMPTY_ARRAY: Array<TactModuleVarDefinition?> = arrayOfNulls(0)
        val ARRAY_FACTORY = ArrayFactory<TactModuleVarDefinition> { count: Int ->
            if (count == 0) EMPTY_ARRAY else arrayOfNulls<TactModuleVarDefinition>(count)
        }
    }
}
