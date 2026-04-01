package net.dinkla.arclens.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import net.dinkla.arclens.SOURCE_DIRECTORY
import java.io.File
import kotlin.io.path.createTempFile

class FileUtilitiesTest :
    StringSpec({
        "getAllKotlinFilesInDirectory" {
            val files = getAllKotlinFiles(SOURCE_DIRECTORY)
            files.size shouldBe 2
        }

        "shouldFileBeAdded should ignore files in .idea" {
            isRelevant(File("/.idea/HelloWorld.kt")) shouldBe false
        }

        "shouldFileBeAdded should ignore test files" {
            isRelevant(File("/test/HelloWorldTest.kt")) shouldBe false
        }

        "shouldFileBeAdded should add other files" {
            isRelevant(File("/src/main/HelloWorld.kt")) shouldBe true
        }

        @Serializable
        data class Test(
            val text: String,
        )

        "saveToJsonFile should save to temporary file" {
            val file = createTempFile().toFile()
            try {
                val testObject = Test("Some")
                file.saveJson(testObject)
                file.loadJson<Test>() shouldBe testObject
            } finally {
                file.delete()
            }
        }
    })
