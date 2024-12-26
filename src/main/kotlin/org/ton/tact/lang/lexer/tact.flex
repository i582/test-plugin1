package org.ton.tact.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.Stack;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static org.ton.tact.lang.psi.TactTokenTypes.*;
import static org.ton.tact.lang.psi.TactDocElementTypes.*;

%%

%{
  private static final class State {
    final int lBraceCount;
    final int state;

    public State(int state, int lBraceCount) {
        this.state = state;
        this.lBraceCount = lBraceCount;
    }

    @Override
    public String toString() {
        return "yystate = " + state + (lBraceCount == 0 ? "" : "lBraceCount = " + lBraceCount);
    }
  }

  private final Stack<State> states = new Stack<State>();
  private int lBraceCount;

  private int commentStart;
  private int commentDepth;

  private void pushState(int state) {
    states.push(new State(yystate(), lBraceCount));
    lBraceCount = 0;
    yybegin(state);
  }

  private void popState() {
    State state = states.pop();
    lBraceCount = state.lBraceCount;
    yybegin(state.state);
  }

  private void popMaybeSemicolonState() {
     popState();
  }

  public _TactLexer() {
    this((java.io.Reader)null);
 }
%}

%class _TactLexer
%implements FlexLexer, TactTypes
%unicode
%public

%function advance
%type IElementType

%eof{
  return;
%eof}

NL = \n
WS = [ \t\f]

EOL_DOC_COMMENT = ({WS}*"//".*{NL})*({WS}*"//".*)
LINE_COMMENT = "//" [^\r\n]*
HASH_COMMENT = "#" [^\[] [^\r\n]*

MULTI_LINE_DEGENERATE_COMMENT = "/*" "*"+ "/"

LETTER = [:letter:] | "_"
DIGIT =  [:digit:]

HEX_DIGIT = [0-9A-Fa-f]
HEX_DIGIT_OR_SEP = {HEX_DIGIT} | "_"

INT_DIGIT = [0-9]
INT_DEGIT_OR_SEP = {INT_DIGIT} | "_"

OCT_DIGIT = [0-7]
OCT_DIGIT_OR_SEP = {OCT_DIGIT} | "_"

BIN_DIGIT = [0-1]
BIN_DIGIT_OR_SEP = {BIN_DIGIT} | "_"

NUM_INT = ({INT_DIGIT} {INT_DEGIT_OR_SEP}* {INT_DIGIT}) | {INT_DIGIT}
NUM_HEX = ("0x" | "0X") (({HEX_DIGIT} {HEX_DIGIT_OR_SEP}* {HEX_DIGIT}) | {HEX_DIGIT}) {HEX_EXPONENT}?
NUM_OCT = "0o" (({OCT_DIGIT} {OCT_DIGIT_OR_SEP}* {OCT_DIGIT}) | {OCT_DIGIT})
NUM_BIN = "0b" (({BIN_DIGIT} {BIN_DIGIT_OR_SEP}* {BIN_DIGIT}) | {BIN_DIGIT})

HEX_EXPONENT = [pP] [+-]? {NUM_INT}*

FLOAT_EXPONENT = [eE] [+-]? {NUM_INT}
NUM_FLOAT = (
    ({NUM_INT}? "." {NUM_INT}) {FLOAT_EXPONENT}?) |
    ({NUM_INT} {FLOAT_EXPONENT}
)

IDENT = {LETTER} {IDENT_PART}*
IDENT_PART = {LETTER} | {DIGIT}

// C special identifier like C.free
SPECIAL_IDENT = "C." {LETTER} ({LETTER} | {DIGIT} | "." )*

STR_DOUBLE = "\""
STR_SINGLE = "'"
STR_OPENING_ANGLE = "<"
STR_CLOSING_ANGLE = ">"

// raw string, like r"hello" or r'hello'
RAW_STR_MODIFIER = "r"

RAW_DOUBLE_QUOTE_STRING = {RAW_STR_MODIFIER} {STR_DOUBLE} [^\"]* {STR_DOUBLE}
RAW_SINGLE_QUOTE_STRING = {RAW_STR_MODIFIER} {STR_SINGLE} [^\']* {STR_SINGLE}

LONELY_DOLLAR=\$
LONG_TEMPLATE_ENTRY_START=\$\{
REGULAR_STRING_PART=[^\\\"\$]+
REGULAR_SINGLE_STRING_PART=[^\\\'\$]+

C_STRING_PART=[^\\\"]+
C_SINGLE_STRING_PART=[^\\\']+

%xstate STRING SINGLE_STRING C_STRING SINGLE_C_STRING MULTI_LINE_COMMENT_STATE
%state LONG_TEMPLATE_ENTRY

%state ASM_BLOCK
%state ASM_BLOCK_LINE

%%
// String templates


c\'                                           { pushState(SINGLE_C_STRING); return OPEN_QUOTE; }
<SINGLE_C_STRING> \'                          { popState(); return CLOSING_QUOTE; }
<SINGLE_C_STRING> "\\" (. | "\\")             { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_C_STRING> "\\"  {OCT_DIGIT} {3}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_C_STRING> "\\x" {HEX_DIGIT} {2}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_C_STRING> "\\u" {HEX_DIGIT} {4}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_C_STRING> "\\U" {HEX_DIGIT} {8}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }

c\"                                           { pushState(C_STRING); return OPEN_QUOTE; }
<C_STRING> \"                                 { popState(); return CLOSING_QUOTE; }
<C_STRING> "\\" (. | "\\")                    { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<C_STRING> "\\"  {OCT_DIGIT} {3}              { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<C_STRING> "\\x" {HEX_DIGIT} {2}              { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<C_STRING> "\\u" {HEX_DIGIT} {4}              { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }

<C_STRING> {C_STRING_PART}                  { return LITERAL_STRING_TEMPLATE_ENTRY; }
<SINGLE_C_STRING> {C_SINGLE_STRING_PART}    { return LITERAL_STRING_TEMPLATE_ENTRY; }


\'                                          { pushState(SINGLE_STRING); return OPEN_QUOTE; }
<SINGLE_STRING> \'                          { popState(); return CLOSING_QUOTE; }
<SINGLE_STRING> "\\" (. | "\\")             { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_STRING> "\\"  {OCT_DIGIT} {3}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_STRING> "\\x" {HEX_DIGIT} {2}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_STRING> "\\u" {HEX_DIGIT} {4}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_STRING> "\\U" {HEX_DIGIT} {8}       { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<SINGLE_STRING> {LONG_TEMPLATE_ENTRY_START} { pushState(LONG_TEMPLATE_ENTRY); return LONG_TEMPLATE_ENTRY_START; }

\"                                          { pushState(STRING); return OPEN_QUOTE; }
<STRING> \"                                 { popState(); return CLOSING_QUOTE; }
<STRING> "\\" (. | "\\")                    { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<STRING> "\\"  {OCT_DIGIT} {3}              { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<STRING> "\\x" {HEX_DIGIT} {2}              { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<STRING> "\\u" {HEX_DIGIT} {4}              { return LITERAL_STRING_TEMPLATE_ESCAPE_ENTRY; }
<STRING> {LONG_TEMPLATE_ENTRY_START}        { pushState(LONG_TEMPLATE_ENTRY); return LONG_TEMPLATE_ENTRY_START; }

<STRING> {REGULAR_STRING_PART}                  { return LITERAL_STRING_TEMPLATE_ENTRY; }
<SINGLE_STRING> {REGULAR_SINGLE_STRING_PART}    { return LITERAL_STRING_TEMPLATE_ENTRY; }

<STRING, SINGLE_STRING> {LONELY_DOLLAR}         { return LITERAL_STRING_TEMPLATE_ENTRY; }

<LONG_TEMPLATE_ENTRY> "{"                       { lBraceCount++; return LBRACE; }
<LONG_TEMPLATE_ENTRY> "}"                       {
                                                    if (lBraceCount == 0) {
                                                      popState();
                                                      return TEMPLATE_ENTRY_END;
                                                    }
                                                    lBraceCount--;
                                                    return RBRACE;
                                                }

// (Nested) comments

"/**/" {
    return MULTI_LINE_COMMENT;
}

"/*" {
    pushState(MULTI_LINE_COMMENT_STATE);
    commentDepth = 0;
    commentStart = getTokenStart();
}

<MULTI_LINE_COMMENT_STATE> {
    "/*" {
         commentDepth++;
    }

    <<EOF>> {
        int state = yystate();
        popState();
        zzStartRead = commentStart;
        return MULTI_LINE_COMMENT;
    }

    "*/" {
        if (commentDepth > 0) {
            commentDepth--;
        } else {
             int state = yystate();
             popState();
             zzStartRead = commentStart;
             return MULTI_LINE_COMMENT;
        }
    }

    [\s\S] {}
}

// Others

<YYINITIAL> "}" { return RBRACE; }

<YYINITIAL, LONG_TEMPLATE_ENTRY> {
{WS}                                      { return WS; }
{NL}+                                     { return NLS; }

{EOL_DOC_COMMENT}                         { return DOC_COMMENT; }
{LINE_COMMENT}                            { return LINE_COMMENT; }

// without this rule /*****/ is parsed as doc comment and /**/ is parsed as not closed doc comment, thanks Dart plugin
{MULTI_LINE_DEGENERATE_COMMENT}           { return MULTI_LINE_COMMENT; }

b?"`\\`"                                    { return BAD_CHARACTER; }
b?"``"                                      { return CHAR; }
b?"`" [^\\] "`"                             { return CHAR; }
b?"`" \n "`"?                               { return CHAR; }
b?"`\\" (. | "\\") "`"                      { return CHAR; }

// \141`, `\342\230\205`
b?"`" ("\\" {OCT_DIGIT} {3}) {1,3} "`"?     { return CHAR; }
b?"`" ("\\x" {HEX_DIGIT} {2}) {1,3} "`"?    { return CHAR; }
b?"`\\u" {HEX_DIGIT} {4} "`"?               { return CHAR; }
b?"`\\U" {HEX_DIGIT} {8} "`"?               { return CHAR; }

{RAW_DOUBLE_QUOTE_STRING}                 { return RAW_STRING; }
{RAW_SINGLE_QUOTE_STRING}                 { return RAW_STRING; }

"."                                       { return DOT; }
"!!"                                      { return ASSERT_OP; }
"~"                                       { return BIT_NOT; }
"|"                                       { return BIT_OR; }
"{"                                       { return LBRACE; }

"#["                                      { return HASH_LBRACK; }

"["                                       { return LBRACK; }
"]"                                       { return RBRACK; }

"("                                       { return LPAREN; }
")"                                       { return RPAREN; }

":"                                       { return COLON; }
";"                                       { return SEMICOLON; }
","                                       { return COMMA; }

"=="                                      { return EQ; }
"="                                       { return ASSIGN; }

"!="                                      { return NOT_EQ; }
"!"                                       { return NOT; }
"?"                                       { return QUESTION; }

"++"                                      { return PLUS_PLUS; }
"+="                                      { return PLUS_ASSIGN; }
"+"                                       { return PLUS; }

"--"                                      { return MINUS_MINUS; }
"-="                                      { return MINUS_ASSIGN; }
"-"                                       { return MINUS; }

"||"                                      { return COND_OR; }
"|="                                      { return BIT_OR_ASSIGN; }

"&^="                                     { return BIT_CLEAR_ASSIGN; }
"&^"                                      { return BIT_CLEAR; }
"&&"                                      { return COND_AND; }

"&="                                      { return BIT_AND_ASSIGN; }
"&"                                       { return BIT_AND; }

"<<="                                     { return SHIFT_LEFT_ASSIGN; }
"<<"                                      { return SHIFT_LEFT; }
"<-"                                      { return SEND_CHANNEL; }
"<="                                      { return LESS_OR_EQUAL; }
"<"                                       { return LESS; }

"^="                                      { return BIT_XOR_ASSIGN; }
"^"                                       { return BIT_XOR; }

"*="                                      { return MUL_ASSIGN; }
"*"                                       { return MUL; }

"/="                                      { return QUOTIENT_ASSIGN; }
"/"                                       { return QUOTIENT; }

"%="                                      { return REMAINDER_ASSIGN; }
"%"                                       { return REMAINDER; }
"@"                                       { return AT; }

">>="                                     { return SHIFT_RIGHT_ASSIGN; }
">>>="                                    { return UNSIGNED_SHIFT_RIGHT_ASSIGN; }
 ">>"                                     { return SHIFT_RIGHT; } // done in parser <<gtGt>>
 ">>>"                                    { return UNSIGNED_SHIFT_RIGHT; } // done in parser <<gtGtGt>>
">="                                      { return GREATER_OR_EQUAL; }
">"                                       { return GREATER; }

"->"                                      { return ARROW; }

// top level declarations
"as"                                      { return AS ; }
"import"                                  { return IMPORT ; }
"struct"                                  { return STRUCT; }
"const"                                   { return CONST; }
"fun"                                     { return FUN; }
"contract"                                { return CONTRACT; }
"trait"                                   { return TRAIT; }
"message"                                 { return MESSAGE; }
"with"                                    { return WITH; }
"receive"                                 { return RECEIVE; }
"external"                                { return EXTERNAL; }
"virtual"                                 { return VIRTUAL; }
"override"                                { return OVERRIDE; }
"abstract"                                { return ABSTRACT; }
"primitive"                               { return PRIMITIVE; }
"native"                                  { return NATIVE; }
"extends"                                 { return EXTENDS; }
"mutates"                                 { return MUTATES; }
"inline"                                  { return INLINE; }
"type"                                    { return TYPE; }

"return"                                  { return RETURN; }
"let"                                     { return LET; }
"while"                                   { return WHILE; }
"repeat"                                  { return REPEAT; }
"do"                                      { return DO; }
"until"                                   { return UNTIL; }
"foreach"                                 { return FOREACH; }
"try"                                     { return TRY; }
"catch"                                   { return CATCH; }
"if"                                      { return IF; }
"else"                                    { return ELSE; }

"initOf"                                  { return INIT_OF; }

// loop
"for"                                     { return FOR; }
"break"                                   { return BREAK; }
"continue"                                { return CONTINUE; }

"map"                                     { return MAP; }
"in"                                      { return IN; }

// literals
"null"                                    { return NULL; }
"true"                                    { return TRUE; }
"false"                                   { return FALSE; }

// modifiers
"var"                                     { return VAR; }

{IDENT}                                   { return IDENTIFIER; }
{SPECIAL_IDENT}                           { return IDENTIFIER; }

{NUM_FLOAT}                               { return FLOAT; }
{NUM_BIN}                                 { return BIN; }
{NUM_OCT}                                 { return OCT; }
{NUM_HEX}                                 { return HEX; }
{NUM_INT}                                 { return INT; }

.                                         { return BAD_CHARACTER; }
}

// error fallback
[\s\S]       { return BAD_CHARACTER; }
// error fallback for exclusive states
<STRING, SINGLE_STRING> .
             { return BAD_CHARACTER; }
