package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
data class Import(
    val name: ImportedElement,
)
