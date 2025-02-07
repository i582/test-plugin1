// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.stubs.TactPrimitiveDeclarationStub;
import org.ton.tact.lang.psi.*;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;
import com.intellij.psi.stubs.IStubElementType;

public class TactPrimitiveDeclarationImpl extends TactNamedElementImpl<TactPrimitiveDeclarationStub> implements TactPrimitiveDeclaration {

  public TactPrimitiveDeclarationImpl(@NotNull TactPrimitiveDeclarationStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactPrimitiveDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitPrimitiveDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TactPrimitiveType getPrimitiveType() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactPrimitiveType.class));
  }

  @Override
  @NotNull
  public PsiElement getSemicolon() {
    return notNullChild(findChildByType(SEMICOLON));
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return TactPsiImplUtil.getIdentifier(this);
  }

  @Override
  @NotNull
  public String getName() {
    return TactPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public TactTypeEx getTypeInner(@Nullable ResolveState context) {
    return TactPsiImplUtil.getTypeInner(this, context);
  }

}
