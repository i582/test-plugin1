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

	""".trimIndent()

    override fun getPriority() = DisplayPriority.KEY_LANGUAGE_SETTINGS
}
