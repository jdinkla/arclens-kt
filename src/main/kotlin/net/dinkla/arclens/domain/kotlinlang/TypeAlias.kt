package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class TypeAlias(
    override val name: String,
    val def: Type,
    override val visibilityModifier: VisibilityModifier? = null,
) : Declaration
