package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.ImportedElement
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier
import net.dinkla.arclens.domain.statistics.Coupling
import net.dinkla.arclens.exampleProject
import net.dinkla.arclens.kotlinFile

class FileStatisticsTest :
    StringSpec({
        "should return the imports for every package" {
            val result = FileStatistics.from(exampleProject).sortedBy { it.filePath }
            result shouldHaveSize 3
            result[0].filePath shouldBe kotlinFile.filePath
            result[0].metrics shouldBe FileMetrics(2, 5, 1, 2, 1, 1)
            result[0].coupling shouldBe Coupling(5, 2, 2.0 / (5 + 2))
        }

        "should return the imports when declarations are filtered" {
            val result =
                FileStatistics
                    .from(exampleProject, DeclarationFilter.EXCLUDE_PRIVATE_DECLARATIONS)
                    .sortedBy { it.filePath }
            result shouldHaveSize 3
            result[0].filePath shouldBe kotlinFile.filePath
            result[0].metrics shouldBe FileMetrics(2, 4, 1, 1, 1, 1)
            result[0].coupling shouldBe Coupling(4, 2, 2.0 / (4 + 2))
        }

        "should return the imports when imports are filtered" {
            val result =
                FileStatistics
                    .from(exampleProject, importFilter = ImportFilter.EXCLUDE_IMPORTS_FROM_OTHER_PACKAGES)
                    .sortedBy { it.filePath }
            result shouldHaveSize 3
            result[0].filePath shouldBe kotlinFile.filePath
            result[0].metrics shouldBe FileMetrics(0, 5, 1, 2, 1, 1)
            result[0].coupling shouldBe Coupling(5, 0, 0.0)
        }

        "should return the imports for filtered packages" {
            val filePath = FilePath("some")
            val theImport = Import(ImportedElement("my.pack.Age"))
            val declaration1 = ClassSignature("A", visibilityModifier = VisibilityModifier.INTERNAL)
            val declaration2 = ClassSignature("B", visibilityModifier = VisibilityModifier.PRIVATE)
            val declaration3 = ClassSignature("C")
            val kotlinFile =
                KotlinFile(
                    filePath = filePath,
                    packageName = PackageName("my.pack"),
                    imports =
                        listOf(
                            Import(ImportedElement("java.lang.something")),
                            theImport,
                        ),
                    declarations =
                        listOf(
                            declaration1,
                            declaration2,
                            declaration3,
                        ),
                )
            val expected =
                FileStatistics(
                    filePath = filePath,
                    imports = listOf(theImport),
                    declarations =
                        listOf(
                            GeneralDeclaration("my.pack.A", VisibilityModifier.INTERNAL),
                            GeneralDeclaration("my.pack.C", null),
                        ),
                    metrics = FileMetrics(1, 2, 2, 0, 0, 0),
                    coupling = Coupling(2, 1, 1.0 / (2 + 1)),
                )
            val project = Project("/base", listOf(kotlinFile))
            val result =
                FileStatistics
                    .from(
                        project,
                        DeclarationFilter.EXCLUDE_PRIVATE_DECLARATIONS,
                        ImportFilter.EXCLUDE_IMPORTS_FROM_OTHER_PACKAGES,
                    ).sortedBy {
                        it.filePath
                    }
            result[0].imports shouldBe expected.imports
            result[0].declarations shouldBe expected.declarations
            result[0].coupling shouldBe expected.coupling
            result shouldContainExactly listOf(expected)
        }
    })
