package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ClassModifierTest :
    StringSpec({
        "ClassModifier.text values should equal lowercase enum names" {
            val texts = ClassModifier.entries.map { it.text }
            val expected = ClassModifier.entries.map { it.name.lowercase() }
            texts shouldBe expected
        }
    })
