package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.kotlinlang.PackageName

class CouplingTest :
    StringSpec({

        "should calculate correct afferent coupling (Ca)" {
            // Arrange
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"), PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.c"), setOf()),
                )

            // Act
            val metrics = coupling(packages)

            // Assert
            val pkgA = metrics.find { it.packageName.name == "pkg.a" }!!
            val pkgB = metrics.find { it.packageName.name == "pkg.b" }!!
            val pkgC = metrics.find { it.packageName.name == "pkg.c" }!!

            pkgA.coupling.afferentCoupling shouldBe 0 // No packages depend on A
            pkgB.coupling.afferentCoupling shouldBe 1 // A depends on B
            pkgC.coupling.afferentCoupling shouldBe 2 // A and B depend on C
        }

        "should calculate correct efferent coupling (Ce)" {
            // Arrange
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"), PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.c"), setOf()),
                )

            // Act
            val metrics = coupling(packages)

            // Assert
            val pkgA = metrics.find { it.packageName.name == "pkg.a" }!!
            val pkgB = metrics.find { it.packageName.name == "pkg.b" }!!
            val pkgC = metrics.find { it.packageName.name == "pkg.c" }!!

            pkgA.coupling.efferentCoupling shouldBe 2 // A depends on B and C
            pkgB.coupling.efferentCoupling shouldBe 1 // B depends on C
            pkgC.coupling.efferentCoupling shouldBe 0 // C depends on nothing
        }

        "should calculate correct instability (I)" {
            // Arrange
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.b"), PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.b"), setOf(PackageName("pkg.c"))),
                    PackageImports(PackageName("pkg.c"), setOf()),
                )

            // Act
            val metrics = coupling(packages)

            // Assert
            val pkgA = metrics.find { it.packageName.name == "pkg.a" }!!
            val pkgB = metrics.find { it.packageName.name == "pkg.b" }!!
            val pkgC = metrics.find { it.packageName.name == "pkg.c" }!!

            // A: Ce=2, Ca=0, I=2/(2+0)=1.0
            pkgA.coupling.instability shouldBe 1.0

            // B: Ce=1, Ca=1, I=1/(1+1)=0.5
            pkgB.coupling.instability shouldBe 0.5

            // C: Ce=0, Ca=2, I=0/(0+2)=0.0
            pkgC.coupling.instability shouldBe 0.0
        }

        "should handle empty imports correctly" {
            // Arrange
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf()),
                    PackageImports(PackageName("pkg.b"), setOf()),
                )

            // Act
            val metrics = coupling(packages)

            // Assert
            metrics.size shouldBe 2
            metrics.forEach {
                it.coupling.efferentCoupling shouldBe 0
                it.coupling.afferentCoupling shouldBe 0
                it.coupling.instability shouldBe 0.0
            }
        }

        "should handle self-references correctly" {
            // Arrange - package referring to itself shouldn't count in coupling
            val packages =
                listOf(
                    PackageImports(PackageName("pkg.a"), setOf(PackageName("pkg.a"))),
                )

            // Act
            val metrics = coupling(packages)

            // Assert
            metrics.size shouldBe 1
            metrics[0].coupling.efferentCoupling shouldBe 1 // Shows self-reference in imports list
            metrics[0].coupling.afferentCoupling shouldBe 0 // But not in afferent coupling from others
            metrics[0].coupling.instability shouldBe 1.0
        }
    })
