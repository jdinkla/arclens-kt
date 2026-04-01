package net.dinkla.arclens.domain.kotlinlang

interface DeclarationContainer {
    val declarations: List<Declaration>

    val functions: List<FunctionSignature>
        get() = declarations.filterIsInstance<FunctionSignature>()

    val properties: List<Property>
        get() = declarations.filterIsInstance<Property>()

    val classes: List<ClassSignature>
        get() = declarations.filterIsInstance<ClassSignature>()

    val typeAliases: List<TypeAlias>
        get() = declarations.filterIsInstance<TypeAlias>()

    val size: Int
        get() = declarations.size
}
