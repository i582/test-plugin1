package org.ton.tact.lang.psi.impl

import com.intellij.psi.scope.PsiScopeProcessor

abstract class TactScopeProcessor : PsiScopeProcessor {
    open fun isCompletion(): Boolean = false
    open fun isCodeFragment(): Boolean = false
}
