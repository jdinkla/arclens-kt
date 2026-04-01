package net.dinkla.arclens.extract

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName

class ParseReportingTest :
    StringSpec({

        val successResult =
            Result.success(
                KotlinFile(FilePath("/test/A.kt"), PackageName("test")),
            )
        val failureResult = Result.failure<KotlinFile>(Error("parse error"))

        "formatParseSummary should return null for empty results" {
            formatParseSummary(emptyList()) shouldBe null
        }

        "formatParseSummary should format all successes" {
            // Given
            val results = listOf(successResult, successResult)

            // When
            val summary = formatParseSummary(results)

            // Then
            summary shouldBe "2 ok"
        }

        "formatParseSummary should format mixed results" {
            // Given
            val results = listOf(successResult, successResult, failureResult)

            // When
            val summary = formatParseSummary(results)

            // Then
            summary shouldContain "2 ok"
            summary shouldContain "1 exceptions"
        }

        "formatParseSummary should format all failures" {
            // Given
            val results = listOf(failureResult, failureResult)

            // When
            val summary = formatParseSummary(results)

            // Then
            summary shouldBe "2 exceptions"
        }

        "formatParseErrors should return null when no failures" {
            // Given
            val results = listOf(successResult, successResult)

            // When / Then
            formatParseErrors(results) shouldBe null
        }

        "formatParseErrors should return null for empty results" {
            formatParseErrors(emptyList()) shouldBe null
        }

        "formatParseErrors should format failure details" {
            // Given
            val results = listOf(failureResult)

            // When
            val errors = formatParseErrors(results)!!

            // Then
            errors shouldContain "ERROR: The following exceptions occurred:"
            errors shouldContain "1. parse error"
            errors.shouldNotBeBlank()
        }

        "formatParseErrors should number multiple failures" {
            // Given
            val results =
                listOf(
                    Result.failure<KotlinFile>(Error("error one")),
                    Result.failure<KotlinFile>(Error("error two")),
                )

            // When
            val errors = formatParseErrors(results)!!

            // Then
            errors shouldContain "1. error one"
            errors shouldContain "2. error two"
        }

        "formatIncrementalStatus should format all parts" {
            // Given
            val results = listOf(successResult, failureResult)

            // When
            val status = formatIncrementalStatus(results, unchangedCount = 5, deletedCount = 2)

            // Then
            status shouldBe "1 parsed, 1 exceptions, 5 unchanged, 2 deleted"
        }

        "formatIncrementalStatus should omit zero counts" {
            // Given
            val results = listOf(successResult)

            // When
            val status = formatIncrementalStatus(results, unchangedCount = 3, deletedCount = 0)

            // Then
            status shouldBe "1 parsed, 3 unchanged"
        }

        "formatIncrementalStatus should handle empty parsed list" {
            val status = formatIncrementalStatus(emptyList(), unchangedCount = 10, deletedCount = 0)
            status shouldBe "10 unchanged"
        }
    })
