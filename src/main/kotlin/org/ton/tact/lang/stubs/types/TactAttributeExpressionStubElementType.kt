package org.ton.tact.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.ton.tact.lang.psi.TactAttributeExpression
import org.ton.tact.lang.psi.impl.TactAttributeExpressionImpl
import org.ton.tact.lang.stubs.TactAttributeExpressionStub

class TactAttributeExpressionStubElementType(name: String) : TactStubElementType<TactAttributeExpressionStub, TactAttributeExpression>(name) {
    override fun createPsi(stub: TactAttributeExpressionStub): TactAttributeExpression {
        return TactAttributeExpressionImpl(stub, this)
    }

    override fun createStub(psi: TactAttributeExpression, parentStub: StubElement<*>?): TactAttributeExpressionStub {
        return TactAttributeExpressionStub(parentStub, this, psi.text)
    }

    override fun serialize(stub: TactAttributeExpressionStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getText())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactAttributeExpressionStub {
        return TactAttributeExpressionStub(parentStub, this, dataStream.readName())
    }
}
