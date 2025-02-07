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

public class TactFunctionTypeImpl extends TactTypeImpl implements TactFunctionType {

  public TactFunctionTypeImpl(@NotNull TactTypeStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactFunctionTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitFunctionType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactSignature getSignature() {
    return TactPsiTreeUtil.getStubChildOfType(this, TactSignature.class);
  }

  @Override
  @NotNull
  public PsiElement getFun() {
    return notNullChild(findChildByType(FUN));
  }

}
