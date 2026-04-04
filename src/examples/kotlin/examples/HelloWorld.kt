package example

import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

internal data class HelloWorld(
    val many: Int,
) {
    override fun toString(): String = "many hello worlds $many"
}

private interface Gen {
    fun gen(n: Int): String?
}

class GenImpl(
    val many: Int,
) : Gen {
    override fun gen(n: Int): String = "many hello worlds ${many * n}"
}

object MathUtils {
    private const val K_EPSILON = 0.01

    fun isZero(x: Double): Boolean = x > -K_EPSILON && x < K_EPSILON
}

enum class AB { A, B }

enum class ABC(
    internal val i: Int,
) {
    A(1),
    B(2),
    C(3),
}

internal fun topLevelFunction(
    n: Int,
    hw: HelloWorld,
): String = FALSE.toString() + TRUE.toString() + hw.toString().repeat(n)

private fun HelloWorld.extensionFun(): String = this.many.toString()

open class O1 {
    protected fun f(x: Int): String = x.toString()

    class I(
        val name: String,
    )
}

fun higherOrderFunction(
    f: (Int) -> String,
    x: Int,
): (Int) -> String = { y -> f(x + y) }

typealias Dictionary = Map<String, String>

fun create(ls: List<String>): Dictionary = ls.map { it to it }.toMap()

val myProperty: String = "is theft..."
internal const val THE_ANSWER: Int = 42
private val twentyOne = HelloWorld(21)

fun withSuspend(action: suspend () -> Unit) {}

fun withSuspendResult(action: suspend (Int) -> String): String = ""

fun withSuspendNullableReturn(action: suspend (Int) -> String?): String = ""

class WithSecondary(
    val x: Int,
    val y: Int,
) {
    constructor(x: Int) : this(x, 0)

    private constructor() : this(0, 0)
}

fun main() {
    println("has no args and returns Unit implicitly")
    MathUtils.isZero(2.3)
    val hw = HelloWorld(3)
    topLevelFunction(3, hw)
    GenImpl(1)
    AB.A
    AB.B
    ABC.A
    ABC.B
    ABC.C
    hw.extensionFun()
    O1()
    higherOrderFunction({ x -> x.toString() }, 1)(2)
    create(listOf(myProperty, THE_ANSWER.toString()))
    twentyOne.toString()
}
