package net.dinkla.arclens.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.arclens.domain.kotlinlang.ClassParameter
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.kotlinFile

class PrettyPrintTest :
    StringSpec({
        "prettyPrint should return a string representation of default class" {
            ClassSignature(
                "C",
                listOf(ClassParameter("p", Type("Int"))),
                declarations = listOf(FunctionSignature("toString", Type("String"), listOf())),
                elementType = ClassSignature.Type.CLASS,
            ).prettyPrint() shouldBe
                """
                class C(p: Int) {
                    fun toString(): String
                }
                """.trimIndent()
        }

        "prettyPrint should show the toplevel" {
            val lines = kotlinFile.prettyPrint().lines()
            lines.size shouldBe 14
            val text = lines.joinToString("\n")
            text shouldContain "import"
            text shouldContain "package"
            text shouldContain "typealias"
            text shouldContain "val"
            text shouldContain "fun"
            text shouldContain "class"
        }
    })
