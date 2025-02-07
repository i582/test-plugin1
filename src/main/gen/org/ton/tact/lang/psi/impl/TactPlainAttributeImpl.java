// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.stubs.TactPlainAttributeStub;
import org.ton.tact.lang.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class TactPlainAttributeImpl extends TactStubbedElementImpl<TactPlainAttributeStub> implements TactPlainAttribute {

  public TactPlainAttributeImpl(@NotNull TactPlainAttributeStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactPlainAttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitPlainAttribute(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TactAttributeKey getAttributeKey() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactAttributeKey.class));
  }

  @Override
  @NotNull
  public List<TactExpression> getExpressionList() {
    return TactPsiTreeUtil.getChildrenOfTypeAsList(this, TactExpression.class);
  }

  @Override
  @Nullable
  public PsiElement getLparen() {
    return findChildByType(LPAREN);
  }

  @Override
  @Nullable
  public PsiElement getRparen() {
    return findChildByType(RPAREN);
  }

}
