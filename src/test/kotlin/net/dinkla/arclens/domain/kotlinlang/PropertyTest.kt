package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.utilities.prettyPrint

class PropertyTest :
    StringSpec({
        "prettyPrint should handle internal val" {
            val property =
                property.copy(
                    dataType = Type("String"),
                    modifier = PropertyModifier.VAL,
                    visibilityModifier = VisibilityModifier.INTERNAL,
                )
            property.prettyPrint() shouldBe "internal val name : String"
        }

        "prettyPrint should handle val" {
            val property =
                property.copy(
                    dataType = Type("String"),
                    modifier = PropertyModifier.CONST_VAL,
                )
            property.prettyPrint() shouldBe "const val name : String"
        }

        "prettyPrint should handle implicit type" {
            val property =
                property.copy(
                    modifier = PropertyModifier.VAR,
                )
            property.prettyPrint() shouldBe "var name"
        }

        "prettyPrint should handle override" {
            val property =
                property.copy(
                    modifier = PropertyModifier.VAR,
                    memberModifier = listOf(MemberModifier.OVERRIDE),
                )
            property.prettyPrint() shouldBe "override var name"
        }

        "prettyPrint should handle override lateinit" {
            val property =
                property.copy(
                    modifier = PropertyModifier.VAR,
                    memberModifier = listOf(MemberModifier.LATE_INIT, MemberModifier.OVERRIDE),
                )
            property.prettyPrint() shouldBe "override lateinit var name"
        }
    })

private val property =
    Property(
        "name",
    )
