package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.arclens.domain.kotlinlang.Package
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.exampleProject

class MermaidImportsFlowDiagramTest :
    StringSpec({

        val p1 = Package(PackageName("a.b"), emptyList())
        val p2 = Package(PackageName("a.b.c"), emptyList())
        val p3 = Package(PackageName("a.d"), emptyList())

        "mermaidImportsFlowDiagram should create diagram" {
            val content = mermaidImportsFlowDiagram(exampleProject, false)
            content shouldContain "net.dinkla.arclens"
            content shouldContain "net.dinkla.arclens --> java.lang.Boolean"
            content shouldContain "net.dinkla.arclens --> net.dinkla.arclens"
        }

        "mermaidImportsFlowDiagram should exclude libraries" {
            val content = mermaidImportsFlowDiagram(exampleProject, true)
            content shouldContain "net.dinkla.arclens"
            content shouldContain "net.dinkla.arclens --> net.dinkla.arclens"
        }

        "toTree should create a tree for one package" {
            // Given
            val ps = listOf(p1)

            // When
            val tree = toTree(ps)

            // Then
            tree.value.packageName.name shouldBe ""
            tree.children shouldHaveSize 1
            val a = tree.children[0]
            a.value.packageName.name shouldBe "a"
            a.children shouldHaveSize 1
            val b = a.children[0]
            b.value.packageName.name shouldBe "b"
            b.children shouldHaveSize 1
            val ab = b.children[0]
            ab.value shouldBe p1
        }

        "toTree should create a tree for two packages ordered hierarchically" {
            // Given
            val ps = listOf(p1, p2)

            // When
            val tree = toTree(ps)

            // Then
            tree.value.packageName.name shouldBe ""
            tree.children shouldHaveSize 1
            val a = tree.children[0]
            a.value.packageName.name shouldBe "a"
            a.children shouldHaveSize 1
            val b = a.children[0]
            b.value.packageName.name shouldBe "b"
            b.children shouldHaveSize 2
            val ab = b.children.sortedBy { it.value.packageName.name }[0]
            ab.value shouldBe p1
            val c = b.children.sortedBy { it.value.packageName.name }[1]
            c.value.packageName.name shouldBe "c"
            c.children shouldHaveSize 1
            c.children[0].value shouldBe p2
        }

        "toTree should create a tree for two packages on the same level" {
            // Given
            val ps = listOf(p1, p3)

            // When
            val tree = toTree(ps)
            println(tree)

            // Then
            tree.value.packageName.name shouldBe ""
            tree.children shouldHaveSize 1
            val a = tree.children[0]
            a.value.packageName.name shouldBe "a"
            a.children shouldHaveSize 2
            val b = a.children.sortedBy { it.value.packageName.name }[0]
            b.value.packageName.name shouldBe "b"
            b.children shouldHaveSize 1
            b.children[0].value shouldBe p1

            val d = a.children.sortedBy { it.value.packageName.name }[1]
            d.value.packageName.name shouldBe "d"
            d.children shouldHaveSize 1
            d.children[0].value shouldBe p3
        }
    })
