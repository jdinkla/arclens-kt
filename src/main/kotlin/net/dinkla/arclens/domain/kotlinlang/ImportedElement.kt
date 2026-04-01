package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ImportedElement(
    val name: String,
) {
    override fun toString(): String = name

    val packageName: PackageName
        get() {
            val index = name.lastIndexOf(".")
            return PackageName(
                if (index >= 0) {
                    name.substring(0, index)
                } else {
                    name
                },
            )
        }
}
