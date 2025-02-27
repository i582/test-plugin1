package org.tonstudio.tact.lang.completion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator

class TactLookupElement(
    delegate: LookupElement,
    val props: TactLookupElementProperties,
) : LookupElementDecorator<LookupElement>(delegate) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as TactLookupElement

        return props == other.props
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + props.hashCode()
        return result
    }
}

data class TactLookupElementProperties(
    val isLocal: Boolean = false,
    val isSameModule: Boolean = false,
    val isReceiverTypeCompatible: Boolean = false,
    val isTypeCompatible: Boolean = false,
    val isContextElement: Boolean = false,
    val isNotDeprecated: Boolean = true,
    val isContextMember: Boolean = false,
    val accessedNotViaOption: Boolean = true,
    val elementKind: ElementKind = ElementKind.OTHER,
) {
    enum class ElementKind {
        // Top Priority
        FIELD,
        METHOD,
        FUNCTION,
        STRUCT,
        CONSTANT,
        OTHER,
        // Least priority
    }
}
