package org.ton.tact.lang.psi.types

enum class TactPrimitiveTypes(val value: String, val size: Int, val numeric: Boolean = false) {
    BOOL("Bool", 1),
    INT("Int", 4, true),
    UINT("Uint", 4, true),
    STRING("string", -1),
    NULL("null", 0),
    VOID("void", 0),
    NEVER("never", 0);

    companion object {
        fun isPrimitiveType(name: String): Boolean {
            return entries.any { it.value == name }
        }

        fun isNumeric(name: String): Boolean {
            return entries.any { it.value == name && it.numeric }
        }
    }
}
