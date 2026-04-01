package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.kotlinlang.PackageName

class CircularDependenciesTest :
    StringSpec({

        "should detect no cycles in acyclic graph" {
            // Given
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.c"), setOf()),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe false
            report.totalCycles shouldBe 0
            report.cycles shouldBe emptyList()
            report.packagesInCycles shouldBe emptySet()
        }

        "should detect simple two-package cycle" {
            // Given: A -> B -> A
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.a"))),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe true
            report.totalCycles shouldBe 1
            report.cycles.size shouldBe 1
            report.cycles[0].packages shouldContainExactlyInAnyOrder
                listOf(
                    PackageName("pkg.a"),
                    PackageName("pkg.b"),
                )
            report.packagesInCycles shouldContainExactlyInAnyOrder
                listOf(
                    PackageName("pkg.a"),
                    PackageName("pkg.b"),
                )
        }

        "should detect three-package cycle" {
            // Given: A -> B -> C -> A
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.c"), setOf(PackageName("pkg.a"))),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe true
            report.totalCycles shouldBe 1
            report.cycles[0].size shouldBe 3
            report.cycles[0].packages shouldContainExactlyInAnyOrder
                listOf(
                    PackageName("pkg.a"),
                    PackageName("pkg.b"),
                    PackageName("pkg.c"),
                )
        }

        "should detect multiple independent cycles" {
            // Given: A <-> B and C <-> D (two separate cycles)
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.a"))),
                    PackageImports(PackageName("pkg.c"), setOf(PackageName("pkg.d"))),
                    PackageImports(PackageName("pkg.d"), setOf(PackageName("pkg.c"))),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe true
            report.totalCycles shouldBe 2
            report.cycles.size shouldBe 2
            report.packagesInCycles.size shouldBe 4
        }

        "should detect cycle mixed with non-cycle packages" {
            // Given: A -> B -> A (cycle), C -> A (no cycle)
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.a"))),
                    PackageImports(PackageName("pkg.c"), setOf(PackageName("pkg.a"))),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe true
            report.totalCycles shouldBe 1
            report.cycles[0].packages shouldContainExactlyInAnyOrder
                listOf(
                    PackageName("pkg.a"),
                    PackageName("pkg.b"),
                )
            report.packagesInCycles shouldContainExactlyInAnyOrder
                listOf(
                    PackageName("pkg.a"),
                    PackageName("pkg.b"),
                )
        }

        "should not report self-references as cycles" {
            // Given: A package that imports itself
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.a"))),
                    PackageImports(PackageName("pkg.b"), setOf()),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe false
            report.totalCycles shouldBe 0
        }

        "should handle empty package list" {
            // Given
            val packages = emptyList<PackageImports>()

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe false
            report.totalCycles shouldBe 0
            report.cycles shouldBe emptyList()
        }

        "should handle single package with no imports" {
            // Given
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf()),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe false
            report.totalCycles shouldBe 0
        }

        "should ignore imports to external packages not in the graph" {
            // Given: A imports external package not in the list
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("external.lib"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.a"))),
                )

            // When
            val report = findCircularDependencies(packages)

            // Then
            report.hasCycles shouldBe false
            report.totalCycles shouldBe 0
        }
    })
