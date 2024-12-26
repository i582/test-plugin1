package org.ton.tact.lang

import com.intellij.lexer.LayeredLexer
import org.ton.tact.lang.lexer.TactLexer

class TactHighlightingLexer : LayeredLexer(TactLexer())
