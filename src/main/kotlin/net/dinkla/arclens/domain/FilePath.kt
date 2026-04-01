package net.dinkla.arclens.domain

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
@JvmInline
value class FilePath(
    val path: String,
) : Comparable<FilePath> {
    val fileName: String
        get() {
            val index = max(path.lastIndexOf("/"), path.lastIndexOf("\\"))
            return if (index >= 0) {
                path.substring(index + 1)
            } else {
                path
            }
        }

    fun withoutDirectory(directory: String): String = path.substring(directory.length + 1)

    override fun compareTo(other: FilePath): Int = path.compareTo(other.path)
}
