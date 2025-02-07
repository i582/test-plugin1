// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactTraitDeclarationStub;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;

public interface TactTraitDeclaration extends TactNamedElement, TactTopLevelTypeDeclaration, StubBasedPsiElement<TactTraitDeclarationStub> {

  @Nullable
  TactAttributes getAttributes();

  @NotNull
  TactTraitType getTraitType();

  @Nullable
  PsiElement getIdentifier();

  @NotNull
  String getName();

  @NotNull
  TactTypeEx getTypeInner(@Nullable ResolveState context);

}
