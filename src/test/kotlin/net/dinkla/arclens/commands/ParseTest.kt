package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import kotlinx.serialization.json.Json
import net.dinkla.arclens.domain.kotlinlang.Project
import kotlin.io.path.createTempFile

class ParseTest :
    StringSpec({

        "should return an error if no arguments are given" {
            val result = Parse().test("")
            result.statusCode shouldNotBe 0
        }

        "should return an error if first argument does not exist" {
            val result = Parse().test("NOT_EXISTENT_723732")
            result.statusCode shouldNotBe 0
        }

        "should return an error if first argument is not a directory" {
            val result = Parse().test("build.gradle.kts")
            result.statusCode shouldNotBe 0
        }

        "should parse a source directory and generate a model as output" {
            val result = Parse().test("src/test/resources")
            result.statusCode shouldBe 0
            val project = Json.decodeFromString<Project>(result.output)
            project.directory shouldEndWith "src/test/resources"
            project.packages() shouldHaveSize 0
        }

        "should parse a source directory and generate a model file" {
            val tmp = createTempFile("arclens", ".json")
            val result = Parse().test("src/test/resources ${tmp.toAbsolutePath()}")
            result.statusCode shouldBe 0
            result.output shouldNotContain "src/test/resources"
            val project = Json.decodeFromString<Project>(tmp.toFile().readText())
            project.directory shouldEndWith "src/test/resources"
            project.packages() shouldHaveSize 0
        }
    })
