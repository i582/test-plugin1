// This is a generated file. Not intended for manual editing.
package org.tonstudio.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.tonstudio.tact.lang.stubs.TactTypeReferenceExpressionStub;
import kotlin.Pair;

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

  @NotNull
  Pair<Integer, Integer> getIdentifierBounds();

}
