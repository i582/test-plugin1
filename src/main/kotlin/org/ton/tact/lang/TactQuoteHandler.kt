package org.ton.tact.lang

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.openapi.editor.highlighter.HighlighterIterator

class TactQuoteHandler : SimpleTokenSetQuoteHandler(
    TactTypes.STRING_TEMPLATE,
    TactTypes.OPEN_QUOTE,
    TactTypes.CLOSING_QUOTE,
    TactTypes.CHAR,
    TactTypes.BACKTICK,
) {
    override fun isClosingQuote(iterator: HighlighterIterator, offset: Int): Boolean {
        if (iterator.tokenType == TactTypes.CHAR) {
            return super.isClosingQuote(iterator, offset)
        }
        return iterator.tokenType == TactTypes.CLOSING_QUOTE
    }

    override fun isOpeningQuote(iterator: HighlighterIterator, offset: Int): Boolean {
        if (iterator.tokenType == TactTypes.CHAR) {
            return super.isOpeningQuote(iterator, offset)
        }
        return iterator.tokenType == TactTypes.OPEN_QUOTE
    }

    override fun isNonClosedLiteral(iterator: HighlighterIterator?, chars: CharSequence?): Boolean {
        return super.isNonClosedLiteral(iterator, chars)
    }
}
