package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.examplePackage

class PackageTest :
    StringSpec({
        "should aggregate declarations" {
            val numberOfDeclarations = examplePackage.files.sumOf { it.declarations.size }
            examplePackage.declarations.size shouldBe numberOfDeclarations
        }

        "should aggregate distinct imports" {
            examplePackage.imports().size shouldBe 3
        }
    })
