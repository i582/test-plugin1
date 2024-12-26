package org.ton.tact.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.ton.tact.lang.psi.TactEmbeddedDefinition
import org.ton.tact.lang.psi.impl.TactEmbeddedDefinitionImpl
import org.ton.tact.lang.stubs.TactEmbeddedDefinitionStub

class TactEmbeddedDefinitionStubElementType(name: String) :
    TactNamedStubElementType<TactEmbeddedDefinitionStub, TactEmbeddedDefinition>(name) {

    override fun createPsi(stub: TactEmbeddedDefinitionStub): TactEmbeddedDefinition {
        return TactEmbeddedDefinitionImpl(stub, this)
    }

    override fun createStub(psi: TactEmbeddedDefinition, parentStub: StubElement<*>?): TactEmbeddedDefinitionStub {
        return TactEmbeddedDefinitionStub(parentStub, this, psi.name)
    }

    override fun serialize(stub: TactEmbeddedDefinitionStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactEmbeddedDefinitionStub {
        return TactEmbeddedDefinitionStub(parentStub, this, dataStream.readName())
    }
}
