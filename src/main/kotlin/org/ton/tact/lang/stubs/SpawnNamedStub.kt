package org.ton.tact.lang.stubs

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.NamedStubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.ton.tact.lang.psi.TactNamedElement

abstract class TactNamedStub<T : TactNamedElement> : NamedStubBase<T> {
    val isPublic: Boolean
    val extern: String?

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?,
        isPublic: Boolean,
        extern: String? = null,
    ) : super(parent, elementType, name) {
        this.isPublic = isPublic
        this.extern = extern
    }

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String?,
        isPublic: Boolean,
        extern: String? = null,
    ) : super(parent, elementType, name) {
        this.isPublic = isPublic
        this.extern = extern
    }

    override fun toString(): String {
        val name = name
        val str = super.toString()
        return if (StringUtil.isEmpty(name)) str else "$str: $name"
    }
}
