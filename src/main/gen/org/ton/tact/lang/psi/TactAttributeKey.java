// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactAttributeKeyStub;

public interface TactAttributeKey extends TactCompositeElement, StubBasedPsiElement<TactAttributeKeyStub> {

  @Nullable
  TactAttributeIdentifier getAttributeIdentifier();

  @Nullable
  TactExpression getExpression();

}
