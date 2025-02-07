// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.stubs.TactMessageDeclarationStub;
import org.ton.tact.lang.psi.*;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;
import com.intellij.psi.stubs.IStubElementType;

public class TactMessageDeclarationImpl extends TactNamedElementImpl<TactMessageDeclarationStub> implements TactMessageDeclaration {

  public TactMessageDeclarationImpl(@NotNull TactMessageDeclarationStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactMessageDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitMessageDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TactMessageType getMessageType() {
    return notNullChild(TactPsiTreeUtil.getStubChildOfType(this, TactMessageType.class));
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

  @Override
  @Nullable
  public PsiElement addField(@NotNull String name, @NotNull String type, boolean mutable) {
    return TactPsiImplUtil.addField(this, name, type, mutable);
  }

}
