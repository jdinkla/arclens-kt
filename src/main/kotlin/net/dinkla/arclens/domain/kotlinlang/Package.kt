package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class Package(
    val packageName: PackageName,
    val files: List<KotlinFile>,
) : DeclarationContainer {
    override val declarations: List<Declaration>
        get() = files.flatMap { it.declarations }

    fun imports(): Set<Import> = files.flatMap { it.imports }.toSet()

    override fun toString(): String = "$packageName: ${files.joinToString(", ") { it.packageName.toString() }}"
}
