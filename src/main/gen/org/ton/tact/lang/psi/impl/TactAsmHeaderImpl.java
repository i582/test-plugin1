// This is a generated file. Not intended for manual editing.
package org.ton.tact.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.ton.tact.lang.psi.TactPsiTreeUtil;
import static org.ton.tact.lang.TactTypes.*;
import org.ton.tact.lang.psi.*;

public class TactAsmHeaderImpl extends TactCompositeElementImpl implements TactAsmHeader {

  public TactAsmHeaderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TactVisitor visitor) {
    visitor.visitAsmHeader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TactVisitor) accept((TactVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TactAsmShuffle getAsmShuffle() {
    return TactPsiTreeUtil.getChildOfType(this, TactAsmShuffle.class);
  }

}
