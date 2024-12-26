package org.ton.tact.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.ton.tact.lang.psi.TactStructDeclaration

class TactStructDeclarationStub : TactNamedStub<TactStructDeclaration> {
    var isUnion: Boolean = false

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?,
        isPublic: Boolean,
        isUnion: Boolean,
        extern: String?,
    ) : super(parent, elementType, name, isPublic, extern) {
        this.isUnion = isUnion
    }

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String?,
        isPublic: Boolean,
        isUnion: Boolean,
        extern: String?,
    ) :
            super(parent, elementType, name, isPublic, extern) {
        this.isUnion = isUnion
    }
}
