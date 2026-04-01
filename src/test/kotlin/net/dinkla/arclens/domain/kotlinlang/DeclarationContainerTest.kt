package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.c1
import net.dinkla.arclens.f1
import net.dinkla.arclens.f2
import net.dinkla.arclens.p1
import net.dinkla.arclens.ta1

class DeclarationContainerTest :
    StringSpec({
        "should return functions" {
            example.functions shouldBe listOf(f1, f2)
        }

        "should return properties" {
            example.properties shouldBe listOf(p1)
        }

        "should return classes" {
            example.classes shouldBe listOf(c1)
        }

        "should return type aliases" {
            example.typeAliases shouldBe listOf(ta1)
        }

        "should return size" {
            example.size shouldBe 5
        }
    })

private val example =
    object : DeclarationContainer {
        override val declarations: List<Declaration> = listOf(f1, f2, c1, ta1, p1)
    }
