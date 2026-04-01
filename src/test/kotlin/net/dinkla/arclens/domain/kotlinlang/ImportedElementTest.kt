package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ImportedElementTest :
    StringSpec({
        "packageName should return the name of the package without the file" {
            ImportedElement("kotlin.math.max").packageName shouldBe PackageName("kotlin.math")
        }

        "packageName should return the name of the package without the *" {
            ImportedElement("kotlin.math.*").packageName shouldBe PackageName("kotlin.math")
        }
    })
