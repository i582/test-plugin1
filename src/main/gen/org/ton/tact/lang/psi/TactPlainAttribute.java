// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactPlainAttributeStub;

public interface TactPlainAttribute extends TactCompositeElement, StubBasedPsiElement<TactPlainAttributeStub> {

  @NotNull
  TactAttributeKey getAttributeKey();

  @NotNull
  List<TactExpression> getExpressionList();

  @Nullable
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

}
