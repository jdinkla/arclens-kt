package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.examplePackage
import net.dinkla.arclens.exampleProject

class ImportStatisticsTest :
    StringSpec({
        "importStatistics should return statistics for one package" {
            val ls: AnalyzedPackage = AnalyzedPackage.from(examplePackage)
            ls.packageName shouldBe PackageName("net.dinkla.arclens")
            ls.importedElements.size shouldBe 3
            ls.importStatistics.total shouldBe 3
            ls.importStatistics.distinct shouldBe 2
        }

        "importStatistics should return statistics from Files" {
            val ls: List<AnalyzedPackage> = AnalyzedPackage.from(exampleProject)
            ls.size shouldBe 1
            ls[0].importedElements.size shouldBe 3
            ls[0].importStatistics.total shouldBe 3
            ls[0].importStatistics.distinct shouldBe 2
        }
    })
