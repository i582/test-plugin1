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

public class TactIfStatementImpl extends TactStatementImpl implements TactIfStatement {

  public TactIfStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitIfStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactElseBranch getElseBranch() {
    return TactPsiTreeUtil.getChildOfType(this, TactElseBranch.class);
  }

  @Override
  @NotNull
  public List<TactElseIfBranch> getElseIfBranchList() {
    return TactPsiTreeUtil.getChildrenOfTypeAsList(this, TactElseIfBranch.class);
  }

  @Override
  @Nullable
  public TactExpression getExpression() {
    return TactPsiTreeUtil.getChildOfType(this, TactExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getIf() {
    return notNullChild(findChildByType(IF));
  }

}
