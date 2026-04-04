package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.LargeClassReport

class LargeClassCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = LargeClassCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<LargeClassReport>(result.output)
            report.threshold shouldBe 10
        }

        "should accept a custom threshold" {
            val result = LargeClassCommand().test("-t 2 src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<LargeClassReport>(result.output)
            report.threshold shouldBe 2
        }
    })
