package net.dinkla.arclens.domain.kotlinlang

import kotlinx.serialization.Serializable

@Serializable
class Project(
    val directory: String,
    val files: List<KotlinFile>,
    val directories: List<String> = listOf(directory),
    val parseTimestamp: Long = 0L,
) : List<KotlinFile> by files {
    fun packages(): List<Package> =
        files
            .groupBy { it.packageName }
            .map { (name, files) -> Package(name, files) }
            .sortedBy { it.packageName.name }

    fun relativePath(fileName: String): String {
        // Find the directory that contains this file and remove it as prefix
        val matchingDir = directories.find { fileName.startsWith(it) }
        return if (matchingDir != null) {
            fileName.removePrefix(matchingDir).removePrefix("/")
        } else {
            fileName.removePrefix(directory).removePrefix("/")
        }
    }

    fun getClass(className: String): List<ClassSignature> =
        flatMap { it.classes }
            .filter { it.name == className }

    fun getSuperClasses(className: String): List<ClassSignature> {
        val clazz = getClass(className)
        return clazz +
            clazz
                .flatMap { it.superTypes }
                .flatMap { getSuperClasses(it) }
    }

    fun getSubClasses(className: String): List<ClassSignature> =
        flatMap { it.classes }
            .filter { it.superTypes.contains(className) }
}
