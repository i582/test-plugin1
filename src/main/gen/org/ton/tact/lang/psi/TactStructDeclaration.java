// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactStructDeclarationStub;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;

public interface TactStructDeclaration extends TactNamedElement, TactAttributeOwner, TactTopLevelTypeDeclaration, StubBasedPsiElement<TactStructDeclarationStub> {

  @Nullable
  TactAttributes getAttributes();

  @NotNull
  TactStructType getStructType();

  @Nullable
  PsiElement getIdentifier();

  @NotNull
  String getName();

  @NotNull
  TactTypeEx getTypeInner(@Nullable ResolveState context);

  @Nullable
  PsiElement addField(@NotNull String name, @NotNull String type, boolean mutable);

}
