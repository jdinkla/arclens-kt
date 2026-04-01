package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.FilePath

@Serializable
data class KotlinFile(
    val filePath: FilePath,
    val packageName: PackageName,
    val imports: List<Import> = listOf(),
    override val declarations: List<Declaration> = listOf(),
    val lastModified: Long = 0L,
    val fileSize: Long = 0L,
) : DeclarationContainer {
    val fullyQualifiedName: String
        get() {
            val name = filePath.fileName.replace(".kt", "")
            return "${packageName.name}.$name"
        }
}
