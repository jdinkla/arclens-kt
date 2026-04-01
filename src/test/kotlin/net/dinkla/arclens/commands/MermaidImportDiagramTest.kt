package net.dinkla.arclens.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class MermaidImportDiagramTest :
    StringSpec({
        "should return a result for a valid model file" {
            val result = MermaidImportDiagram().test("src/test/resources/model.json")
            result.statusCode shouldBe 0
            result.output shouldContain "net.dinkla.arclens.analysis --> net.dinkla.arclens.domain"
        }
    })
