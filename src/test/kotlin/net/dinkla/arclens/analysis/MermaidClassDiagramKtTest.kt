package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.kotlinlang.ClassParameter
import net.dinkla.arclens.domain.kotlinlang.FunctionParameter
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.MemberModifier
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.PropertyModifier
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier

class MermaidClassDiagramKtTest :
    StringSpec({
        "a function signature in mermaid format" {
            val functionSignature =
                FunctionSignature(
                    name = "foo",
                    returnType = Type("List<Int>"),
                    parameters =
                        listOf(
                            FunctionParameter("a", Type("Int")),
                            FunctionParameter("b", Type("String")),
                        ),
                    visibilityModifier = VisibilityModifier.PUBLIC,
                    memberModifier = MemberModifier.OVERRIDE,
                )
            functionSignature.mermaid() shouldBe Pair("+", "foo(a: Int, b: String): List‹Int› «override»")
        }

        "a property in mermaid format" {
            val property =
                Property(
                    name = "bar",
                    dataType = Type("String"),
                    visibilityModifier = VisibilityModifier.PRIVATE,
                )
            property.mermaid() shouldBe Pair("-", "bar: String «val»")
        }

        "a parameter in mermaid format" {
            val parameter =
                ClassParameter(
                    name = "a",
                    type = Type("List<Int>"),
                    propertyModifier = PropertyModifier.VAL,
                )
            parameter.mermaid() shouldBe Pair("+", "a: List‹Int› «val»")
        }
    })
