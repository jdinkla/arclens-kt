package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.kotlinlang.PackageName

@Serializable
data class Cycle(
    val packages: List<PackageName>,
) {
    val size: Int get() = packages.size
}

@Serializable
data class CircularDependenciesReport(
    val cycles: List<Cycle>,
    val hasCycles: Boolean,
    val totalCycles: Int,
    val packagesInCycles: Set<PackageName>,
)

internal fun findCircularDependencies(imports: List<PackageImports>): CircularDependenciesReport {
    val graph = imports.associate { it.packageName to it.imports }
    val allPackages = graph.keys

    val sccs = tarjanSCC(allPackages, graph)
    val cycles =
        sccs
            .filter { it.size > 1 }
            .map { Cycle(it.sortedBy { pkg -> pkg.name }) }
            .sortedBy { it.packages.first().name }

    val packagesInCycles = cycles.flatMap { it.packages }.toSet()

    return CircularDependenciesReport(
        cycles = cycles,
        hasCycles = cycles.isNotEmpty(),
        totalCycles = cycles.size,
        packagesInCycles = packagesInCycles.toSortedSet(compareBy { it.name }),
    )
}

private fun tarjanSCC(
    nodes: Set<PackageName>,
    graph: Map<PackageName, Set<PackageName>>,
): List<List<PackageName>> {
    var index = 0
    val nodeIndex = mutableMapOf<PackageName, Int>()
    val nodeLowLink = mutableMapOf<PackageName, Int>()
    val onStack = mutableSetOf<PackageName>()
    val stack = ArrayDeque<PackageName>()
    val sccs = mutableListOf<List<PackageName>>()

    fun strongConnect(node: PackageName) {
        nodeIndex[node] = index
        nodeLowLink[node] = index
        index++
        stack.addLast(node)
        onStack.add(node)

        val neighbors = graph[node] ?: emptySet()
        for (neighbor in neighbors) {
            if (!nodes.contains(neighbor)) {
                continue
            }
            if (!nodeIndex.containsKey(neighbor)) {
                strongConnect(neighbor)
                nodeLowLink[node] = minOf(nodeLowLink[node]!!, nodeLowLink[neighbor]!!)
            } else if (onStack.contains(neighbor)) {
                nodeLowLink[node] = minOf(nodeLowLink[node]!!, nodeIndex[neighbor]!!)
            }
        }

        if (nodeLowLink[node] == nodeIndex[node]) {
            val scc = mutableListOf<PackageName>()
            do {
                val w = stack.removeLast()
                onStack.remove(w)
                scc.add(w)
            } while (w != node)
            sccs.add(scc)
        }
    }

    for (node in nodes) {
        if (!nodeIndex.containsKey(node)) {
            strongConnect(node)
        }
    }

    return sccs
}
