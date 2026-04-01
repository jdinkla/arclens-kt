package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.Project

@Serializable
data class Search(
    val classes: List<ClassSignature>,
    val superClasses: List<ClassSignature>,
    val subClasses: List<ClassSignature>,
)

fun Project.search(className: String): Search {
    val classes = getClass(className)
    val superClasses = getSuperClasses(className)
    val subClasses = getSubClasses(className)
    return Search(classes, superClasses, subClasses)
}
