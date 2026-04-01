package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
enum class InheritanceModifier(
    val text: String,
) {
    OPEN("open"),
    ABSTRACT("abstract"),
}
