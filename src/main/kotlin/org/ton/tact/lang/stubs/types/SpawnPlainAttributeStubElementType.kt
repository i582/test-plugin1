package org.ton.tact.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.ton.tact.lang.psi.TactPlainAttribute
import org.ton.tact.lang.psi.impl.TactPlainAttributeImpl
import org.ton.tact.lang.stubs.TactPlainAttributeStub

class TactPlainAttributeStubElementType(name: String) : TactStubElementType<TactPlainAttributeStub, TactPlainAttribute>(name) {
    override fun createPsi(stub: TactPlainAttributeStub): TactPlainAttribute {
        return TactPlainAttributeImpl(stub, this)
    }

    override fun createStub(psi: TactPlainAttribute, parentStub: StubElement<*>?): TactPlainAttributeStub {
        return TactPlainAttributeStub(parentStub, this, psi.text)
    }

    override fun serialize(stub: TactPlainAttributeStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getText())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactPlainAttributeStub {
        return TactPlainAttributeStub(parentStub, this, dataStream.readName())
    }
}
