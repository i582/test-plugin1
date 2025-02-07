// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.stubs.TactImportDeclarationStub;
import org.ton.tact.lang.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class TactImportDeclarationImpl extends TactStubbedElementImpl<TactImportDeclarationStub> implements TactImportDeclaration {

  public TactImportDeclarationImpl(@NotNull TactImportDeclarationStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public TactImportDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitImportDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactStringLiteral getStringLiteral() {
    return TactPsiTreeUtil.getChildOfType(this, TactStringLiteral.class);
  }

  @Override
  @Nullable
  public PsiElement getSemicolon() {
    return findChildByType(SEMICOLON);
  }

  @Override
  @NotNull
  public PsiElement getImport() {
    return notNullChild(findChildByType(IMPORT));
  }

  @Override
  @NotNull
  public String getPath() {
    return TactPsiImplUtil.getPath(this);
  }

}
