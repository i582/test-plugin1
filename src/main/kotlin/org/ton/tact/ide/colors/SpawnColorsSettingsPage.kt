package org.ton.tact.ide.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable
import org.ton.tact.ide.ui.Icons
import org.ton.tact.lang.TactSyntaxHighlighter

class TactColorsSettingsPage : ColorSettingsPage, DisplayPrioritySortable {
    object Util {
        val ANNOTATOR_TAGS: Map<String, TextAttributesKey> = TactColor.entries.associateBy({ it.name }, { it.textAttributesKey })
        val ATTRS: Array<AttributesDescriptor> = TactColor.entries.map { it.attributesDescriptor }.toTypedArray()
    }

    override fun getHighlighter() = TactSyntaxHighlighter()
    override fun getIcon() = Icons.Tact
    override fun getAttributeDescriptors() = Util.ATTRS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getAdditionalHighlightingTagToDescriptorMap() = Util.ANNOTATOR_TAGS
    override fun getDisplayName() = "Tact"

    override fun getDemoText() = """
// Example file
module <MODULE>main</MODULE>

import <MODULE>log</MODULE>
import <MODULE>os</MODULE>
import sys.<MODULE>libc</MODULE>
import hash.<MODULE>wyhash</MODULE>

<ATTRIBUTE>#[enable_if</ATTRIBUTE>(!<PUBLIC_CONSTANT>windows</PUBLIC_CONSTANT>)<ATTRIBUTE>]</ATTRIBUTE>

const <CONSTANT>BIRTHDAY</CONSTANT> = "08.07.2023"

pub const <PUBLIC_CONSTANT>AGE</PUBLIC_CONSTANT> = 1

var <MODULE_VARIABLE>logger</MODULE_VARIABLE> = <MODULE>log</MODULE>.Logger{}
pub var <PUBLIC_MODULE_VARIABLE>outer_logger</PUBLIC_MODULE_VARIABLE> = <MODULE>log</MODULE>.Logger{}

type <TYPE_ALIAS>Handle</TYPE_ALIAS> = *mut <KEYWORD>void</KEYWORD>

pub type <PUBLIC_TYPE_ALIAS>ReportCb</PUBLIC_TYPE_ALIAS> = fn (err <BUILTIN_TYPE>string</BUILTIN_TYPE>)

union <UNION>IdentValue</UNION> = <KEYWORD>i32</KEYWORD> | <BUILTIN_TYPE>string</BUILTIN_TYPE>

pub union <PUBLIC_UNION>Value</PUBLIC_UNION> = <KEYWORD>i32</KEYWORD> | <KEYWORD>f32</KEYWORD> | <KEYWORD>bool</KEYWORD> | <BUILTIN_TYPE>string</BUILTIN_TYPE>

pub enum <PUBLIC_ENUM>ErrorKind</PUBLIC_ENUM> {
    <ENUM_VARIANT>error</ENUM_VARIANT>
    <ENUM_VARIANT>warning</ENUM_VARIANT>
    <ENUM_VARIANT>note</ENUM_VARIANT>
}

enum <ENUM>Place</ENUM> {
    <ENUM_VARIANT>workspace</ENUM_VARIANT>
    <ENUM_VARIANT>stdlib</ENUM_VARIANT>
}

interface <INTERFACE>NamedNode</INTERFACE> {
    fn <INTERFACE_METHOD>name</INTERFACE_METHOD>(<KEYWORD>self</KEYWORD>) -> <BUILTIN_TYPE>string</BUILTIN_TYPE>
}

// <COMMENT_REFERENCE>Node</COMMENT_REFERENCE> is base interface for all nodes
//
// <DOC_HEADING># Representation</DOC_HEADING>
//
// Any <DOC_EMPHASIS>*type*</DOC_EMPHASIS> that implement <DOC_STRONG>**this**</DOC_STRONG> interface
//
// See also <DOC_LINK>[`NamedNode`]</DOC_LINK>.
//
// More info on <DOC_LINK>[official site](https://spawnlang.dev)</DOC_LINK>.
//
// Example:
// <DOC_CODE>```</DOC_CODE>
// <DOC_CODE>struct Identifier {}</DOC_CODE>
// <DOC_CODE>```</DOC_CODE>
pub interface <PUBLIC_INTERFACE>Node</PUBLIC_INTERFACE> {
    <PUBLIC_INTERFACE>Hashable</PUBLIC_INTERFACE>
    fn <INTERFACE_METHOD>node</INTERFACE_METHOD>(<KEYWORD>self</KEYWORD>)
    fn <INTERFACE_METHOD>id</INTERFACE_METHOD>(&<KEYWORD>self</KEYWORD>) -> <BUILTIN_TYPE>usize</BUILTIN_TYPE>
    fn <INTERFACE_METHOD>set_id</INTERFACE_METHOD>(&mut <KEYWORD>self</KEYWORD>, <PARAMETER>new</PARAMETER> <BUILTIN_TYPE>usize</BUILTIN_TYPE>)

    fn <INTERFACE_METHOD>create</INTERFACE_METHOD>() -> &mut <KEYWORD>self</KEYWORD>
}

<ATTRIBUTE>#[align</ATTRIBUTE>(1)<ATTRIBUTE>]</ATTRIBUTE>
pub struct <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT> {
    <FIELD>id</FIELD>    <BUILTIN_TYPE>usize</BUILTIN_TYPE>
    <FIELD>value</FIELD> <BUILTIN_TYPE>string</BUILTIN_TYPE>
}

fn (<RECEIVER>id</RECEIVER> &mut <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>) <FUNCTION>name</FUNCTION>() -> <BUILTIN_TYPE>string</BUILTIN_TYPE> {
    return <RECEIVER>id</RECEIVER>.<FIELD>value</FIELD>
}

pub fn <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>.<PUBLIC_FUNCTION>create</PUBLIC_FUNCTION>() -> &mut <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT> {
    return &mut <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>{ <FIELD>value</FIELD>: "new" }
}

fn (<RECEIVER>id</RECEIVER> <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>) <FUNCTION>node</FUNCTION>() {}

pub fn (<RECEIVER>id</RECEIVER> &<PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>) <PUBLIC_FUNCTION>id</PUBLIC_FUNCTION>() -> <BUILTIN_TYPE>usize</BUILTIN_TYPE> {
    return <RECEIVER>id</RECEIVER>.<FIELD>id</FIELD>
}

pub fn (<RECEIVER>id</RECEIVER> &mut <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>) <PUBLIC_FUNCTION>set_id</PUBLIC_FUNCTION>(<PARAMETER>new</PARAMETER> <BUILTIN_TYPE>usize</BUILTIN_TYPE>) {
    <RECEIVER>id</RECEIVER>.<FIELD>id</FIELD> = <PARAMETER>new</PARAMETER>
}

pub fn (<RECEIVER>id</RECEIVER> &<PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>) <PUBLIC_FUNCTION>hash</PUBLIC_FUNCTION>() -> <BUILTIN_TYPE>u64</BUILTIN_TYPE> {
    return <MODULE>main</MODULE>.<PUBLIC_FUNCTION>wyhash64</PUBLIC_FUNCTION>(<RECEIVER>id</RECEIVER>.<FIELD>id</FIELD>, 0)
}

struct <STRUCT>Nodes</STRUCT>[<TYPE_PARAMETER>T</TYPE_PARAMETER>: <PUBLIC_STRUCT>Node</PUBLIC_STRUCT>] {
    <ATTRIBUTE>#[json</ATTRIBUTE>("Nodes")<ATTRIBUTE>]</ATTRIBUTE>
    <FIELD>nodes</FIELD> []<TYPE_PARAMETER>T</TYPE_PARAMETER>
}

pub fn (<RECEIVER>n</RECEIVER> <STRUCT>Nodes</STRUCT>[<TYPE_PARAMETER>T</TYPE_PARAMETER>]) <PUBLIC_FUNCTION>as_map</PUBLIC_FUNCTION>() -> <KEYWORD>map</KEYWORD>[<BUILTIN_TYPE>string</BUILTIN_TYPE>]<TYPE_PARAMETER>T</TYPE_PARAMETER>
    where <TYPE_PARAMETER>T</TYPE_PARAMETER>: NamedNode
{
    mut <MUTABLE_VARIABLE>mp</MUTABLE_VARIABLE> := <KEYWORD>map</KEYWORD>[<BUILTIN_TYPE>string</BUILTIN_TYPE>]<TYPE_PARAMETER>T</TYPE_PARAMETER>{}
    for <VARIABLE>node</VARIABLE> in <RECEIVER>n</RECEIVER>.<FIELD>nodes</FIELD> {
        <MUTABLE_VARIABLE>mp</MUTABLE_VARIABLE>[<VARIABLE>node</VARIABLE>.<INTERFACE_METHOD>name</INTERFACE_METHOD>()] = <VARIABLE>node</VARIABLE>
    }
    return <MUTABLE_VARIABLE>mp</MUTABLE_VARIABLE>
}

<ATTRIBUTE>#[enable_if</ATTRIBUTE>(<PUBLIC_CONSTANT>debug</PUBLIC_CONSTANT>)<ATTRIBUTE>]</ATTRIBUTE>
pub fn <PUBLIC_FUNCTION>debug_log</PUBLIC_FUNCTION>(mut <MUTABLE_PARAMETER>msg</MUTABLE_PARAMETER> <BUILTIN_TYPE>string</BUILTIN_TYPE>) {
    <MUTABLE_PARAMETER>msg</MUTABLE_PARAMETER> = "DEBUG: ${'$'}{<MUTABLE_PARAMETER>msg</MUTABLE_PARAMETER>}"
    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>(<MUTABLE_PARAMETER>msg</MUTABLE_PARAMETER>)
}

<CFG_DISABLED_CODE>#[enable_if(!debug)]
pub fn debug_log(mut msg string) {}</CFG_DISABLED_CODE>

fn <FUNCTION>copy</FUNCTION>[<TYPE_PARAMETER>T</TYPE_PARAMETER>, const <CONST_TYPE_PARAMETER>Size</CONST_TYPE_PARAMETER> as <BUILTIN_TYPE>usize</BUILTIN_TYPE>](<PARAMETER>arr</PARAMETER> []<TYPE_PARAMETER>T</TYPE_PARAMETER>) -> [<CONST_TYPE_PARAMETER>Size</CONST_TYPE_PARAMETER>]<TYPE_PARAMETER>T</TYPE_PARAMETER> {
    mut <MUTABLE_VARIABLE>res</MUTABLE_VARIABLE> := [<CONST_TYPE_PARAMETER>Size</CONST_TYPE_PARAMETER>]<TYPE_PARAMETER>T</TYPE_PARAMETER>{}
    for <VARIABLE>i</VARIABLE> in 0 .. <CONST_TYPE_PARAMETER>Size</CONST_TYPE_PARAMETER> {
        <MUTABLE_VARIABLE>res</MUTABLE_VARIABLE>[i] = <PARAMETER>arr</PARAMETER>[<VARIABLE>i</VARIABLE>]
    }
    return <MUTABLE_VARIABLE>res</MUTABLE_VARIABLE>
}

<BLOCK_COMMENT>/**
 * We usually use line comments :)
 */</BLOCK_COMMENT>
fn <FUNCTION>main</FUNCTION>() {
    <VARIABLE>user</VARIABLE> := <MODULE>os</MODULE>.<PUBLIC_CONSTANT>ARGS</PUBLIC_CONSTANT>.<PUBLIC_FUNCTION>get</PUBLIC_FUNCTION>(1) or { "John" }
    <VARIABLE>hey</VARIABLE> := "Hey"

    mut <MUTABLE_VARIABLE>ident</MUTABLE_VARIABLE> := <PUBLIC_STRUCT>Identifier</PUBLIC_STRUCT>.<PUBLIC_FUNCTION>create</PUBLIC_FUNCTION>()
    <MUTABLE_VARIABLE>ident</MUTABLE_VARIABLE>.<PUBLIC_FUNCTION>set_id</PUBLIC_FUNCTION>(100)

    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>(<MUTABLE_VARIABLE>ident</MUTABLE_VARIABLE>.<INTERFACE_METHOD>id</INTERFACE_METHOD>())
    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>(<MUTABLE_VARIABLE>ident</MUTABLE_VARIABLE>.<INTERFACE_METHOD>name</INTERFACE_METHOD>())

    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>("we have usual strings\nuse string interpolation when needed:  ${'$'}{<VARIABLE>hey</VARIABLE>}  ${'$'}{<VARIABLE>hey</VARIABLE>}")
    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>(r"and the raw one, use any escape sequence without fear \n\n")
    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>(c"or even C one with *u8 type :D")

    unsafe {
        <MODULE>libc</MODULE>.<UNSAFE_CODE><PUBLIC_FUNCTION>printf</PUBLIC_FUNCTION></UNSAFE_CODE>(<STRING>c"Hello, C function!\n"</STRING>)
    }

    <PUBLIC_FUNCTION>println</PUBLIC_FUNCTION>("we are in  ${'$'}{<PUBLIC_CONSTANT>${'$'}FILE</PUBLIC_CONSTANT>}")
    <MODULE>os</MODULE>.<PUBLIC_FUNCTION>exit</PUBLIC_FUNCTION>(1)
}

	""".trimIndent()

    override fun getPriority() = DisplayPriority.KEY_LANGUAGE_SETTINGS
}
