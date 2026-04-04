package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class FunctionSignature(
    override val name: String,
    val returnType: Type? = null,
    val parameters: List<FunctionParameter> = listOf(),
    val extensionOf: String? = null,
    override val visibilityModifier: VisibilityModifier? = null,
    val memberModifier: MemberModifier? = null,
    val functionModifiers: List<FunctionModifier> = listOf(),
    val lineCount: Int = 0,
) : Declaration
