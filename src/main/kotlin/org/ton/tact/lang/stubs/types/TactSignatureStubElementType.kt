package org.ton.tact.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.ton.tact.lang.psi.TactSignature
import org.ton.tact.lang.psi.impl.TactSignatureImpl
import org.ton.tact.lang.stubs.TactSignatureStub

class TactSignatureStubElementType(name: String) : TactStubElementType<TactSignatureStub, TactSignature>(name) {
    override fun createPsi(stub: TactSignatureStub): TactSignature {
        return TactSignatureImpl(stub, this)
    }

    override fun createStub(psi: TactSignature, parentStub: StubElement<*>?): TactSignatureStub {
        return TactSignatureStub(parentStub, this, psi.text)
    }

    override fun serialize(stub: TactSignatureStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getText())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactSignatureStub {
        return TactSignatureStub(parentStub, this, dataStream.readName())
    }
}
