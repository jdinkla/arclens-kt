package net.dinkla.arclens.domain.statistics

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CouplingTest :
    StringSpec({
        "should calculate instability" {
            Coupling(2, 3).instability shouldBe (3.0 / (2 + 3))
        }
    })
