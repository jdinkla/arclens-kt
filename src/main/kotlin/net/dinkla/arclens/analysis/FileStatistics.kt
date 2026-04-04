package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.Declaration
import net.dinkla.arclens.domain.kotlinlang.DeclarationContainer
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier
import net.dinkla.arclens.domain.statistics.Coupling

@Serializable
data class FileStatistics(
    val filePath: FilePath,
    val imports: List<Import>,
    val declarations: List<GeneralDeclaration>,
    val metrics: FileMetrics,
    val coupling: Coupling,
) {
    companion object {
        fun from(
            project: Project,
            declarationFilter: DeclarationFilter = DeclarationFilter.INCLUDE_ALL,
            importFilter: ImportFilter = ImportFilter.INCLUDE_ALL,
        ): List<FileStatistics> =
            project.files.map { file ->
                fileStatistics(file, declarationFilter, importFilter)
            }
    }
}

@Serializable
data class GeneralDeclaration(
    val name: String,
    val visibilityModifier: VisibilityModifier?,
)

@Serializable
data class FileMetrics(
    val imports: Int,
    val declarations: Int,
    val classes: Int,
    val functions: Int,
    val properties: Int,
    val aliases: Int,
    val lineCount: Int = 0,
) {
    companion object {
        fun default() = FileMetrics(0, 0, 0, 0, 0, 0)
    }
}

enum class DeclarationFilter(
    val filter: (Declaration) -> Boolean,
) {
    EXCLUDE_PRIVATE_DECLARATIONS({ it.visibilityModifier != VisibilityModifier.PRIVATE }),

    INCLUDE_ALL({ true }),
    ;

    companion object {
        fun select(flag: Boolean) = if (flag) INCLUDE_ALL else EXCLUDE_PRIVATE_DECLARATIONS
    }
}

enum class ImportFilter(
    val filter: (KotlinFile, Import) -> Boolean,
) {
    EXCLUDE_IMPORTS_FROM_OTHER_PACKAGES({ file, import -> !import.name.packageName.isOtherPackage(file.packageName) }),

    INCLUDE_ALL({ _, _ -> true }),
    ;

    companion object {
        fun select(flag: Boolean) = if (flag) INCLUDE_ALL else EXCLUDE_IMPORTS_FROM_OTHER_PACKAGES
    }
}

private fun fileStatistics(
    file: KotlinFile,
    declarationFilter: DeclarationFilter,
    importFilter: ImportFilter,
): FileStatistics {
    val pname = file.packageName.toString()
    val filteredDeclarations =
        file.declarations
            .filter { declarationFilter.filter(it) }
    val declaredElements =
        filteredDeclarations
            .map { GeneralDeclaration("$pname.${it.name}", it.visibilityModifier) }
    val declarations =
        object : DeclarationContainer {
            override val declarations: List<Declaration>
                get() = filteredDeclarations
        }
    val imports =
        file.imports.filter { importFilter.filter(file, it) }
    return FileStatistics(
        filePath = file.filePath,
        imports = imports,
        declarations = declaredElements,
        metrics =
            FileMetrics(
                imports = imports.size,
                declarations = declarations.size,
                classes = declarations.classes.size,
                functions = declarations.functions.size,
                properties = declarations.properties.size,
                aliases = declarations.typeAliases.size,
                lineCount = file.lineCount,
            ),
        coupling = Coupling(declarations.size, imports.size),
    )
}
