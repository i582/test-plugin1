package org.ton.tact.lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.*
import com.intellij.ui.IconManager
import com.intellij.util.PlatformIcons
import org.ton.tact.ide.ui.Icons
import org.ton.tact.lang.doc.psi.TactDocComment
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.TactPsiTreeUtil.parentStubOfType
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.psi.types.TactTypeEx
import org.ton.tact.lang.stubs.TactFileStub
import org.ton.tact.lang.stubs.TactNamedStub
import javax.swing.Icon

abstract class TactNamedElementImpl<T : TactNamedStub<*>> :
    TactStubbedElementImpl<T>,
    TactCompositeElement,
    TactNamedElement {

    constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub, nodeType)
    constructor(node: ASTNode) : super(node)

    override fun isBlank(): Boolean = name == "_"

    override fun isDeprecated(): Boolean = false // TODO

    override fun getName(): String? {
        val stub = stub
        if (stub != null) {
            return stub.name
        }
        val identifier = getIdentifier()
        return identifier?.text
    }

    override fun getQualifiedName(): String? {
        return getQualifiedNameBase()
    }

    fun getQualifiedNameBase(): String? {
        val stub = stub
        if (stub != null) {
            val name = stub.name ?: "anon"
            val fileStub = stub.parentStubOfType<TactFileStub>()
            val moduleName = fileStub?.getModuleQualifiedName()
            return TactPsiImplUtil.getFqn(moduleName, name)
        }

        val name = name ?: return null
        val moduleName = (containingFile as? TactFile)?.getModuleQualifiedName() ?: return null

        return TactPsiImplUtil.getFqn(moduleName, name)
    }

    override fun getOwner(): PsiElement? {
        return parentOfTypes(
            TactStructDeclaration::class,
            TactFile::class,
        )
    }

    override fun getModuleName(): String? {
        val stub = stub
        if (stub != null) {
            val fileStub = stub.parentStubOfType<TactFileStub>()
            return fileStub?.getModuleQualifiedName()
        }

        return (containingFile as? TactFile)?.getModuleQualifiedName() ?: return null
    }

    override fun setName(name: String): PsiElement? {
        val identifier = getIdentifier()
        val newIdentifier = TactElementFactory.createIdentifier(project, name)
        identifier?.replace(newIdentifier)
        return this
    }

    override fun getType(context: ResolveState?): TactTypeEx? {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result
                .create(
                    getTypeInner(null),
                    PsiModificationTracker.MODIFICATION_COUNT
                )
        }
    }

    protected open fun getTypeInner(context: ResolveState?) = findSiblingType()

    open fun findSiblingType(): TactTypeEx? {
        return findSiblingTypeNoEx().toEx()
    }

    open fun findSiblingTypeNoEx(): TactType? {
        val stub = stub
        return if (stub != null) {
            TactPsiTreeUtil.getStubChildOfType(parentByStub, TactType::class.java)
        } else {
            PsiTreeUtil.getChildOfType(this, TactType::class.java)
        }
    }

    override fun getPresentation(): ItemPresentation? {
        return object : TactItemPresentation<TactNamedElement>(this) {
            override fun getPresentableText() = runReadAction { element.name }
        }
    }

    override fun getIcon(flags: Int): Icon? {
        val icon = when (this) {
            is TactStructDeclaration   -> Icons.Struct
            is TactFunctionDeclaration -> Icons.Function
            is TactVarDefinition       -> Icons.Variable
            is TactConstDefinition     -> Icons.Constant
            is TactFieldDefinition     -> Icons.Field
            is TactParamDefinition     -> Icons.Parameter
            else                       -> null
        } ?: return super.getIcon(flags)

        if (flags and ICON_FLAG_VISIBILITY == 0) {
            return icon
        }

        val rowIcon = IconManager.getInstance().createLayeredIcon(this, icon, flags)
        if (rowIcon.iconCount <= 1) return rowIcon

        rowIcon.setIcon(PlatformIcons.PUBLIC_ICON, 1)

        return rowIcon
    }

    override fun getTextOffset(): Int {
        val identifier = getIdentifier()
        return identifier?.textOffset ?: super.getTextOffset()
    }

    override fun getNameIdentifier(): PsiElement? {
        return getIdentifier()
    }

    override fun getDocumentation(): TactDocComment? {
        return PsiTreeUtil.getPrevSiblingOfType(this, TactDocComment::class.java)
    }

    override fun getUseScope(): SearchScope {
        if (!isValid) return GlobalSearchScope.EMPTY_SCOPE
        val file = containingFile
        if (this is TactImportDeclaration) {
            return GlobalSearchScope.fileScope(file)
        }
        if (this is TactVarDefinition || this is TactConstDefinition) {
            val block = parentOfType<TactBlock>()
            if (block != null) return LocalSearchScope(block)
        }
        if (this is TactParamDefinition) {
            val f = parentOfType<TactSignatureOwner>()
            if (f != null) return LocalSearchScope(f.getBlockIfAny() ?: f)
        }
        val dir = file.containingDirectory ?: return super.getUseScope()
        val mod = TactModule.fromDirectory(dir)
        return mod.getScope()
    }
}
