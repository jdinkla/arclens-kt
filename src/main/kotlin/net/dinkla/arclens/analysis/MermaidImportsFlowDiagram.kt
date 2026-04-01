package net.dinkla.arclens.analysis

import net.dinkla.arclens.domain.kotlinlang.Package
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project

fun mermaidImportsFlowDiagram(
    project: Project,
    excludeOtherLibraries: Boolean,
): String {
    val packagesList = project.packages()
    val packagesTree = toTree(packagesList)
    return generateDiagram(packagesTree, packagesList, excludeOtherLibraries)
}

internal class TreeNode<T>(
    val value: T,
    val children: MutableList<TreeNode<T>> = mutableListOf(),
) {
    fun addChild(node: TreeNode<T>) {
        children.add(node)
    }

    override fun toString(): String = "TreeNode(value=$value, children=$children)"
}

internal fun toTree(packages: List<Package>): TreeNode<Package> {
    val root = TreeNode(Package(PackageName(""), emptyList()))
    packages.forEach { pkg ->
        val parts = pkg.packageName.name.split(".")
        var current = root
        for (part in parts) {
            val found = current.children.find { it.value.packageName.name == part }
            if (found != null) {
                current = found
            } else {
                val newNode = TreeNode(Package(PackageName(part), emptyList()))
                current.addChild(newNode)
                current = newNode
            }
        }
        current.addChild(TreeNode(pkg))
    }
    return root
}

private fun generateDiagram(
    tree: TreeNode<Package>,
    packages: List<Package>,
    excludeOtherLibraries: Boolean,
) = buildString {
    appendLine("flowchart LR")
    generateDiagramRecursive(tree, this)
    val imports = importDependencies(packages, excludeOtherLibraries)
    val distinctImports =
        imports
            .map {
                "  ${it.first} --> ${it.second}"
            }.sorted()
            .distinct()
    distinctImports.forEach { appendLine(it) }
}

private fun importDependencies(
    packages: List<Package>,
    excludeOtherLibraries: Boolean,
): List<Pair<String, String>> {
    val packageNames = packages.map { it.packageName.name }.sorted().distinct()
    return packages.flatMap { pkg ->
        pkg.files.flatMap { file ->
            file.imports
                .filter {
                    if (excludeOtherLibraries) {
                        packageNames.contains(it.name.packageName.name)
                    } else {
                        true
                    }
                }.map { imp ->
                    Pair(file.packageName.name, imp.name.packageName.name)
                }
        }
    }
}

private fun generateDiagramRecursive(
    tree: TreeNode<Package>,
    stringBuilder: StringBuilder,
    indent: Int = 0,
) {
    if (tree.value.packageName.name
            .isEmpty()
    ) {
        tree.children.forEach {
            generateDiagramRecursive(it, stringBuilder, indent)
        }
        return
    }
    val spaces = " ".repeat(indent * 2)
    stringBuilder.appendLine("${spaces}subgraph ${tree.value.packageName.name}")
    tree.children.forEach { child ->
        if (child.children.isEmpty()) {
            val id = child.value.packageName.name
            stringBuilder.appendLine("$spaces  $id")
        } else {
            generateDiagramRecursive(child, stringBuilder, indent + 1)
        }
    }
    stringBuilder.appendLine("${spaces}end")
}
