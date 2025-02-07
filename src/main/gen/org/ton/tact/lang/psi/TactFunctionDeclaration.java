// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.ton.tact.lang.stubs.TactFunctionDeclarationStub;
import com.intellij.psi.ResolveState;
import org.ton.tact.lang.psi.types.TactTypeEx;

public interface TactFunctionDeclaration extends TactSignatureOwner, TactFunctionOrMethodDeclaration, TactAttributeOwner, TactScopeHolder, StubBasedPsiElement<TactFunctionDeclarationStub> {

  @Nullable
  TactAttributes getAttributes();

  @Nullable
  TactBlock getBlock();

  @NotNull
  List<TactFunctionAttribute> getFunctionAttributeList();

  @NotNull
  TactSignature getSignature();

  @NotNull
  PsiElement getFun();

  @NotNull
  PsiElement getIdentifier();

  @NotNull
  String getName();

  @Nullable
  TactTypeEx getTypeInner(@Nullable ResolveState context);

  boolean isDefinition();

}
