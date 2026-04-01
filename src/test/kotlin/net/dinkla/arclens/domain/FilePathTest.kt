package net.dinkla.arclens.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FilePathTest :
    StringSpec({
        "basename should return the name of the file given a full path" {
            FilePath("src/test/resources/example/HelloWorld.kt").fileName shouldBe "HelloWorld.kt"
        }

        "basename should return the name of the file on Windows" {
            FilePath("C:\\src\\test\\resources\\example\\HelloWorld.kt").fileName shouldBe "HelloWorld.kt"
        }

        "fileNameWithoutDirectory should return the file name without the directory" {
            FilePath("a/b/cde.fg").withoutDirectory("a") shouldBe "b/cde.fg"
        }

        "fileNameWithoutDirectory should return the file name without the directory 2" {
            FilePath("a/b/c/def.x").withoutDirectory("a/b/c") shouldBe "def.x"
        }
    })
