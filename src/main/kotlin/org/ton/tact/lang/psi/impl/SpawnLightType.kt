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

    override fun getTypeReferenceExpression() = null

    override fun getIdentifier() = null

    override fun toString() = javaClass.simpleName + "{" + element + "}"

    override fun getElementType() = null

    override fun getStub(): TactTypeStub? = null

    override fun resolveType() = resolveType(this)

    class TactGenericType(private val name: String, val param: TactCompositeElement) : TactLightType<TactCompositeElement>(param) {
        override fun getName() = name

        override fun getText() = name

        override fun getModuleName() = ""
    }
}
