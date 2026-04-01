package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.AnalyzedPackage

class PackageStatisticsCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = PackageStatisticsCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val stats = Json.decodeFromString<List<AnalyzedPackage>>(result.output)
            stats shouldHaveAtLeastSize 1
        }
    })
