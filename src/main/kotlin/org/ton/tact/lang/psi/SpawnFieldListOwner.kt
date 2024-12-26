package org.ton.tact.lang.psi

interface TactFieldListOwner {
    /**
     * list of fields declared in this element and in embedded elements.
     */
    val fieldList: List<TactFieldDefinition>

    /**
     * list of fields declared in this element.
     */
    val ownFieldList: List<TactFieldDefinition>
}
