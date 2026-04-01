package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
sealed interface Declaration {
    val name: String
    val visibilityModifier: VisibilityModifier?
}
