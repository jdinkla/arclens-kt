package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class FunctionParameter(
    val name: String,
    val type: Type,
    val modifier: ParameterModifier? = null,
)
