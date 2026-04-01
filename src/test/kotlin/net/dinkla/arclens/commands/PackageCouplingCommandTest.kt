package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.PackageCouplingItem
import kotlin.math.absoluteValue

class PackageCouplingCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = PackageCouplingCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val reportItems = Json.decodeFromString<List<PackageCouplingItem>>(result.output)
            reportItems shouldHaveAtLeastSize 1
        }

        "all packages should have corresponding metrics" {
            val result = PackageCouplingCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val reportItems = Json.decodeFromString<List<PackageCouplingItem>>(result.output)

            reportItems.forEach { item ->
                // Verify that the coupling metrics exist
                item.coupling shouldNotBe null
            }
        }

        "efferent coupling values should match imports count" {
            val result = PackageCouplingCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val reportItems = Json.decodeFromString<List<PackageCouplingItem>>(result.output)

            reportItems.forEach { item ->
                item.coupling.efferentCoupling shouldBe item.imports.size
            }
        }

        "instability calculation should be correct" {
            val result = PackageCouplingCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val reportItems = Json.decodeFromString<List<PackageCouplingItem>>(result.output)

            reportItems.forEach { item ->
                val totalCoupling = item.coupling.afferentCoupling + item.coupling.efferentCoupling
                if (totalCoupling > 0) {
                    val expectedInstability = item.coupling.efferentCoupling.toDouble() / totalCoupling
                    (item.coupling.instability - expectedInstability).absoluteValue shouldBe 0.0
                } else {
                    item.coupling.instability shouldBe 0.0
                }
            }
        }
    })
