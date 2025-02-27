package org.ton.tact.lang.psi

import org.ton.tact.lang.doc.psi.TactDocComment

interface TactDocumentationOwner : TactCompositeElement {
    fun getDocumentation(): TactDocComment?
}
