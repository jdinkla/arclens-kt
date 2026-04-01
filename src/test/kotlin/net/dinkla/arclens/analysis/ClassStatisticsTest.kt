package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassModifier
import net.dinkla.arclens.domain.kotlinlang.ClassParameter
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.InheritanceModifier
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.domain.kotlinlang.TypeAlias
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier

class ClassStatisticsTest :
    StringSpec({
        "should return the statistics for a class" {
            val stats = ClassStatistics.from(project)
            stats shouldHaveSize 1
            stats[0] shouldBe
                ClassStatistics(
                    "C",
                    packageName,
                    VisibilityModifier.INTERNAL,
                    ClassSignature.Type.CLASS,
                    ClassModifier.DATA,
                    InheritanceModifier.OPEN,
                    ClassMetrics(2, 3, 4, 0, 2, 1, 1, 0, 0),
                )
        }

        "ClassMetrics should have default 0 values" {
            val metrics = ClassMetrics.from(project, classSignature)
            metrics.parameters shouldBe 2
            metrics.superTypes shouldBe 3
            metrics.declarations shouldBe 4
            metrics.classes shouldBe 0
            metrics.functions shouldBe 2
            metrics.properties shouldBe 1
            metrics.aliases shouldBe 1
            metrics.superClasses shouldBe 0
            metrics.subClasses shouldBe 0
            println(Json.encodeToString(metrics))
        }
    })

private val classSignature =
    ClassSignature(
        "C",
        listOf(ClassParameter("p", Type("Int")), ClassParameter("q", Type("Int"))),
        listOf("Interface1", "Interface2", "Interface3"),
        VisibilityModifier.INTERNAL,
        classModifier = ClassModifier.DATA,
        inheritanceModifier = InheritanceModifier.OPEN,
        declarations =
            listOf(
                FunctionSignature("toString", Type("String"), listOf()),
                FunctionSignature("hashCode", Type("Int"), listOf()),
                Property("p", Type("Int")),
                TypeAlias("T", Type("Int")),
            ),
        elementType = ClassSignature.Type.CLASS,
    )

private val packageName = PackageName("P")

private val project =
    Project(
        "dir",
        listOf(
            KotlinFile(
                filePath = FilePath("filePath"),
                packageName = packageName,
                imports = listOf(),
                declarations = listOf(classSignature),
            ),
        ),
    )
