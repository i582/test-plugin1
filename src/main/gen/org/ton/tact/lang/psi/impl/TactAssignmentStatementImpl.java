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

public class TactAssignmentStatementImpl extends TactStatementImpl implements TactAssignmentStatement {

  public TactAssignmentStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitAssignmentStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TactAssignOp getAssignOp() {
    return notNullChild(TactPsiTreeUtil.getChildOfType(this, TactAssignOp.class));
  }

  @Override
  @NotNull
  public List<TactExpression> getExpressionList() {
    return TactPsiTreeUtil.getChildrenOfTypeAsList(this, TactExpression.class);
  }

  @Override
  @NotNull
  public TactListExpression getListExpression() {
    return notNullChild(TactPsiTreeUtil.getChildOfType(this, TactListExpression.class));
  }

  @Override
  @NotNull
  public List<TactExpression> getLeftExpressions() {
    return TactPsiImplUtil.getLeftExpressions(this);
  }

  @Override
  @NotNull
  public List<TactExpression> getRightExpressions() {
    return TactPsiImplUtil.getRightExpressions(this);
  }

}
