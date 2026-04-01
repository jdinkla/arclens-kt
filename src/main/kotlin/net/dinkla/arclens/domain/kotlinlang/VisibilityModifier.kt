package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
enum class VisibilityModifier(
    val text: String,
) {
    PUBLIC(""),
    PRIVATE("private"),
    INTERNAL("internal"),
    PROTECTED("protected"),
}
