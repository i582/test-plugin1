// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TactMapType extends TactType {

  @NotNull
  List<TactType> getTypeList();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getGreater();

  @Nullable
  PsiElement getLess();

  @NotNull
  PsiElement getMap();

  @Nullable
  TactType getKeyType();

  @Nullable
  TactType getValueType();

}
