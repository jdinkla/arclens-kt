package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.kotlinlang.ClassModifier
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.InheritanceModifier
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier

@Serializable
data class ClassStatistics(
    val className: String,
    val packageName: PackageName,
    val visibilityModifier: VisibilityModifier? = null,
    val elementType: ClassSignature.Type = ClassSignature.Type.CLASS,
    val classModifier: ClassModifier? = null,
    val inheritanceModifier: InheritanceModifier? = null,
    val metrics: ClassMetrics = ClassMetrics.default(),
) {
    companion object {
        fun from(project: Project): List<ClassStatistics> =
            project
                .flatMap { file ->
                    file.classes.map {
                        from(project, file.packageName, it)
                    }
                }.sortedBy { "${it.packageName}-${it.className}" }

        private fun from(
            project: Project,
            packageName: PackageName,
            clazz: ClassSignature,
        ): ClassStatistics =
            ClassStatistics(
                className = clazz.name,
                packageName = packageName,
                visibilityModifier = clazz.visibilityModifier,
                elementType = clazz.elementType,
                classModifier = clazz.classModifier,
                inheritanceModifier = clazz.inheritanceModifier,
                metrics = ClassMetrics.from(project, clazz),
            )
    }
}

@Serializable
data class ClassMetrics(
    val parameters: Int,
    val superTypes: Int,
    val declarations: Int,
    val classes: Int,
    val functions: Int,
    val properties: Int,
    val aliases: Int,
    val superClasses: Int,
    val subClasses: Int,
) {
    companion object {
        fun from(
            project: Project,
            clazz: ClassSignature,
        ): ClassMetrics =
            ClassMetrics(
                parameters = clazz.parameters.size,
                superTypes = clazz.superTypes.size,
                declarations = clazz.declarations.size,
                classes = clazz.classes.size,
                functions = clazz.functions.size,
                properties = clazz.properties.size,
                aliases = clazz.typeAliases.size,
                superClasses = project.getSuperClasses(clazz.name).size - 1,
                subClasses = project.getSubClasses(clazz.name).size,
            )

        fun default(): ClassMetrics = ClassMetrics(0, 0, 0, 0, 0, 0, 0, 0, 0)
    }
}
