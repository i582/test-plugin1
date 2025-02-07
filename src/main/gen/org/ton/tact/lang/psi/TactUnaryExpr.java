// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TactUnaryExpr extends TactExpression {

  @Nullable
  TactExpression getExpression();

  @Nullable
  PsiElement getBitNot();

  @Nullable
  PsiElement getBitXor();

  @Nullable
  PsiElement getMinus();

  @Nullable
  PsiElement getMul();

  @Nullable
  PsiElement getNot();

  @Nullable
  PsiElement getPlus();

  @Nullable
  PsiElement getSendChannel();

  //WARNING: getOperator(...) is skipped
  //matching getOperator(TactUnaryExpr, ...)
  //methods are not found in TactPsiImplUtil

}
