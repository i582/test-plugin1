package org.ton.tact.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.ton.tact.lang.psi.TactFunctionDeclaration

class TactFunctionDeclarationStub : TactFunctionOrMethodDeclarationStub<TactFunctionDeclaration> {
    var type: String? = null
    var isUnsafe: Boolean = false

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?,
        isPublic: Boolean,
        isUnsafe: Boolean,
        extern: String?,
        type: String?,
    ) : super(parent, elementType, name, isPublic, extern) {
        this.isUnsafe = isUnsafe
        this.type = type
    }

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String,
        isPublic: Boolean,
        isUnsafe: Boolean,
        extern: String?,
        type: String?,
    ) : super(parent, elementType, name, isPublic, extern) {
        this.isUnsafe = isUnsafe
        this.type = type
    }
}
