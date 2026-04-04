package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class ConstructorSignature(
    override val name: String = "constructor",
    val parameters: List<FunctionParameter> = listOf(),
    override val visibilityModifier: VisibilityModifier? = null,
    val lineCount: Int = 0,
) : Declaration
