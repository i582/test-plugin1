// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactImportDeclarationStub;

public interface TactImportDeclaration extends TactCompositeElement, StubBasedPsiElement<TactImportDeclarationStub> {

  @Nullable
  TactStringLiteral getStringLiteral();

  @Nullable
  PsiElement getSemicolon();

  @NotNull
  PsiElement getImport();

  @NotNull
  String getPath();

}
