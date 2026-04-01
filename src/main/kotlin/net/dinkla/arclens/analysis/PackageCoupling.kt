package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.statistics.Coupling

@Serializable
class PackageCoupling(
    val packageName: PackageName,
    val coupling: Coupling,
)

@Serializable
data class PackageCouplingItem(
    val packageName: PackageName,
    val imports: Set<PackageName>,
    val coupling: Coupling,
)

fun combinedReport(imports: List<PackageImports>): List<PackageCouplingItem> {
    val couplings = coupling(imports)
    return imports.map { import ->
        val matchingCoupling = couplings.find { it.packageName == import.packageName }
        check(matchingCoupling != null) { "No coupling found for package ${import.packageName}" }
        PackageCouplingItem(
            packageName = import.packageName,
            imports = import.imports,
            coupling = matchingCoupling.coupling,
        )
    }
}

internal fun coupling(importsList: List<PackageImports>): List<PackageCoupling> =
    importsList.map { imports ->
        val packageName = imports.packageName
        val efferentCoupling = imports.imports.size
        val afferentCoupling =
            importsList.count { other ->
                other.packageName != packageName && other.imports.contains(packageName)
            }
        PackageCoupling(
            packageName = packageName,
            coupling = Coupling(afferentCoupling, efferentCoupling),
        )
    }
