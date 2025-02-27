package org.ton.tact.lang.stubs

import com.intellij.psi.stubs.PsiFileStubImpl
import org.ton.tact.lang.TactFileElementType
import org.ton.tact.lang.psi.TactFile

class TactFileStub(file: TactFile?) : PsiFileStubImpl<TactFile?>(file) {
    override fun getType() = TactFileElementType.INSTANCE

    fun getModuleName(): String {
        return ""
    }

    fun getModuleQualifiedName(): String {
        return ""
    }
}
