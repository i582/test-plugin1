// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactTypeStub;

public interface TactType extends TactCompositeElement, StubBasedPsiElement<TactTypeStub> {

  @Nullable
  TactType getType();

  @Nullable
  TactTypeExtra getTypeExtra();

  @Nullable
  TactTypeReferenceExpression getTypeReferenceExpression();

  @Nullable
  PsiElement getIdentifier();

  @NotNull
  TactType resolveType();

  @NotNull
  String getModuleName();

}
