package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.EXAMPLE_MODEL
import net.dinkla.arclens.analysis.FileStatistics

class FileStatisticsCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = FileStatisticsCommand().test(EXAMPLE_MODEL)
            result.statusCode shouldBe 0
            val files = Json.decodeFromString<List<FileStatistics>>(result.output)
            files shouldHaveAtLeastSize 10
        }

        "should return a result for a valid model file with include-all-libraries flag" {
            val result = FileStatisticsCommand().test("$EXAMPLE_MODEL --include-all-libraries")
            result.statusCode shouldBe 0
            val stats = Json.decodeFromString<List<FileStatistics>>(result.output)
            stats shouldHaveAtLeastSize 1
        }

        "should return a result for a valid model file with include-private-declarations flag" {
            val result = FileStatisticsCommand().test("$EXAMPLE_MODEL --include-private-declarations")
            result.statusCode shouldBe 0
            val stats = Json.decodeFromString<List<FileStatistics>>(result.output)
            stats shouldHaveAtLeastSize 1
        }

        "should return a result for a valid model file with both flags" {
            val result =
                FileStatisticsCommand().test(
                    "$EXAMPLE_MODEL --include-all-libraries --include-private-declarations",
                )
            result.statusCode shouldBe 0
            val stats = Json.decodeFromString<List<FileStatistics>>(result.output)
            stats shouldHaveAtLeastSize 1
        }
    })
