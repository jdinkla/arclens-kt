package net.dinkla.arclens.extract

import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.Project
import java.io.File

enum class ChangeStatus {
    NEW,
    MODIFIED,
    DELETED,
    UNCHANGED,
}

data class FileChange(
    val path: String,
    val status: ChangeStatus,
)

fun detectChanges(
    cachedProject: Project?,
    currentFiles: List<File>,
): List<FileChange> {
    if (cachedProject == null) {
        return currentFiles.map { FileChange(it.absolutePath, ChangeStatus.NEW) }
    }

    val cachedFileMap =
        cachedProject.files.associateBy { cachedFile ->
            resolveAbsolutePath(cachedProject.directories, cachedFile.filePath.path)
        }
    val currentFileSet = currentFiles.map { it.absolutePath }.toSet()

    val changes = mutableListOf<FileChange>()

    for (file in currentFiles) {
        val absolutePath = file.absolutePath
        val cachedFile = cachedFileMap[absolutePath]
        if (cachedFile == null) {
            changes.add(FileChange(absolutePath, ChangeStatus.NEW))
        } else if (hasFileChanged(file, cachedFile)) {
            changes.add(FileChange(absolutePath, ChangeStatus.MODIFIED))
        } else {
            changes.add(FileChange(absolutePath, ChangeStatus.UNCHANGED))
        }
    }

    for ((cachedPath, _) in cachedFileMap) {
        if (cachedPath !in currentFileSet) {
            changes.add(FileChange(cachedPath, ChangeStatus.DELETED))
        }
    }

    return changes
}

private fun resolveAbsolutePath(
    directories: List<String>,
    relativePath: String,
): String {
    for (dir in directories) {
        val fullPath =
            if (relativePath.startsWith("/")) {
                "$dir$relativePath"
            } else {
                "$dir/$relativePath"
            }
        if (File(fullPath).exists()) {
            return fullPath
        }
    }
    val primaryDir = directories.first()
    return if (relativePath.startsWith("/")) {
        "$primaryDir$relativePath"
    } else {
        "$primaryDir/$relativePath"
    }
}

private fun hasFileChanged(
    file: File,
    cachedFile: KotlinFile,
): Boolean {
    if (cachedFile.lastModified == 0L || cachedFile.fileSize == 0L) {
        return true
    }
    return file.lastModified() != cachedFile.lastModified || file.length() != cachedFile.fileSize
}

fun filterFilesToParse(changes: List<FileChange>): List<File> =
    changes
        .filter { it.status == ChangeStatus.NEW || it.status == ChangeStatus.MODIFIED }
        .map { File(it.path) }

fun reuseFromCache(
    cachedProject: Project,
    changes: List<FileChange>,
): List<KotlinFile> {
    val unchangedPaths =
        changes
            .filter { it.status == ChangeStatus.UNCHANGED }
            .map { it.path }
            .toSet()

    return cachedProject.files.filter { cachedFile ->
        val absolutePath = resolveAbsolutePath(cachedProject.directories, cachedFile.filePath.path)
        absolutePath in unchangedPaths
    }
}
