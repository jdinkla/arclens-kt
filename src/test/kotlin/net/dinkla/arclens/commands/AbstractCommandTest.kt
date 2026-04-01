package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

private class Command : AbstractCommand("some help") {
    override fun run() {
        echo("test")
    }
}

private class CommandWithLoadProject : AbstractCommand("test command") {
    override fun run() {
        val project = loadProject()
        echo("loaded: ${project.directory}")
    }
}

class AbstractCommandTest :
    StringSpec({
        "should return an error if no arguments are given" {
            val result = Command().test("")
            result.statusCode shouldNotBe 0
        }

        "should return an error if first argument does not exist" {
            val result = Command().test("NOT_EXISTENT_723732")
            result.statusCode shouldNotBe 0
        }

        "should return a result for a valid model file" {
            val result = Command().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            result.output shouldStartWith "test"
        }

        "should return an error for invalid JSON format" {
            val tmp =
                createTempFile("arclens", ".json").apply {
                    writeText("not json at all")
                }
            val result = CommandWithLoadProject().test(tmp.toAbsolutePath().toString())
            result.statusCode shouldNotBe 0
            result.output shouldContain "ERROR: Failed to parse JSON file"
            result.output shouldContain tmp.toAbsolutePath().toString()
        }

        "should return an error for JSON with unknown keys" {
            val tmp =
                createTempFile("arclens", ".json").apply {
                    writeText("""{"invalid": "key", "unknown": "field"}""")
                }
            val result = CommandWithLoadProject().test(tmp.toAbsolutePath().toString())
            result.statusCode shouldNotBe 0
            result.output shouldContain "ERROR: Failed to parse JSON file"
            result.output shouldContain tmp.toAbsolutePath().toString()
        }

        "should return an error for malformed JSON" {
            val tmp =
                createTempFile("arclens", ".json").apply {
                    writeText("""{"directory": "test", "files": [""")
                }
            val result = CommandWithLoadProject().test(tmp.toAbsolutePath().toString())
            result.statusCode shouldNotBe 0
            result.output shouldContain "ERROR: Failed to parse JSON file"
        }

        "should successfully load a valid project file" {
            val result = CommandWithLoadProject().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            result.output shouldContain "loaded:"
        }
    })
