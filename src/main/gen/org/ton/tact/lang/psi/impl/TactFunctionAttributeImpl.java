// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.psi.*;

public class TactFunctionAttributeImpl extends TactCompositeElementImpl implements TactFunctionAttribute {

  public TactFunctionAttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitFunctionAttribute(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactExpression getExpression() {
    return TactPsiTreeUtil.getChildOfType(this, TactExpression.class);
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

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(ABSTRACT);
  }

  @Override
  @Nullable
  public PsiElement getExtends() {
    return findChildByType(EXTENDS);
  }

  @Override
  @Nullable
  public PsiElement getInline() {
    return findChildByType(INLINE);
  }

  @Override
  @Nullable
  public PsiElement getMutates() {
    return findChildByType(MUTATES);
  }

  @Override
  @Nullable
  public PsiElement getOverride() {
    return findChildByType(OVERRIDE);
  }

  @Override
  @Nullable
  public PsiElement getVirtual() {
    return findChildByType(VIRTUAL);
  }

}
