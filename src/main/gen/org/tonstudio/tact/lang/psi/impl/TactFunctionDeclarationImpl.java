// This is a generated file. Not intended for manual editing.
package org.tonstudio.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.tonstudio.tact.lang.psi.TactPsiTreeUtil;
import static org.tonstudio.tact.lang.TactTypes.*;
import org.tonstudio.tact.lang.psi.*;
import com.intellij.psi.ResolveState;
import org.tonstudio.tact.lang.psi.types.TactTypeEx;
import org.tonstudio.tact.lang.stubs.TactFunctionDeclarationStub;
import com.intellij.psi.stubs.IStubElementType;

public class TactFunctionDeclarationImpl extends TactFunctionDeclarationWithScopeHolder implements TactFunctionDeclaration {

  public TactFunctionDeclarationImpl(@NotNull TactFunctionDeclarationStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactFunctionDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitFunctionDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactAttributes getAttributes() {
    return TactPsiTreeUtil.getStubChildOfType(this, TactAttributes.class);
  }

  @Override
  @Nullable
  public TactBlock getBlock() {
    return TactPsiTreeUtil.getChildOfType(this, TactBlock.class);
  }

  @Override
  @NotNull
  public List<TactFunctionAttribute> getFunctionAttributeList() {
    return TactPsiTreeUtil.getChildrenOfTypeAsList(this, TactFunctionAttribute.class);
  }

  @Override
  @NotNull
  public TactSignature getSignature() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactSignature.class));
  }

  @Override
  @NotNull
  public PsiElement getFun() {
    return notNullChild(findChildByType(FUN));
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return notNullChild(findChildByType(IDENTIFIER));
  }

  @Override
  @NotNull
  public String getName() {
    return TactPsiImplUtil.getName(this);
  }

  @Override
  @Nullable
  public TactTypeEx getTypeInner(@Nullable ResolveState context) {
    return TactPsiImplUtil.getTypeInner(this, context);
  }

}
