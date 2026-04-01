package net.dinkla.arclens

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

data class Person(
    val yearOfBirth: Int,
)

class ExampleTest :
    StringSpec({
        "function f should calculate the age of a person" {
            f(Person(2000)) shouldBe 24
        }
    })

fun f(person: Person): Int = 2024 - person.yearOfBirth
