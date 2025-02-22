// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactSignatureStub;
import kotlin.Pair;

public interface TactSignature extends TactCompositeElement, StubBasedPsiElement<TactSignatureStub> {

  @NotNull
  TactParameters getParameters();

  @Nullable
  TactResult getResult();

  @NotNull
  Pair<Integer, Integer> resultCount();

}
