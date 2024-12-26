package org.ton.tact.lang.psi.impl

import com.intellij.psi.PsiFile
import com.intellij.psi.impl.light.LightElement
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.impl.TactPsiImplUtil.resolveType
import org.ton.tact.lang.stubs.TactTypeStub

abstract class TactLightType<E : TactCompositeElement>(
    protected val element: E,
) : LightElement(element.manager, element.language), TactType {

    init {
        navigationElement = element
    }

    override fun getContainingFile(): PsiFile = element.containingFile

    override fun getIdentifier() = null

    override fun toString() = javaClass.simpleName + "{" + element + "}"

    override fun getElementType() = null

    override fun getStub(): TactTypeStub? = null

    override fun resolveType() = resolveType(this)
}
