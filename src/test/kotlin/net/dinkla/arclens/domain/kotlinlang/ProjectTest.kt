package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.exampleProject

class ProjectTest :
    StringSpec({
        "should contain the directory" {
            exampleProject.directory shouldBe "/base"
        }

        "should return the packages" {
            exampleProject.packages().size shouldBe 1
        }

        "relativePath() should return the relative path" {
            exampleProject.relativePath(exampleProject[0].filePath.path) shouldBe "ExampleFile.kt"
        }
    })
