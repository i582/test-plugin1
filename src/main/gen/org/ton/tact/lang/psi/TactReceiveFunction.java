// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TactReceiveFunction extends TactCompositeElement {

  @Nullable
  TactBlock getBlock();

  @Nullable
  TactParameters getParameters();

  @Nullable
  TactReceiveStringId getReceiveStringId();

  @NotNull
  PsiElement getReceive();

}
