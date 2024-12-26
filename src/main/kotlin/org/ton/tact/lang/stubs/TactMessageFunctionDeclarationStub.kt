package org.ton.tact.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.ton.tact.lang.psi.TactMessageFunctionDeclaration
import org.ton.tact.lang.stubs.types.TactNamedStubElementType
import org.ton.tact.lang.psi.impl.TactMessageFunctionDeclarationImpl

class TactMessageFunctionDeclarationStub : TactNamedStub<TactMessageFunctionDeclaration> {
    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?,
    ) : super(parent, elementType, name, true)

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String?,
    ) : super(parent, elementType, name, true)

    class Type(name: String) : TactNamedStubElementType<TactMessageFunctionDeclarationStub, TactMessageFunctionDeclaration>(name) {

        override fun createPsi(stub: TactMessageFunctionDeclarationStub): TactMessageFunctionDeclaration =
            TactMessageFunctionDeclarationImpl(stub, this)

        override fun createStub(psi: TactMessageFunctionDeclaration, parentStub: StubElement<*>?): TactMessageFunctionDeclarationStub =
            TactMessageFunctionDeclarationStub(parentStub, this, psi.name)

        override fun serialize(stub: TactMessageFunctionDeclarationStub, dataStream: StubOutputStream) {
            dataStream.writeName(stub.name)
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TactMessageFunctionDeclarationStub =
            TactMessageFunctionDeclarationStub(parentStub, this, dataStream.readName())
    }
}
