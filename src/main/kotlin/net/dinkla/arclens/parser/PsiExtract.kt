package net.dinkla.arclens.parser

import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassModifier
import net.dinkla.arclens.domain.kotlinlang.ClassParameter
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.ConstructorSignature
import net.dinkla.arclens.domain.kotlinlang.Declaration
import net.dinkla.arclens.domain.kotlinlang.FunctionModifier
import net.dinkla.arclens.domain.kotlinlang.FunctionParameter
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.ImportedElement
import net.dinkla.arclens.domain.kotlinlang.InheritanceModifier
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.MemberModifier
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.ParameterModifier
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.PropertyModifier
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.domain.kotlinlang.TypeAlias
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType

/**
 * Extract a KotlinFile domain model from a PSI KtFile.
 */
internal fun extractKotlinFile(
    ktFile: KtFile,
    filePath: FilePath,
    lastModified: Long = 0L,
    fileSize: Long = 0L,
): KotlinFile {
    val packageName = extractPackageName(ktFile)
    val imports = extractImports(ktFile)
    val declarations = extractDeclarations(ktFile)
    val lineCount = ktFile.text.lines().size
    return KotlinFile(filePath, packageName, imports, declarations, lastModified, fileSize, lineCount)
}

/**
 * Extract the package name from a KtFile.
 */
private fun extractPackageName(ktFile: KtFile): PackageName = PackageName(ktFile.packageFqName.asString())

/**
 * Extract imports from a KtFile.
 */
private fun extractImports(ktFile: KtFile): List<Import> =
    ktFile.importDirectives.mapNotNull { importDirective ->
        importDirective.importedFqName?.asString()?.let { fqName ->
            Import(ImportedElement(fqName))
        }
    }

/**
 * Extract top-level declarations from a KtFile.
 */
private fun extractDeclarations(ktFile: KtFile): List<Declaration> =
    ktFile.declarations.mapNotNull { declaration ->
        when (declaration) {
            is KtClass -> extractClass(declaration)
            is KtObjectDeclaration -> extractObject(declaration)
            is KtNamedFunction -> extractFunction(declaration)
            is KtProperty -> extractProperty(declaration)
            is KtTypeAlias -> extractTypeAlias(declaration)
            else -> null
        }
    }

/**
 * Extract a ClassSignature from a KtClass.
 */
private fun extractClass(ktClass: KtClass): ClassSignature {
    val name = ktClass.name ?: "ERROR_CLASS_NAME"
    val elementType =
        when {
            ktClass.isInterface() -> ClassSignature.Type.INTERFACE
            else -> ClassSignature.Type.CLASS
        }
    val parameters = ktClass.primaryConstructorParameters.map { extractClassParameter(it) }
    val superTypes = extractSuperTypes(ktClass)
    val visibilityModifier = extractVisibilityModifier(ktClass)
    val classModifier = extractClassModifier(ktClass)
    val inheritanceModifier = extractInheritanceModifier(ktClass)
    val declarations = extractBodyDeclarations(ktClass)

    return ClassSignature(
        name = name,
        parameters = parameters,
        superTypes = superTypes,
        visibilityModifier = visibilityModifier,
        elementType = elementType,
        classModifier = classModifier,
        inheritanceModifier = inheritanceModifier,
        declarations = declarations,
    )
}

/**
 * Extract a ClassSignature (as OBJECT type) from a KtObjectDeclaration.
 */
private fun extractObject(ktObject: KtObjectDeclaration): ClassSignature {
    val name = ktObject.name ?: "ERROR_OBJECT_NAME"
    val superTypes = extractSuperTypes(ktObject)
    val declarations = extractBodyDeclarations(ktObject)

    return ClassSignature(
        name = name,
        parameters = emptyList(),
        superTypes = superTypes,
        elementType = ClassSignature.Type.OBJECT,
        declarations = declarations,
    )
}

/**
 * Extract super types from a class or object.
 */
private fun extractSuperTypes(classOrObject: KtClassOrObject): List<String> =
    classOrObject.superTypeListEntries.mapNotNull { entry ->
        entry.typeReference?.let { extractTypeName(it) }
    }

/**
 * Extract class body declarations (functions, properties, nested classes).
 */
private fun extractBodyDeclarations(classOrObject: KtClassOrObject): List<Declaration> =
    classOrObject.declarations.mapNotNull { declaration ->
        when (declaration) {
            is KtNamedFunction -> extractFunction(declaration)
            is KtProperty -> extractProperty(declaration)
            is KtClass -> extractClass(declaration)
            is KtObjectDeclaration -> extractObject(declaration)
            is KtSecondaryConstructor -> extractSecondaryConstructor(declaration)
            else -> null
        }
    }

/**
 * Extract a ClassParameter from a constructor parameter.
 */
private fun extractClassParameter(parameter: KtParameter): ClassParameter {
    val name = parameter.name ?: "ERROR_PARAM_NAME"
    val type = parameter.typeReference?.let { extractType(it) } ?: Type("ERROR_PARAM_TYPE")
    val visibilityModifier = extractVisibilityModifierFromParameter(parameter)
    val propertyModifier =
        when {
            parameter.hasValOrVar() && parameter.isMutable -> PropertyModifier.VAR
            parameter.hasValOrVar() -> PropertyModifier.VAL
            else -> null
        }
    return ClassParameter(name, type, visibilityModifier, propertyModifier)
}

/**
 * Extract a ConstructorSignature from a KtSecondaryConstructor.
 */
private fun extractSecondaryConstructor(constructor: KtSecondaryConstructor): ConstructorSignature {
    val parameters = constructor.valueParameters.map { extractFunctionParameter(it) }
    val visibilityModifier = extractVisibilityModifier(constructor)
    val lineCount = constructor.text.lines().size
    return ConstructorSignature(
        parameters = parameters,
        visibilityModifier = visibilityModifier,
        lineCount = lineCount,
    )
}

/**
 * Extract a FunctionSignature from a KtNamedFunction.
 */
private fun extractFunction(function: KtNamedFunction): FunctionSignature {
    val name = function.name ?: "ERROR_FUNCTION_NAME"
    val parameters = function.valueParameters.map { extractFunctionParameter(it) }
    val returnType = function.typeReference?.let { extractType(it) }
    val extensionOf = function.receiverTypeReference?.let { extractTypeName(it) }
    val visibilityModifier = extractVisibilityModifier(function)
    val memberModifier = extractMemberModifier(function)
    val functionModifiers = extractFunctionModifiers(function)
    val lineCount = function.text.lines().size

    return FunctionSignature(
        name = name,
        returnType = returnType,
        parameters = parameters,
        extensionOf = extensionOf,
        visibilityModifier = visibilityModifier,
        memberModifier = memberModifier,
        functionModifiers = functionModifiers,
        lineCount = lineCount,
    )
}

/**
 * Extract a FunctionParameter from a KtParameter.
 */
private fun extractFunctionParameter(parameter: KtParameter): FunctionParameter {
    val name = parameter.name ?: "ERROR_PARAM_NAME"
    val type = parameter.typeReference?.let { extractType(it) } ?: Type("ERROR_PARAM_TYPE")
    val modifier = extractParameterModifier(parameter)
    return FunctionParameter(name, type, modifier)
}

/**
 * Extract a Property from a KtProperty.
 */
private fun extractProperty(property: KtProperty): Property {
    val name = property.name ?: "ERROR_PROPERTY_NAME"
    val type = property.typeReference?.let { extractType(it) }
    val isConst = property.hasModifier(KtTokens.CONST_KEYWORD)
    val isMutable = property.isVar
    val propertyModifier = PropertyModifier.create(isConst, isMutable)
    val visibilityModifier = extractVisibilityModifier(property)
    val memberModifiers = extractMemberModifiers(property)

    return Property(
        name = name,
        dataType = type,
        modifier = propertyModifier,
        visibilityModifier = visibilityModifier,
        memberModifier = memberModifiers,
    )
}

/**
 * Extract a TypeAlias from a KtTypeAlias.
 */
private fun extractTypeAlias(typeAlias: KtTypeAlias): TypeAlias {
    val name = typeAlias.name ?: "ERROR_TYPEALIAS_NAME"
    val type = typeAlias.getTypeReference()?.let { extractType(it) } ?: Type("ERROR_TYPEALIAS_TYPE")
    val visibilityModifier = extractVisibilityModifier(typeAlias)
    return TypeAlias(name, type, visibilityModifier)
}

/**
 * Extract a Type from a KtTypeReference.
 */
private fun extractType(typeReference: KtTypeReference): Type {
    val typeElement = typeReference.typeElement
    val isSuspend = typeReference.hasModifier(KtTokens.SUSPEND_KEYWORD)
    val suspendPrefix = if (isSuspend) "suspend " else ""
    return when (typeElement) {
        is KtNullableType -> {
            val innerType = typeElement.innerType
            when (innerType) {
                is KtUserType -> Type("${extractUserTypeName(innerType)}?")
                is KtFunctionType -> Type("$suspendPrefix${extractFunctionTypeName(innerType)}?")
                else -> Type("${innerType?.text ?: "ERROR"}?")
            }
        }

        is KtUserType -> {
            Type(extractUserTypeName(typeElement))
        }

        is KtFunctionType -> {
            Type("$suspendPrefix${extractFunctionTypeName(typeElement)}")
        }

        else -> {
            Type(typeElement?.text ?: "ERROR_TYPE")
        }
    }
}

/**
 * Extract the simple type name from a type reference (for super types).
 */
private fun extractTypeName(typeReference: KtTypeReference): String {
    val typeElement = typeReference.typeElement
    return when (typeElement) {
        is KtUserType -> typeElement.referencedName ?: "ERROR"
        else -> typeElement?.text ?: "ERROR"
    }
}

/**
 * Extract user type name including generic parameters.
 */
private fun extractUserTypeName(userType: KtUserType): String {
    val baseName = userType.referencedName ?: "ERROR"
    val typeArgs = userType.typeArguments
    return if (typeArgs.isEmpty()) {
        baseName
    } else {
        val args = typeArgs.mapNotNull { it.typeReference?.let { ref -> extractType(ref).name } }
        "$baseName<${args.joinToString(",")}>"
    }
}

/**
 * Extract function type representation.
 */
private fun extractFunctionTypeName(functionType: KtFunctionType): String {
    val params =
        functionType.parameters.mapNotNull { param ->
            param.typeReference?.let { extractType(it).name }
        }
    val returnType = functionType.returnTypeReference?.let { extractType(it).name } ?: "Unit"
    return "(${params.joinToString(",")}) -> $returnType"
}

// ==================== Modifier Extraction ====================

/**
 * Extract visibility modifier from a declaration.
 */
private fun extractVisibilityModifier(declaration: org.jetbrains.kotlin.psi.KtModifierListOwner): VisibilityModifier? =
    when {
        declaration.hasModifier(KtTokens.PUBLIC_KEYWORD) -> VisibilityModifier.PUBLIC
        declaration.hasModifier(KtTokens.PRIVATE_KEYWORD) -> VisibilityModifier.PRIVATE
        declaration.hasModifier(KtTokens.INTERNAL_KEYWORD) -> VisibilityModifier.INTERNAL
        declaration.hasModifier(KtTokens.PROTECTED_KEYWORD) -> VisibilityModifier.PROTECTED
        else -> null
    }

/**
 * Extract visibility modifier from a constructor parameter.
 */
private fun extractVisibilityModifierFromParameter(parameter: KtParameter): VisibilityModifier? =
    when {
        parameter.hasModifier(KtTokens.PUBLIC_KEYWORD) -> VisibilityModifier.PUBLIC
        parameter.hasModifier(KtTokens.PRIVATE_KEYWORD) -> VisibilityModifier.PRIVATE
        parameter.hasModifier(KtTokens.INTERNAL_KEYWORD) -> VisibilityModifier.INTERNAL
        parameter.hasModifier(KtTokens.PROTECTED_KEYWORD) -> VisibilityModifier.PROTECTED
        else -> null
    }

/**
 * Extract class modifier (data, enum, value, inner, sealed).
 */
private fun extractClassModifier(ktClass: KtClass): ClassModifier? =
    when {
        ktClass.isData() -> ClassModifier.DATA
        ktClass.isEnum() -> ClassModifier.ENUM
        ktClass.isValue() -> ClassModifier.VALUE
        ktClass.isInner() -> ClassModifier.INNER
        ktClass.isSealed() -> ClassModifier.SEALED
        else -> null
    }

/**
 * Extract inheritance modifier (open, abstract).
 */
private fun extractInheritanceModifier(
    declaration: org.jetbrains.kotlin.psi.KtModifierListOwner,
): InheritanceModifier? =
    when {
        declaration.hasModifier(KtTokens.OPEN_KEYWORD) -> InheritanceModifier.OPEN
        declaration.hasModifier(KtTokens.ABSTRACT_KEYWORD) -> InheritanceModifier.ABSTRACT
        else -> null
    }

/**
 * Extract member modifier (override, lateinit) - returns first found.
 */
private fun extractMemberModifier(declaration: org.jetbrains.kotlin.psi.KtModifierListOwner): MemberModifier? =
    when {
        declaration.hasModifier(KtTokens.OVERRIDE_KEYWORD) -> MemberModifier.OVERRIDE
        declaration.hasModifier(KtTokens.LATEINIT_KEYWORD) -> MemberModifier.LATE_INIT
        else -> null
    }

/**
 * Extract member modifiers (override, lateinit) - returns all found.
 */
private fun extractMemberModifiers(declaration: org.jetbrains.kotlin.psi.KtModifierListOwner): List<MemberModifier> =
    buildList {
        if (declaration.hasModifier(KtTokens.OVERRIDE_KEYWORD)) add(MemberModifier.OVERRIDE)
        if (declaration.hasModifier(KtTokens.LATEINIT_KEYWORD)) add(MemberModifier.LATE_INIT)
    }

/**
 * Extract function modifiers (suspend, inline, infix, tailrec, operator, external).
 */
private fun extractFunctionModifiers(function: KtNamedFunction): List<FunctionModifier> =
    buildList {
        if (function.hasModifier(KtTokens.SUSPEND_KEYWORD)) add(FunctionModifier.SUSPEND)
        if (function.hasModifier(KtTokens.INLINE_KEYWORD)) add(FunctionModifier.INLINE)
        if (function.hasModifier(KtTokens.INFIX_KEYWORD)) add(FunctionModifier.INFIX)
        if (function.hasModifier(KtTokens.TAILREC_KEYWORD)) add(FunctionModifier.TAILREC)
        if (function.hasModifier(KtTokens.OPERATOR_KEYWORD)) add(FunctionModifier.OPERATOR)
        if (function.hasModifier(KtTokens.EXTERNAL_KEYWORD)) add(FunctionModifier.EXTERNAL)
    }

/**
 * Extract parameter modifier (vararg, noinline, crossinline).
 */
private fun extractParameterModifier(parameter: KtParameter): ParameterModifier? =
    when {
        parameter.hasModifier(KtTokens.VARARG_KEYWORD) -> ParameterModifier.VARARG
        parameter.hasModifier(KtTokens.NOINLINE_KEYWORD) -> ParameterModifier.NOINLINE
        parameter.hasModifier(KtTokens.CROSSINLINE_KEYWORD) -> ParameterModifier.CROSSINLINE
        else -> null
    }
