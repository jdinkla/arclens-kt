package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.utilities.prettyPrint

class ClassSignatureTest :
    StringSpec({
        "aliases should return the type aliases" {
            val typeAlias = TypeAlias("T", Type("Int"))
            val classSignature =
                ClassSignature(
                    "C",
                    listOf(ClassParameter("p", Type("Int"))),
                    declarations = listOf(typeAlias),
                    elementType = ClassSignature.Type.CLASS,
                )
            classSignature.typeAliases shouldBe listOf(typeAlias)
        }

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

        "prettyPrint should return a string representation of an internal data class" {
            ClassSignature(
                "C",
                listOf(ClassParameter("p", Type("Int"))),
                declarations = listOf(FunctionSignature("toString", Type("String"), listOf())),
                visibilityModifier = VisibilityModifier.INTERNAL,
                elementType = ClassSignature.Type.CLASS,
                classModifier = ClassModifier.DATA,
            ).prettyPrint() shouldBe
                """
                internal data class C(p: Int) {
                    fun toString(): String
                }
                """.trimIndent()
        }

        "prettyPrint should return a string representation of class with an inner class" {
            val def =
                ClassSignature(
                    "C",
                    listOf(ClassParameter("p", Type("Int"))),
                    elementType = ClassSignature.Type.CLASS,
                    inheritanceModifier = InheritanceModifier.OPEN,
                    declarations =
                        listOf(
                            FunctionSignature("toString", Type("String"), listOf()),
                            ClassSignature(
                                "D",
                                listOf(ClassParameter("p", Type("Int"))),
                                elementType = ClassSignature.Type.CLASS,
                                classModifier = ClassModifier.INNER,
                            ),
                        ),
                )
            val lines = def.prettyPrint().lines()
            lines[0] shouldBe "open class C(p: Int) {"
            lines[1] shouldBe "    fun toString(): String"
            lines[2] shouldBe "    inner class D(p: Int) {}"
            lines[3] shouldBe "}"
        }
    })
