package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.UnusedImportsReport

class UnusedImportsCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = UnusedImportsCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val report = Json.decodeFromString<UnusedImportsReport>(result.output)
            report.totalUnusedImports shouldBe report.unusedImports.size
        }
    })
