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
import com.intellij.psi.stubs.IStubElementType;
import org.ton.tact.lang.stubs.TactTypeStub;

public class TactMessageTypeImpl extends TactTypeImpl implements TactMessageType {

  public TactMessageTypeImpl(@NotNull TactTypeStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactMessageTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitMessageType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<TactFieldDeclaration> getFieldDeclarationList() {
    return TactPsiTreeUtil.getStubChildrenOfTypeAsList(this, TactFieldDeclaration.class);
  }

  @Override
  @Nullable
  public TactMessageId getMessageId() {
    return TactPsiTreeUtil.getChildOfType(this, TactMessageId.class);
  }

  @Override
  @Nullable
  public PsiElement getLbrace() {
    return findChildByType(LBRACE);
  }

  @Override
  @Nullable
  public PsiElement getRbrace() {
    return findChildByType(RBRACE);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public PsiElement getMessage() {
    return notNullChild(findChildByType(MESSAGE));
  }

  @Override
  @NotNull
  public List<TactFieldDefinition> getFieldList() {
    return TactPsiImplUtil.getFieldList(this);
  }

}
