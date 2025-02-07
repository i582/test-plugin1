// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;

public interface TactListExpression extends TactExpression {

  @NotNull
  List<TactExpression> getExpressionList();

  @Nullable
  TactTypeEx getType(@Nullable ResolveState context);

}
