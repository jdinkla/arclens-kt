package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionParameter
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.ImportedElement
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.domain.kotlinlang.TypeAlias

class UnusedImportsTest :
    StringSpec({
        "should detect unused imports" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("java.lang.Boolean"))),
                    declarations = listOf(FunctionSignature("foo")),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 1
            report.unusedImports[0].importName shouldBe "java.lang.Boolean"
        }

        "should not flag imports used in function return types" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("kotlin.String"))),
                    declarations = listOf(FunctionSignature("foo", returnType = Type("String"))),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should not flag imports used in function parameter types" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("kotlin.Int"))),
                    declarations =
                        listOf(
                            FunctionSignature(
                                "foo",
                                parameters = listOf(FunctionParameter("x", Type("Int"))),
                            ),
                        ),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should not flag imports used in class supertypes" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("pkg.MyInterface"))),
                    declarations = listOf(ClassSignature("Impl", superTypes = listOf("MyInterface"))),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should not flag imports used in property types" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("kotlin.String"))),
                    declarations = listOf(Property("myProp", Type("String"))),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should not flag imports used in generic types" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("kotlin.String"))),
                    declarations = listOf(Property("myList", Type("List<String>"))),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should not flag imports used in type aliases" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("kotlin.String"))),
                    declarations = listOf(TypeAlias("MyString", Type("String"))),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should not flag imports used in extension function receivers" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("pkg.HelloWorld"))),
                    declarations = listOf(FunctionSignature("greet", extensionOf = "HelloWorld")),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }

        "should report correct count across multiple files" {
            // Given
            val file1 =
                KotlinFile(
                    filePath = FilePath("file1.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("java.util.List"))),
                    declarations = listOf(FunctionSignature("foo")),
                )
            val file2 =
                KotlinFile(
                    filePath = FilePath("file2.kt"),
                    packageName = PackageName("pkg"),
                    imports = listOf(Import(ImportedElement("java.util.Map"))),
                    declarations = listOf(FunctionSignature("bar")),
                )
            val project = Project("dir", listOf(file1, file2))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 2
        }

        "should handle file with no imports" {
            // Given
            val file =
                KotlinFile(
                    filePath = FilePath("file.kt"),
                    packageName = PackageName("pkg"),
                    declarations = listOf(FunctionSignature("foo")),
                )
            val project = Project("dir", listOf(file))
            // When
            val report = UnusedImportsReport.from(project)
            // Then
            report.totalUnusedImports shouldBe 0
        }
    })
