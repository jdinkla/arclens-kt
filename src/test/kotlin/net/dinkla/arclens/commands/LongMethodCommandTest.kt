package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.LongMethodReport

class LongMethodCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = LongMethodCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<LongMethodReport>(result.output)
            report.threshold shouldBe 60
        }

        "should accept a custom threshold" {
            val result = LongMethodCommand().test("-t 5 src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<LongMethodReport>(result.output)
            report.threshold shouldBe 5
        }
    })
