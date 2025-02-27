// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TactCallExpr extends TactExpression {

  @NotNull
  TactArgumentList getArgumentList();

  @Nullable
  TactAssertNotNullExpression getAssertNotNullExpression();

  @Nullable
  TactExpression getExpression();

  @NotNull
  List<TactExpression> getArguments();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  TactReferenceExpression getQualifier();

  @Nullable
  PsiElement resolve();

  int paramIndexOf(@NotNull PsiElement pos);

}
