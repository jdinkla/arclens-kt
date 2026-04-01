package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class ClassParameter(
    val name: String,
    val type: Type,
    val visibilityModifier: VisibilityModifier? = null,
    val propertyModifier: PropertyModifier? = null,
)
