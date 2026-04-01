package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
enum class FunctionModifier(
    val text: String,
) {
    SUSPEND("suspend"),
    INLINE("inline"),
    INFIX("infix"),
    TAILREC("tailrec"),
    OPERATOR("operator"),
    EXTERNAL("external"),
}
