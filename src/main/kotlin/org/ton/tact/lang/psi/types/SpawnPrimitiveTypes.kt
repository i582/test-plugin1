package org.ton.tact.lang.psi.types

enum class TactPrimitiveTypes(val value: String, val size: Int, val numeric: Boolean = false) {
    BOOL("Bool", 1),
    INT("Int", 4, true),
    BUILDER("Builder", 4, true),
    CELL("Cell", 4, true),
    ADDRESS("Address", 4, true),
    SLICE("Slice", 4, true),
    STRING("string", -1),
    NULL("null", 0),
    VOID("void", 0);

    companion object {
        fun find(name: String): TactPrimitiveTypes? = entries.find { it.value == name }

        fun isPrimitiveType(name: String): Boolean {
            return entries.any { it.value == name }
        }
    }
}
