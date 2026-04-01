package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Type(
    val name: String?,
) {
    override fun toString(): String = name ?: ""
}
