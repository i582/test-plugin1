// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.stubs.TactResultStub;
import org.ton.tact.lang.psi.*;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;
import com.intellij.psi.stubs.IStubElementType;

public class TactResultImpl extends TactStubbedElementImpl<TactResultStub> implements TactResult {

  public TactResultImpl(@NotNull TactResultStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactResultImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitResult(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TactType getType() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactType.class));
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(COLON);
  }

  @Override
  public boolean isVoid() {
    return TactPsiImplUtil.isVoid(this);
  }

  @Override
  @NotNull
  public TactTypeEx getType(@Nullable ResolveState context) {
    return TactPsiImplUtil.getType(this, context);
  }

}
