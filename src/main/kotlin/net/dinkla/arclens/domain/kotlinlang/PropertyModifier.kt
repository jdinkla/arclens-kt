package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
enum class PropertyModifier(
    val text: String,
) {
    VAR("var"),
    VAL("val"),
    CONST_VAL("const val"),
    ;

    companion object {
        fun create(
            hasConstModifier: Boolean,
            isMutable: Boolean,
        ): PropertyModifier =
            when {
                hasConstModifier -> CONST_VAL
                isMutable -> VAR
                else -> VAL
            }
    }
}
