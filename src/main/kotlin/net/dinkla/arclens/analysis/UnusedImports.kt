package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.Declaration
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.TypeAlias

@Serializable
data class UnusedImport(
    val filePath: FilePath,
    val importName: String,
)

@Serializable
data class UnusedImportsReport(
    val unusedImports: List<UnusedImport>,
    val totalUnusedImports: Int,
) {
    companion object {
        fun from(project: Project): UnusedImportsReport {
            val unused =
                project
                    .flatMap { file -> findUnusedImports(file) }
                    .sortedBy { "${it.filePath}-${it.importName}" }
            return UnusedImportsReport(
                unusedImports = unused,
                totalUnusedImports = unused.size,
            )
        }
    }
}

internal fun findUnusedImports(file: KotlinFile): List<UnusedImport> {
    val usedNames = collectUsedNames(file.declarations)
    return file.imports
        .filter { import -> simpleName(import) !in usedNames }
        .map { UnusedImport(file.filePath, it.name.name) }
}

private fun simpleName(import: Import): String {
    val fqName = import.name.name
    val lastDot = fqName.lastIndexOf('.')
    return if (lastDot >= 0) fqName.substring(lastDot + 1) else fqName
}

private fun collectUsedNames(declarations: List<Declaration>): Set<String> =
    buildSet {
        for (decl in declarations) {
            add(decl.name)
            when (decl) {
                is FunctionSignature -> collectFromFunction(decl)
                is ClassSignature -> collectFromClass(decl)
                is Property -> decl.dataType?.name?.let { addTypeNames(it) }
                is TypeAlias -> decl.def.name?.let { addTypeNames(it) }
            }
        }
    }

private fun MutableSet<String>.collectFromFunction(fn: FunctionSignature) {
    fn.returnType?.name?.let { addTypeNames(it) }
    fn.parameters.forEach { addTypeNames(it.type.name ?: "") }
    fn.extensionOf?.let { add(it) }
}

private fun MutableSet<String>.collectFromClass(cls: ClassSignature) {
    cls.superTypes.forEach { add(it) }
    cls.parameters.forEach { addTypeNames(it.type.name ?: "") }
    addAll(collectUsedNames(cls.declarations))
}

private fun MutableSet<String>.addTypeNames(typeName: String) {
    typeName
        .split(Regex("[^A-Za-z0-9_]+"))
        .filter { it.isNotEmpty() }
        .forEach { add(it) }
}
