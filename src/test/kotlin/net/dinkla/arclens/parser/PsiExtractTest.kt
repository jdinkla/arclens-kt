package net.dinkla.arclens.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassModifier
import net.dinkla.arclens.domain.kotlinlang.ClassParameter
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionModifier
import net.dinkla.arclens.domain.kotlinlang.FunctionParameter
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.InheritanceModifier
import net.dinkla.arclens.domain.kotlinlang.MemberModifier
import net.dinkla.arclens.domain.kotlinlang.ParameterModifier
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.PropertyModifier
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.domain.kotlinlang.TypeAlias
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier

class PsiExtractTest :
    StringSpec({
        val parser = PsiParser()

        fun parseFile(source: String) = extractKotlinFile(parser.parseText(source), FilePath("/test.kt"))

        fun parseDeclarations(source: String) = parseFile(source).declarations

        // ==================== Suspend function types ====================

        "should parse suspend function type parameter" {
            // Given
            val source = "fun withSuspend(action: suspend () -> Unit) {}"

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters shouldBe
                listOf(
                    FunctionParameter("action", Type("suspend () -> Unit")),
                )
        }

        "should parse suspend function type with params and return type" {
            // Given
            val source =
                "fun withSuspendResult(action: suspend (Int) -> String): String = \"\""

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters shouldBe
                listOf(
                    FunctionParameter("action", Type("suspend (Int) -> String")),
                )
        }

        "should parse suspend function type with nullable return type" {
            // Given
            val source =
                "fun f(action: suspend (Int) -> String?): String = \"\""

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters shouldBe
                listOf(
                    FunctionParameter("action", Type("suspend (Int) -> String?")),
                )
        }

        // ==================== Secondary constructors ====================

        "should parse secondary constructors" {
            // Given
            val source =
                """
                class C(val x: Int, val y: Int) {
                    constructor(x: Int) : this(x, 0)
                    private constructor() : this(0, 0)
                }
                """.trimIndent()

            // When
            val cls = parseDeclarations(source).first() as ClassSignature
            val constructors = cls.constructors

            // Then
            constructors shouldHaveSize 2
            constructors[0].parameters shouldBe
                listOf(
                    FunctionParameter("x", Type("Int")),
                )
            constructors[0].visibilityModifier shouldBe null
            constructors[1].parameters shouldBe emptyList()
            constructors[1].visibilityModifier shouldBe VisibilityModifier.PRIVATE
        }

        // ==================== Imports ====================

        "should parse imports" {
            // Given
            val source =
                """
                import java.io.File
                import kotlin.math.PI
                fun f() {}
                """.trimIndent()

            // When
            val file = parseFile(source)

            // Then
            file.imports shouldHaveSize 2
            file.imports[0].name.name shouldBe "java.io.File"
            file.imports[1].name.name shouldBe "kotlin.math.PI"
        }

        // ==================== Properties ====================

        "should parse top-level properties" {
            // Given
            val source =
                """
                val x: String = "hello"
                var y: Int = 42
                private const val Z: Int = 0
                """.trimIndent()

            // When
            val declarations = parseDeclarations(source)

            // Then
            declarations shouldHaveSize 3
            val p1 = declarations[0] as Property
            p1.name shouldBe "x"
            p1.dataType shouldBe Type("String")

            val p2 = declarations[1] as Property
            p2.name shouldBe "y"
            p2.modifier shouldBe PropertyModifier.VAR

            val p3 = declarations[2] as Property
            p3.name shouldBe "Z"
            p3.modifier shouldBe PropertyModifier.CONST_VAL
            p3.visibilityModifier shouldBe VisibilityModifier.PRIVATE
        }

        "should parse property with lateinit" {
            // Given
            val source =
                """
                class A {
                    lateinit var name: String
                }
                """.trimIndent()

            // When
            val cls = parseDeclarations(source).first() as ClassSignature

            // Then
            val props = cls.properties
            props shouldHaveSize 1
            props[0].memberModifier shouldBe listOf(MemberModifier.LATE_INIT)
        }

        // ==================== Objects ====================

        "should parse object declaration" {
            // Given
            val source =
                """
                object Util {
                    fun helper(): String = ""
                }
                """.trimIndent()

            // When
            val obj = parseDeclarations(source).first() as ClassSignature

            // Then
            obj.name shouldBe "Util"
            obj.elementType shouldBe ClassSignature.Type.OBJECT
            obj.functions shouldHaveSize 1
        }

        // ==================== Type aliases ====================

        "should parse type alias" {
            // Given
            val source = "typealias StringMap = Map<String, String>"

            // When
            val ta = parseDeclarations(source).first() as TypeAlias

            // Then
            ta.name shouldBe "StringMap"
            ta.def shouldBe Type("Map<String,String>")
        }

        // ==================== Generic types ====================

        "should parse generic type parameters" {
            // Given
            val source = "fun f(items: List<String>): Map<String, Int> = mapOf()"

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters.first().type shouldBe Type("List<String>")
            function.returnType shouldBe Type("Map<String,Int>")
        }

        // ==================== Nullable types ====================

        "should parse nullable types" {
            // Given
            val source = "fun f(x: String?): Int? = null"

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters.first().type shouldBe Type("String?")
            function.returnType shouldBe Type("Int?")
        }

        "should parse nullable function type" {
            // Given
            val source = "fun f(action: ((Int) -> String)?): String = \"\""

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters.first().type shouldBe Type("(Int) -> String?")
        }

        // ==================== Extension functions ====================

        "should parse extension function" {
            // Given
            val source = "fun String.reversed2(): String = \"\""

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.name shouldBe "reversed2"
            function.extensionOf shouldBe "String"
        }

        // ==================== Function modifiers ====================

        "should parse function modifiers" {
            // Given
            val source =
                """
                suspend fun s() {}
                inline fun i(block: () -> Unit) { block() }
                infix fun Int.plus2(other: Int): Int = this + other
                tailrec fun factorial(n: Int, acc: Int = 1): Int =
                    if (n <= 1) acc else factorial(n - 1, n * acc)
                operator fun Int.not(): Int = -this
                """.trimIndent()

            // When
            val declarations = parseDeclarations(source)

            // Then
            declarations shouldHaveSize 5
            (declarations[0] as FunctionSignature).functionModifiers shouldBe
                listOf(FunctionModifier.SUSPEND)
            (declarations[1] as FunctionSignature).functionModifiers shouldBe
                listOf(FunctionModifier.INLINE)
            (declarations[2] as FunctionSignature).functionModifiers shouldBe
                listOf(FunctionModifier.INFIX)
            (declarations[3] as FunctionSignature).functionModifiers shouldBe
                listOf(FunctionModifier.TAILREC)
            (declarations[4] as FunctionSignature).functionModifiers shouldBe
                listOf(FunctionModifier.OPERATOR)
        }

        // ==================== Parameter modifiers ====================

        "should parse parameter modifiers" {
            // Given
            val source = "fun f(vararg items: String) {}"

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters.first().modifier shouldBe ParameterModifier.VARARG
        }

        "should parse noinline and crossinline parameter modifiers" {
            // Given
            val source =
                """
                inline fun f(
                    noinline a: () -> Unit,
                    crossinline b: () -> Unit
                ) { a(); b() }
                """.trimIndent()

            // When
            val function = parseDeclarations(source).first() as FunctionSignature

            // Then
            function.parameters[0].modifier shouldBe ParameterModifier.NOINLINE
            function.parameters[1].modifier shouldBe ParameterModifier.CROSSINLINE
        }

        // ==================== Class modifiers ====================

        "should parse class modifiers" {
            // Given
            val source =
                """
                data class D(val x: Int)
                enum class E { A }
                sealed class S
                @JvmInline value class V(val v: String)
                abstract class Ab
                """.trimIndent()

            // When
            val declarations = parseDeclarations(source)

            // Then
            (declarations[0] as ClassSignature).classModifier shouldBe ClassModifier.DATA
            (declarations[1] as ClassSignature).classModifier shouldBe ClassModifier.ENUM
            (declarations[2] as ClassSignature).classModifier shouldBe ClassModifier.SEALED
            (declarations[3] as ClassSignature).classModifier shouldBe ClassModifier.VALUE
            (declarations[4] as ClassSignature).inheritanceModifier shouldBe
                InheritanceModifier.ABSTRACT
        }

        // ==================== Visibility modifiers ====================

        "should parse all visibility modifiers" {
            // Given
            val source =
                """
                public fun pub() {}
                internal fun int() {}
                protected fun pro() {}
                private fun pri() {}
                """.trimIndent()

            // When
            val declarations = parseDeclarations(source)

            // Then
            (declarations[0] as FunctionSignature).visibilityModifier shouldBe
                VisibilityModifier.PUBLIC
            (declarations[1] as FunctionSignature).visibilityModifier shouldBe
                VisibilityModifier.INTERNAL
            (declarations[2] as FunctionSignature).visibilityModifier shouldBe
                VisibilityModifier.PROTECTED
            (declarations[3] as FunctionSignature).visibilityModifier shouldBe
                VisibilityModifier.PRIVATE
        }

        // ==================== Class parameters ====================

        "should parse class parameters with var and without val/var" {
            // Given
            val source = "class C(val a: Int, var b: String, c: Double)"

            // When
            val cls = parseDeclarations(source).first() as ClassSignature

            // Then
            cls.parameters shouldBe
                listOf(
                    ClassParameter("a", Type("Int"), propertyModifier = PropertyModifier.VAL),
                    ClassParameter("b", Type("String"), propertyModifier = PropertyModifier.VAR),
                    ClassParameter("c", Type("Double")),
                )
        }

        // ==================== Super types ====================

        "should parse super types" {
            // Given
            val source =
                """
                interface I
                class C : I
                """.trimIndent()

            // When
            val declarations = parseDeclarations(source)

            // Then
            val cls = declarations[1] as ClassSignature
            cls.superTypes shouldBe listOf("I")
        }

        // ==================== Member modifier: override ====================

        "should parse override member modifier" {
            // Given
            val source =
                """
                class C {
                    override fun toString(): String = ""
                }
                """.trimIndent()

            // When
            val cls = parseDeclarations(source).first() as ClassSignature

            // Then
            cls.functions.first().memberModifier shouldBe MemberModifier.OVERRIDE
        }

        // ==================== Nested classes and objects ====================

        "should parse nested class and companion object" {
            // Given
            val source =
                """
                class Outer {
                    class Inner
                    object Nested {
                        fun helper() {}
                    }
                }
                """.trimIndent()

            // When
            val outer = parseDeclarations(source).first() as ClassSignature

            // Then
            outer.classes shouldHaveSize 2
            outer.classes[0].name shouldBe "Inner"
            outer.classes[0].elementType shouldBe ClassSignature.Type.CLASS
            outer.classes[1].name shouldBe "Nested"
            outer.classes[1].elementType shouldBe ClassSignature.Type.OBJECT
            outer.classes[1].functions shouldHaveSize 1
        }
    })
