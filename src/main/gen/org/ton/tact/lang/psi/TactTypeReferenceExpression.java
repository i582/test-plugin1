// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactTypeReferenceExpressionStub;

public interface TactTypeReferenceExpression extends TactReferenceExpressionBase, StubBasedPsiElement<TactTypeReferenceExpressionStub> {

  @NotNull
  PsiElement getIdentifier();

  @Nullable
  TactCompositeElement getQualifier();

  @Nullable
  PsiElement resolve();

  //WARNING: getType(...) is skipped
  //matching getType(TactTypeReferenceExpression, ...)
  //methods are not found in TactPsiImplUtil

}
