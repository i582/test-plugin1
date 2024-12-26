package org.ton.tact.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.ton.tact.lang.psi.TactFunctionOrMethodDeclaration

abstract class TactFunctionOrMethodDeclarationStub<T : TactFunctionOrMethodDeclaration> : TactNamedStub<T> {
    protected constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?,
        isPublic: Boolean,
        extern: String? = null,
    ) : super(parent, elementType, name, isPublic, extern)

    protected constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String?,
        isPublic: Boolean,
        extern: String? = null,
    ) : super(parent, elementType, name, isPublic, extern)
}
