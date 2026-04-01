package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
enum class MemberModifier(
    val text: String,
) {
    OVERRIDE("override"),
    LATE_INIT("lateinit"),
}
