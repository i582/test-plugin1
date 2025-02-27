package org.tonstudio.tact.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.impl.source.tree.FileElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.LightVirtualFile
import org.tonstudio.tact.lang.TactFileType
import org.tonstudio.tact.lang.TactLanguage
import org.tonstudio.tact.utils.childOfType

abstract class TactCodeFragment(
    fileViewProvider: FileViewProvider,
    contentElementType: IElementType,
    open val context: TactCompositeElement,
    forceCachedPsi: Boolean = true,
) : TactFile(fileViewProvider), PsiCodeFragment {

    constructor(
        project: Project,
        text: CharSequence,
        contentElementType: IElementType,
        context: TactCompositeElement,
    ) : this(
        PsiManagerEx.getInstanceEx(project).fileManager.createFileViewProvider(
            LightVirtualFile("fragment.sp", TactLanguage, text), true
        ),
        contentElementType,
        context,
    )

    private var viewProvider = super.getViewProvider() as SingleRootFileViewProvider
    private var forcedResolveScope: GlobalSearchScope? = null
    private var isPhysical = true

    init {
        if (forceCachedPsi) {
            @Suppress("LeakingThis")
            viewProvider.forceCachedPsi(this)
        }
        init(TokenType.CODE_FRAGMENT, contentElementType)
    }

    final override fun init(elementType: IElementType, contentElementType: IElementType?) {
        super.init(elementType, contentElementType)
    }

    override fun getContext(): PsiElement =
        context

    final override fun getViewProvider(): SingleRootFileViewProvider = viewProvider

    override fun forceResolveScope(scope: GlobalSearchScope?) {
        forcedResolveScope = scope
    }

    override fun getForcedResolveScope(): GlobalSearchScope? = forcedResolveScope

    override fun isValid() = true

    override fun isPhysical() = isPhysical

    override fun clone(): PsiFileImpl {
        val clone = cloneImpl(calcTreeElement().clone() as FileElement) as TactCodeFragment
        clone.isPhysical = false
        clone.myOriginalFile = this
        clone.viewProvider =
            SingleRootFileViewProvider(PsiManager.getInstance(project), LightVirtualFile(name, TactLanguage, text), false)
        clone.viewProvider.forceCachedPsi(clone)
        return clone
    }

    override fun accept(visitor: PsiElementVisitor) {
        visitor.visitFile(this)
    }

    override fun getFileType(): TactFileType = TactFileType
}

open class TactExpressionCodeFragment : TactCodeFragment {
    protected constructor(fileViewProvider: FileViewProvider, context: TactCompositeElement)
            : super(fileViewProvider, TactCodeFragmentElementType.EXPR, context)

    constructor(project: Project, text: CharSequence, context: TactCompositeElement)
            : super(project, text, TactCodeFragmentElementType.EXPR, context)

    val expr: TactExpression? get() = childOfType()
}

open class TactDebuggerExpressionCodeFragment(project: Project, text: CharSequence, context: TactCompositeElement) :
    TactExpressionCodeFragment(project, text, context)

open class TactTypeCodeFragment(project: Project, text: CharSequence, context: TactCompositeElement) :
    TactCodeFragment(project, text, TactCodeFragmentElementType.TYPE, context) {

    fun getType(): TactType {
        var errorElement: PsiErrorElement? = null
        accept(object : TactRecursiveVisitor() {
            override fun visitErrorElement(element: PsiErrorElement) {
                errorElement = element
            }
        })

        if (errorElement != null) {
            throw TactTypeSyntaxException(errorElement!!)
        }

        return childOfType()!!
    }

    class TactTypeSyntaxException(val error: PsiErrorElement) : Exception(error.errorDescription)
}
