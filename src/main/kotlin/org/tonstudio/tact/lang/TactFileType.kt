package org.tonstudio.tact.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile

object TactFileType : LanguageFileType(TactLanguage) {
    override fun getName() = "Tact"
    override fun getDescription() = "Tact file"
    override fun getDefaultExtension() = "tact"
    override fun getIcon() = null // TODO
    override fun getCharset(file: VirtualFile, content: ByteArray): String = "UTF-8"
}
