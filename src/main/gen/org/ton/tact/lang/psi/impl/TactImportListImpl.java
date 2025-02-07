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

public class TactImportListImpl extends TactCompositeElementImpl implements TactImportList {

  public TactImportListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitImportList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<TactImportDeclaration> getImportDeclarationList() {
    return TactPsiTreeUtil.getChildrenOfTypeAsList(this, TactImportDeclaration.class);
  }

}
