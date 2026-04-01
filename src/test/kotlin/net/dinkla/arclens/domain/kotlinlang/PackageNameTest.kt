package net.dinkla.arclens.domain.kotlinlang

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class PackageNameTest :
    FunSpec({

        context("isSubPackageOf") {
            withData(
                Triple("a.b.c", "a.b", true),
                Triple("a.b", "a.b.c", false),
                Triple("a.b", "a.d", false),
                Triple("a.b.c", "a.d", false),
                Triple("x.b", "a.b.c", false),
                Triple("x", "a", false),
            ) { (a, b, expected) ->
                PackageName(a) isSubPackageOf PackageName(b) shouldBe expected
            }
        }

        context("isSuperPackage") {
            withData(
                Triple("a.b.c", "a.b", false),
                Triple("a.b", "a.b.c", true),
                Triple("a.b", "a.d", false),
                Triple("a.b.c", "a.d", false),
                Triple("x.b", "a.b.c", false),
                Triple("x", "a", false),
            ) { (a, b, expected) ->
                PackageName(a) isSuperPackage PackageName(b) shouldBe expected
            }
        }

        context("isSidePackage") {
            withData(
                Triple("a.b.c", "a.b", false),
                Triple("a.b", "a.b.c", false),
                Triple("a.b", "a.d", true),
                Triple("a.b.c", "a.d", true),
                Triple("x.b", "a.b.c", false),
                Triple("x", "a", false),
            ) { (a, b, expected) ->
                PackageName(a) isSidePackage PackageName(b) shouldBe expected
            }
        }

        context("isOtherPackage") {
            withData(
                Triple("a.b.c", "a.b", false),
                Triple("a.b", "a.b.c", false),
                Triple("a.b", "a.d", false),
                Triple("a.b.c", "a.d", false),
                Triple("x.b", "a.b.c", true),
                Triple("x", "a", true),
            ) { (a, b, expected) ->
                PackageName(a) isOtherPackage PackageName(b) shouldBe expected
            }
        }
    })
