package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    override val name: String,
    val dataType: Type? = null,
    val modifier: PropertyModifier = PropertyModifier.VAL,
    override val visibilityModifier: VisibilityModifier? = null,
    val memberModifier: List<MemberModifier> = listOf(),
) : Declaration
