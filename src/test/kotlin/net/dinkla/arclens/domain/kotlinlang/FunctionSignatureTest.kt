package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.utilities.prettyPrint

class FunctionSignatureTest :
    StringSpec({
        "prettyPrint should return a string representation of a function fun f(x: Int): Int" {
            FunctionSignature(
                "f",
                Type("Int"),
                listOf(FunctionParameter("x", Type("Int"))),
            ).prettyPrint() shouldBe "fun f(x: Int): Int"
        }

        "prettyPrint should return a string representation of a function private fun f(x: Int): Int" {
            FunctionSignature(
                "f",
                Type("Int"),
                listOf(FunctionParameter("x", Type("Int"))),
                visibilityModifier = VisibilityModifier.PRIVATE,
            ).prettyPrint() shouldBe "private fun f(x: Int): Int"
        }

        "prettyPrint should return a string representation of a function fun Ext.f(x: Int): Int" {
            FunctionSignature(
                "f",
                Type("Int"),
                listOf(FunctionParameter("x", Type("Int"))),
                extensionOf = "Ext",
            ).prettyPrint() shouldBe "fun Ext.f(x: Int): Int"
        }

        "prettyPrint should return a string representation of a function override fun f(x: Int): Int" {
            FunctionSignature(
                "f",
                Type("Int"),
                listOf(FunctionParameter("x", Type("Int"))),
                visibilityModifier = VisibilityModifier.PROTECTED,
                memberModifier = MemberModifier.OVERRIDE,
            ).prettyPrint() shouldBe "protected override fun f(x: Int): Int"
        }

        "prettyPrint should return a string representation of a function with generic arguments" {
            FunctionSignature(
                "reverse",
                Type("List<Int>"),
                listOf(FunctionParameter("xs", Type("List<Int>"))),
            ).prettyPrint() shouldBe "fun reverse(xs: List<Int>): List<Int>"
        }
    })
