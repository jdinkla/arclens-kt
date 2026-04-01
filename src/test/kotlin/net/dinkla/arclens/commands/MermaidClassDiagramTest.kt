package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class MermaidClassDiagramTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = MermaidClassDiagram().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            result.output shouldContain "class HelloWorld"
        }
    })
