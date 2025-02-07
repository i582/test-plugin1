// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactContractInitDeclarationStub;

public interface TactContractInitDeclaration extends TactNamedElement, StubBasedPsiElement<TactContractInitDeclarationStub> {

  @Nullable
  TactBlock getBlock();

  @Nullable
  TactParameters getParameters();

  @Nullable
  PsiElement getIdentifier();

  @NotNull
  String getName();

}
