package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.DeepInheritanceReport

class DeepInheritanceCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = DeepInheritanceCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<DeepInheritanceReport>(result.output)
            report.threshold shouldBe 3
        }

        "should accept a custom threshold" {
            val result = DeepInheritanceCommand().test("-t 1 src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<DeepInheritanceReport>(result.output)
            report.threshold shouldBe 1
        }
    })
