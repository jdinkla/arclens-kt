package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PackageName(
    val name: String,
) {
    infix fun isSubPackageOf(other: PackageName): Boolean = name.startsWith("${other.name}.")

    infix fun isSuperPackage(other: PackageName): Boolean = other isSubPackageOf this

    infix fun isSidePackage(other: PackageName): Boolean =
        !isSubPackageOf(other) &&
            !isSuperPackage(other) &&
            name.commonPrefixWith(other.name).isNotEmpty()

    infix fun isOtherPackage(other: PackageName): Boolean = name.commonPrefixWith(other.name).isEmpty()

    override fun toString(): String = name
}
