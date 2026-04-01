package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PropertyModifierTest :
    StringSpec({
        "create should create the correct modifier" {
            PropertyModifier.create(hasConstModifier = false, isMutable = false) shouldBe PropertyModifier.VAL
            PropertyModifier.create(hasConstModifier = false, isMutable = true) shouldBe PropertyModifier.VAR
            PropertyModifier.create(hasConstModifier = true, isMutable = false) shouldBe PropertyModifier.CONST_VAL
        }
    })
