package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath

class LineCountTest :
    StringSpec({
        "KotlinFile lineCount should default to 0" {
            // Given
            val file = KotlinFile(FilePath("test.kt"), PackageName("pkg"))
            // Then
            file.lineCount shouldBe 0
        }

        "KotlinFile lineCount should return value when set" {
            // Given
            val file = KotlinFile(FilePath("test.kt"), PackageName("pkg"), lineCount = 42)
            // Then
            file.lineCount shouldBe 42
        }

        "FunctionSignature lineCount should default to 0" {
            // Given
            val fn = FunctionSignature("foo")
            // Then
            fn.lineCount shouldBe 0
        }

        "FunctionSignature lineCount should return value when set" {
            // Given
            val fn = FunctionSignature("foo", lineCount = 10)
            // Then
            fn.lineCount shouldBe 10
        }
    })
