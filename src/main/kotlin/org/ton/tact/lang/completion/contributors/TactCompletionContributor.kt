package org.ton.tact.lang.completion.contributors

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import org.ton.tact.lang.completion.TactCompletionPatterns.cachedReferenceExpression
import org.ton.tact.lang.completion.TactCompletionPatterns.referenceExpression
import org.ton.tact.lang.completion.providers.*
import org.ton.tact.lang.completion.sort.withTactSorter

class TactCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, referenceExpression(),       ReferenceCompletionProvider)
        extend(CompletionType.BASIC, cachedReferenceExpression(), ReferenceCompletionProvider)
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, withTactSorter(parameters, result))
    }
}
