package org.ton.tact.lang.psi.impl

import ai.grazie.utils.chainIf
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import org.ton.tact.ide.codeInsight.TactCodeInsightUtil
import org.ton.tact.lang.psi.*
import org.ton.tact.lang.psi.types.*
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.toEx
import org.ton.tact.lang.psi.types.TactBaseTypeEx.Companion.unwrapAlias
import org.ton.tact.utils.parentNth

fun TactExpression.inferType(context: ResolveState?): TactTypeEx? {
    return TactTypeInferer.getType(this, context)
}

fun PsiElement.contextType(): TactTypeEx? {
    return TactTypeInferer.getContextType(this)
}

object TactTypeInferer {
    fun getType(expr: TactExpression, context: ResolveState?): TactTypeEx? {
        if (isBoolExpr(expr)) {
            return TactPrimitiveTypeEx.BOOL
        }

        if (expr is TactUnaryExpr) {
            if (expr.expression == null) return null
            val exprType = expr.expression!!.getType(context) ?: return null
            when {
                expr.not != null -> return TactPrimitiveTypeEx.BOOL
                expr.mul != null -> return exprType
            }
            return exprType
        }

        // (type) -> type
        if (expr is TactParenthesesExpr) {
            return expr.expression?.getType(context)
        }

        // "" -> string
        if (expr is TactStringLiteral) {
            return TactStringTypeEx.getInstance(expr.project)
        }

        if (expr is TactLiteral) {
            // 1 -> int
            if (expr.int != null || expr.hex != null || expr.oct != null || expr.bin != null) return TactPrimitiveTypeEx.INT
            // true -> bool
            if (expr.`true` != null || expr.`false` != null) return TactPrimitiveTypeEx.BOOL
            // nil -> null
            if (expr.`null` != null) return TactPrimitiveTypeEx.NULL
        }

        // type1 + type2 -> type1
        if (expr is TactAddExpr) {
            return expr.left.getType(context)
        }

        if (expr is TactMulExpr) {
            val left = expr.left
            if (left !is TactLiteral) return left.getType(context)
            val right = expr.right
            if (right != null) return right.getType(context)
        }

        if (expr is TactReferenceExpression) {
            val resolved = expr.reference.resolve()
            if (resolved is TactTypeOwner) {
                val type = typeOrParameterType(resolved, context)

                val smartcastType = inferTypeWithSmartcasts(resolved, type, expr)
                if (smartcastType != null) {
                    return smartcastType
                }

                return type
            }
        }

        // type{...} -> type
        if (expr is TactLiteralValueExpression) {
            return expr.type.toEx()
        }

        if (expr is TactCallExpr) {
            val calledExpr = expr.expression
            val exprType = calledExpr?.getType(context) ?: return null
            val unwrapped = exprType.unwrapAlias()
            if (unwrapped !is TactFunctionTypeEx) {
                return exprType
            }

            val signature = unwrapped.signature
            val owner = signature.parent as TactSignatureOwner

            return processSignatureReturnType(signature, expr, owner)
        }

        if (expr is TactDotExpression) {
            if (expr.assertNotNullExpression != null) {
                val type = expr.expression?.getType(context)
                return unwrapOptionOrResultType(type)
            }
        }

        // fn () -> type(fn())
        if (expr is TactFunctionLiteral) {
            return TactFunctionTypeEx.from(expr)
        }

        if (expr is TactTupleLiteral) {
            val expressions = mutableListOf(expr.expression)
            val expressionList = expr.listExpression?.expressionList ?: return null
            expressions.addAll(expressionList)
            val types = expressions.map { it.getType(context) ?: TactAnyTypeEx.INSTANCE }
            return TactTupleTypeEx(types, expr)
        }

        if (expr is TactIfStatement) {
            val ifBody = expr.block
            val elseBody = expr.elseBranch?.block

            val ifType = ifBody.getType(context)
            val elseType = elseBody.getType(context)

            if (ifType == null) return elseType
            if (elseType == null) return ifType
            if (ifType.toString() == elseType.toString()) return ifType

            // TODO: union type of if and else types
            return ifType
        }

        if (expr is TactListExpression) {
            val types = expr.expressionList.map { it.getType(context) ?: TactAnyTypeEx.INSTANCE }
            if (types.size == 1) {
                return types.first()
            }
            return TactTupleTypeEx(types, expr)
        }

        return null
    }

    private fun processSignatureReturnType(
        signature: TactSignature,
        expr: TactCallExpr,
        owner: TactSignatureOwner,
    ): TactTypeEx {
        val result = signature.result ?: return TactVoidTypeEx.INSTANCE
        val resultType = result.type.toEx()

        return resultType
    }

    private fun TactBlock?.getType(
        context: ResolveState?,
    ): TactTypeEx? {
        val lastIfStatement = this?.statementList?.lastOrNull()
        val lastIfExpressionList = lastIfStatement?.childrenOfType<TactListExpression>()?.lastOrNull()
        return lastIfExpressionList?.getType(context)
    }

    private fun typeOrParameterType(typeOwner: TactTypeOwner, context: ResolveState?): TactTypeEx? {
        val type = typeOwner.getType(context)
        if (typeOwner is TactParamDefinition && typeOwner.isVariadic && type != null) {
            return TactArrayTypeEx(type, typeOwner)
        }
        return type
    }

    private fun inferTypeWithSmartcasts(resolved: TactTypeOwner, type: TactTypeEx?, expr: TactReferenceExpression): TactTypeEx? {
        if (resolved !is TactVarDefinition &&
            resolved !is TactModuleVarDefinition &&
            resolved !is TactParamDefinition &&
            resolved !is TactFieldDefinition
        ) {
            // if identifier is not and some var or field, we don't need to apply any smartcasts
            return type
        }

        if (!needSmartcast(expr)) {
            // fast path
            // don't apply any smartcasts if we are on the left side of assignment
            // For example:
            // ```
            // mut a := Foo{} as IFoo
            // if a is Foo {
            //    // ...
            //    a = Bar{} // Bar implements IFoo as well
            // }
            // ```
            // In this case, we don't want to apply smartcast to `a` in `a = Foo{}`, since
            // then in type checker we will get error that `Bar` cannot be assigned to `Foo`.
            return type
        }

        return type
    }

    private fun needSmartcast(expr: TactExpression): Boolean {
        val parent = expr.parent
        if (parent is TactAssignmentStatement) {
            val left = parent.listExpression
            if (left.isEquivalentTo(expr)) {
                return false
            }
        }

        if (parent is TactUnaryExpr && parent.mul != null) {
            return needSmartcast(parent)
        }

        return true
    }

    fun TactVarDefinition.getVarType(context: ResolveState?): TactTypeEx? {
        val parent = PsiTreeUtil.getStubOrPsiParent(this)
//        if (parent is TactRangeClause) {
//            return processRangeClause(this, parent, context)
//        }

        if (parent is TactVarDeclaration) {
            val hint = parent.typeHint
            if (hint != null) {
                return hint.type.toEx()
            }

            return parent.expression?.getType(context)
        }

        if (parent is TactForEachStatement) {
            val expr = parent.expression ?: return null
            val exprType = expr.getType(context) as? TactMapTypeEx ?: return null
            val key = parent.key ?: return null
            val value = parent.value ?: return null
            if (key.isEquivalentTo(this)) {
                return exprType.key
            }
            if (value.isEquivalentTo(this)) {
                return exprType.value
            }
        }

        val literal = PsiTreeUtil.getNextSiblingOfType(this, TactLiteral::class.java)
        if (literal != null) {
            return literal.getType(context)
        }

        return null
    }

    private fun getTypeInVarSpec(
        o: TactVarDefinition,
        decl: TactVarDeclaration,
        context: ResolveState?,
        needUnwrap: Boolean,
    ): TactTypeEx? {
        return decl.expression?.getType(context).chainIf(needUnwrap) {
            unwrapOptionOrResultType(this)
        }
    }

    private fun unwrapOptionOrResultTypeIf(type: TactTypeEx?, condition: Boolean): TactTypeEx? {
        if (!condition) return type
        return unwrapOptionOrResultType(type)
    }

    private fun unwrapOptionOrResultType(type: TactTypeEx?): TactTypeEx? {
        if (type is TactOptionTypeEx) {
            return type.inner
        }
        return type
    }

    private fun callerType(call: TactCallExpr): TactTypeEx? {
        val callExpression = call.expression as? TactReferenceExpression ?: return null
        val caller = callExpression.getQualifier() as? TactTypeOwner ?: return null
        val type = caller.getType(null)

        return unwrapOptionOrResultTypeIf(type, false)
    }

    fun getContextType(element: PsiElement): TactTypeEx? {
        val parentStructInit = element.parentNth<TactLiteralValueExpression>(3)
        if (parentStructInit != null) {
            val withoutKeys = parentStructInit.elementList.any { it.key == null }
            if (withoutKeys) {
                val elem = parentStructInit.elementList.firstOrNull { PsiTreeUtil.isAncestor(it.value, element, false) }
                val key = elem?.key
                val fieldName = key?.fieldName?.text ?: return null
                val struct = parentStructInit.getType(null) as? TactStructTypeEx ?: return null
                val structDecl = struct.resolve(element.project) ?: return null
                val structType = structDecl.structType
                return structType.fieldList.firstOrNull { it.name == fieldName }?.getType(null)
            }
        }

        if (element.parent is TactValue) {
            val parentElement = element.parentNth<TactElement>(2)
            val key = parentElement?.key
            if (key?.fieldName != null) {
                val resolved = key.fieldName?.resolve() as? TactFieldDefinition
                val declaration = resolved?.parent as? TactFieldDeclaration
                val resolvedType = declaration?.type?.toEx()
                if (resolvedType != null) {
                    return resolvedType
                }
            }
        }

        if (element.parent is TactDefaultFieldValue) {
            val fieldDeclaration = element.parentOfType<TactFieldDeclaration>() ?: return null
            return fieldDeclaration.type.toEx()
        }

        if (element.parent is TactAddExpr) {
            val addExpr = element.parent as TactAddExpr
            if (addExpr.bitOr != null) {
                return getContextType(addExpr)
            }
        }

        if (element.parent is TactBinaryExpr) {
            val binaryExpr = element.parent as TactBinaryExpr
            val right = binaryExpr.right ?: return null
            if (binaryExpr.right != null && right.isEquivalentTo(element)) {
                val left = binaryExpr.left
                val leftType = left.getType(null)
                if (leftType is TactArrayTypeEx) {
                    return leftType.inner
                }
                return leftType
            }
        }

        if (element.parent is TactAssignmentStatement) {
            val assign = element.parent as TactAssignmentStatement
            // TODO: support multi assign
            val assignExpression = assign.leftExpressions.firstOrNull() as? TactTypeOwner ?: return null
            return assignExpression.getType(null)
        }

        val callExpr = TactCodeInsightUtil.getCallExpr(element)
        if (callExpr != null) {
            val index = callExpr.paramIndexOf(element)
            if (index == -1) return null

            val function = callExpr.resolve() as? TactSignatureOwner ?: return null
            val params = function.getSignature()?.parameters?.paramDefinitionList ?: return null

            val param = params.getOrNull(index) ?: return null
            val paramType = param.type.toEx()

            return paramType
        }

        return null
    }

    private fun isBoolExpr(expr: TactExpression) = expr is TactConditionalExpr ||
            expr is TactAndExpr ||
            expr is TactOrExpr

}
