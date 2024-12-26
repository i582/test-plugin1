package org.ton.tact.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.ton.tact.lang.psi.TactImportDeclaration
import org.ton.tact.lang.psi.TactResult
import org.ton.tact.lang.psi.impl.TactImportDeclarationImpl
import org.ton.tact.lang.psi.impl.TactResultImpl
import org.ton.tact.lang.stubs.TactImportDeclarationStub
import org.ton.tact.lang.stubs.TactResultStub

class TactImportDeclarationStubElementType(name: String) : TactStubElementType<TactImportDeclarationStub, TactImportDeclaration>(name) {
    override fun createPsi(stub: TactImportDeclarationStub): TactImportDeclaration {
        return TactImportDeclarationImpl(stub, this)
    }

    override fun createStub(psi: TactImportDeclaration, parentStub: StubElement<*>?): TactImportDeclarationStub {
        return TactImportDeclarationStub(parentStub, this, psi.text)
    }

    override fun serialize(stub: TactImportDeclarationStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getText())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactImportDeclarationStub {
        return TactImportDeclarationStub(parentStub, this, dataStream.readName())
    }
}
