package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.domain.kotlinlang.Package

class PackagesCommandTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = PackagesCommand().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            val packages = Json.decodeFromString<List<Package>>(result.output)
            packages shouldHaveAtLeastSize 1
        }
    })
