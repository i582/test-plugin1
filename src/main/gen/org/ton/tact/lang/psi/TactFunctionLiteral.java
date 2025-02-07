// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TactFunctionLiteral extends TactExpression, TactSignatureOwner {

  @Nullable
  TactBlock getBlock();

  @Nullable
  TactSignature getSignature();

  @NotNull
  PsiElement getFun();

  //WARNING: processDeclarations(...) is skipped
  //matching processDeclarations(TactFunctionLiteral, ...)
  //methods are not found in TactPsiImplUtil

}
