// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.stubs.TactFieldDeclarationStub;
import org.ton.tact.lang.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class TactFieldDeclarationImpl extends TactStubbedElementImpl<TactFieldDeclarationStub> implements TactFieldDeclaration {

  public TactFieldDeclarationImpl(@NotNull TactFieldDeclarationStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactFieldDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitFieldDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactDefaultFieldValue getDefaultFieldValue() {
    return TactPsiTreeUtil.getChildOfType(this, TactDefaultFieldValue.class);
  }

  @Override
  @NotNull
  public TactFieldDefinition getFieldDefinition() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactFieldDefinition.class));
  }

  @Override
  @NotNull
  public TactType getType() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactType.class));
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(COLON));
  }

  @Override
  @NotNull
  public PsiElement getSemicolon() {
    return notNullChild(findChildByType(SEMICOLON));
  }

}
