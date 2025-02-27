package org.tonstudio.tact.lang.formatter

import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.tonstudio.tact.lang.TactLanguage

class TactLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage() = TactLanguage

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {}

    override fun getIndentOptionsEditor() = SmartIndentOptionsEditor()

    override fun customizeDefaults(commonSettings: CommonCodeStyleSettings, indentOptions: CommonCodeStyleSettings.IndentOptions) {
        indentOptions.INDENT_SIZE = 4
        indentOptions.CONTINUATION_INDENT_SIZE = 4
        indentOptions.TAB_SIZE = 4
        indentOptions.USE_TAB_CHARACTER = false
        commonSettings.BLOCK_COMMENT_AT_FIRST_COLUMN = false
        commonSettings.LINE_COMMENT_AT_FIRST_COLUMN = false
        commonSettings.BLANK_LINES_AROUND_CLASS
    }

    override fun getCodeSample(settingsType: SettingsType): String {
        return """
            // Tact code sample
            fun main() {
                dump("Hello, World!")
            }
        """.trimIndent()
    }
}
