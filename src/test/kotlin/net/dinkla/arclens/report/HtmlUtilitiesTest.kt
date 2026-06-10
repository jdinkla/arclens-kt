package net.dinkla.arclens.report

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class HtmlUtilitiesTest :
    StringSpec({
        // escapeHtml

        "escapeHtml should not modify plain text" {
            // Given
            val input = "hello world"
            // When
            val result = escapeHtml(input)
            // Then
            result shouldBe "hello world"
        }

        "escapeHtml should escape ampersand" {
            // Given / When / Then
            escapeHtml("a & b") shouldBe "a &amp; b"
        }

        "escapeHtml should escape less-than" {
            // Given / When / Then
            escapeHtml("a < b") shouldBe "a &lt; b"
        }

        "escapeHtml should escape greater-than" {
            // Given / When / Then
            escapeHtml("a > b") shouldBe "a &gt; b"
        }

        "escapeHtml should escape double quote" {
            // Given / When / Then
            escapeHtml("say \"hi\"") shouldBe "say &quot;hi&quot;"
        }

        "escapeHtml should escape single quote" {
            // Given / When / Then
            escapeHtml("it's") shouldBe "it&#39;s"
        }

        "escapeHtml should escape all special chars in one string" {
            // Given
            val input = "<script>alert('xss & \"fun\"')</script>"
            // When
            val result = escapeHtml(input)
            // Then
            result shouldNotContain "<"
            result shouldNotContain ">"
            result shouldNotContain "'"
            result shouldNotContain "\""
            result shouldContain "&lt;"
            result shouldContain "&gt;"
            result shouldContain "&#39;"
            result shouldContain "&quot;"
        }

        "escapeHtml should handle empty string" {
            // Given / When / Then
            escapeHtml("") shouldBe ""
        }

        // formatTimestamp

        "formatTimestamp should return N/A for zero" {
            // Given / When / Then
            formatTimestamp(0L) shouldBe "N/A"
        }

        "formatTimestamp should return formatted string for non-zero timestamp" {
            // Given
            val timestamp = 1_000_000_000_000L // 2001-09-09
            // When
            val result = formatTimestamp(timestamp)
            // Then
            result shouldContain "2001"
        }

        // formatNumber

        "formatNumber should format small number without separator" {
            // Given / When / Then
            formatNumber(42) shouldBe "42"
        }

        "formatNumber should format thousands" {
            // Given / When / Then
            formatNumber(1000) shouldContain "1"
            formatNumber(1000) shouldContain "000"
        }

        // formatDouble

        "formatDouble should format to 2 decimal places by default" {
            // Given / When / Then
            formatDouble(1.0) shouldBe "1.00"
        }

        "formatDouble should format to specified decimal places" {
            // Given / When / Then
            formatDouble(1.23456, 4) shouldBe "1.2346"
        }

        // toHtmlId

        "toHtmlId should replace dots with hyphens" {
            // Given / When / Then
            toHtmlId("net.dinkla.arclens") shouldBe "net-dinkla-arclens"
        }

        "toHtmlId should replace spaces with hyphens" {
            // Given / When / Then
            toHtmlId("My Package") shouldBe "my-package"
        }

        "toHtmlId should lowercase the result" {
            // Given / When / Then
            toHtmlId("FooBar") shouldBe "foobar"
        }
    })
