package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.statistics.Coupling

class MermaidCouplingDiagramTest :
    StringSpec({

        "should generate a valid Mermaid diagram with packages and metrics" {
            // Arrange
            val reportItems =
                listOf(
                    PackageCouplingItem(
                        packageName = PackageName("pkg.a"),
                        imports = setOf(PackageName("pkg.b"), PackageName("pkg.c")),
                        coupling = Coupling(0, 2, 1.0),
                    ),
                    PackageCouplingItem(
                        packageName = PackageName("pkg.b"),
                        imports = setOf(PackageName("pkg.c")),
                        coupling = Coupling(1, 1, 0.5),
                    ),
                    PackageCouplingItem(
                        packageName = PackageName("pkg.c"),
                        imports = setOf(),
                        coupling = Coupling(2, 0, 0.0),
                    ),
                )

            val diagram = MermaidCouplingDiagram(reportItems)

            // Act
            val result = diagram.generate()

            // Assert
            result shouldStartWith "flowchart TD"

            // Should include style definitions
            result shouldContain "classDef stable"
            result shouldContain "classDef balanced"
            result shouldContain "classDef unstable"

            // Should include all packages with their metrics
            result shouldContain "pkg_pkg_a"
            result shouldContain "pkg_pkg_b"
            result shouldContain "pkg_pkg_c"

            // Should include coupling metrics
            result shouldContain "I=1.00" // pkg.a
            result shouldContain "I=0.50" // pkg.b
            result shouldContain "I=0.00" // pkg.c

            // Should include dependency relationships
            result shouldContain "pkg_pkg_a --> pkg_pkg_b"
            result shouldContain "pkg_pkg_a --> pkg_pkg_c"
            result shouldContain "pkg_pkg_b --> pkg_pkg_c"

            // Should have proper styling based on instability
            result shouldContain "pkg_pkg_a:::unstable" // I=1.0 > 0.7
            result shouldContain "pkg_pkg_b:::balanced" // 0.3 < I=0.5 < 0.7
            result shouldContain "pkg_pkg_c:::stable" // I=0.0 < 0.3

            // Should include a legend
            result shouldContain "subgraph Legend"
        }

        "should handle empty dependency list" {
            // Arrange
            val reportItems =
                listOf(
                    PackageCouplingItem(
                        packageName = PackageName("pkg.a"),
                        imports = setOf(),
                        coupling = Coupling(0, 0, 0.0),
                    ),
                    PackageCouplingItem(
                        packageName = PackageName("pkg.b"),
                        imports = setOf(),
                        coupling = Coupling(0, 0, 0.0),
                    ),
                )

            val diagram = MermaidCouplingDiagram(reportItems)

            // Act
            val result = diagram.generate()

            // Assert
            result shouldStartWith "flowchart TD"

            // Should include all packages with their metrics
            result shouldContain "pkg_pkg_a"
            result shouldContain "pkg_pkg_b"

            // Should include coupling metrics with zeros
            result shouldContain "I=0.00"
            result shouldContain "Ca=0"
            result shouldContain "Ce=0"

            // Should not have any dependencies
            (result.split("\n").any { it.contains("-->") }) shouldBe false
        }
    })
