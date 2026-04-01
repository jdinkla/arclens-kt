package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.exampleProject

class ImportsTest :
    StringSpec({
        "should return the imports for every package" {
            val result = PackageImports.allImports(exampleProject)
            result shouldHaveSize 1
            result[0].packageName shouldBe PackageName("net.dinkla.arclens")
            result[0].imports shouldContainExactly
                setOf(PackageName("java.lang.Boolean"), PackageName("net.dinkla.arclens"))
        }

        "should return the imports for every package but not external once" {
            val result = PackageImports.filteredImports(exampleProject)
            result shouldHaveSize 1
            result[0].packageName shouldBe PackageName("net.dinkla.arclens")
            result[0].imports shouldContainExactly
                setOf(PackageName("net.dinkla.arclens"))
        }
    })
