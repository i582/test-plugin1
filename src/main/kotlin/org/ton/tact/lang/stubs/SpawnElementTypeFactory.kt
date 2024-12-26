package org.ton.tact.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.ReflectionUtil
import org.ton.tact.lang.TactCodeBlockElementType
import org.ton.tact.lang.psi.TactType
import org.ton.tact.lang.psi.impl.*
import org.ton.tact.lang.stubs.types.*

object TactElementTypeFactory {
    private val TYPES: Map<String, Class<*>> = object : HashMap<String, Class<*>>() {
        init {
            put("TUPLE_TYPE", TactTupleTypeImpl::class.java)
            put("FUNCTION_TYPE", TactFunctionTypeImpl::class.java)
            put("MAP_TYPE", TactMapTypeImpl::class.java)
            put("STRUCT_TYPE", TactStructTypeImpl::class.java)
            put("TYPE", TactTypeImpl::class.java)
            put("OPTION_TYPE", TactOptionTypeImpl::class.java)
            put("CONTRACT_TYPE", TactContractTypeImpl::class.java)
            put("TRAIT_TYPE", TactTraitTypeImpl::class.java)
            put("MESSAGE_TYPE", TactMessageTypeImpl::class.java)
        }
    }

    @JvmStatic
    fun stubFactory(name: String) = when (name) {
        "FUNCTION_DECLARATION"         -> TactFunctionDeclarationStubElementType(name)
        "FIELD_DEFINITION"             -> TactFieldDefinitionStubElementType(name)
        "FIELD_DECLARATION"            -> TactFieldDeclarationStubElementType(name)
        "STRUCT_DECLARATION"           -> TactStructDeclarationStubElementType(name)
        "MESSAGE_DECLARATION"          -> TactMessageDeclarationStub.Type(name)
        "CONTRACT_DECLARATION"         -> TactContractDeclarationStub.Type(name)
        "TRAIT_DECLARATION"            -> TactTraitDeclarationStub.Type(name)
        "MESSAGE_FUNCTION_DECLARATION" -> TactMessageFunctionDeclarationStub.Type(name)
        "CONTRACT_INIT_DECLARATION"    -> TactContractInitDeclarationStub.Type(name)
        "IMPORT_DECLARATION"           -> TactImportDeclarationStubElementType(name)
        "PARAM_DEFINITION"             -> TactParamDefinitionStubElementType(name)
        "CONST_DEFINITION"             -> TactConstDefinitionStubElementType(name)
        "MODULE_VAR_DEFINITION"        -> TactModuleVarDefinitionStubElementType(name)
        "VAR_DEFINITION"               -> TactVarDefinitionStubElementType(name)
        "SIGNATURE"                    -> TactSignatureStubElementType(name)
        "PARAMETERS"                   -> TactParametersStubElementType(name)
        "RESULT"                       -> TactResultStubElementType(name)
        "TYPE_REFERENCE_EXPRESSION"    -> TactTypeReferenceExpressionStubElementType(name)
        "ATTRIBUTES"                   -> TactAttributesStubElementType(name)
        "ATTRIBUTE"                    -> TactAttributeStubElementType(name)
        "ATTRIBUTE_EXPRESSION"         -> TactAttributeExpressionStubElementType(name)
        "PLAIN_ATTRIBUTE"              -> TactPlainAttributeStubElementType(name)
        "ATTRIBUTE_KEY"                -> TactAttributeKeyStubElementType(name)
        "BLOCK"                        -> TactCodeBlockElementType()

        else                           -> {
            val c = TYPES[name] ?: throw RuntimeException("Unknown element type: $name")

            object : TactTypeStubElementType(name) {
                override fun createPsi(stub: TactTypeStub): TactType {
                    return try {
                        ReflectionUtil.createInstance(
                            c.getConstructor(stub::class.java, IStubElementType::class.java),
                            stub,
                            this
                        ) as TactType
                    } catch (e: NoSuchMethodException) {
                        throw RuntimeException(e)
                    }
                }
            }
        }
    }
}
