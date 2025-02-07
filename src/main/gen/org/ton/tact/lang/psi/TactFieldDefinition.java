// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactFieldDefinitionStub;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;

public interface TactFieldDefinition extends TactNamedElement, StubBasedPsiElement<TactFieldDefinitionStub> {

  @NotNull
  PsiElement getIdentifier();

  @Nullable
  TactCompositeElement getQualifier();

  @Nullable
  String getQualifiedName();

  @NotNull
  TactTypeEx getTypeInner(@Nullable ResolveState context);

  @NotNull
  TactNamedElement getOwner();

}
