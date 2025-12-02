package io.github.ackeecz.danger.detekt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.io.File

internal class DetektPluginTest : FunSpec({

    test("should process two files in report") {
        val context = FakeDangerContext()
        DetektPlugin.context = context

        DetektPlugin.parseAndReport(
            loadFile("detekt_result_two_files.xml"),
        )

        context.warnings shouldHaveSize 6
    }

    test("should process two report files") {
        val context = FakeDangerContext()
        DetektPlugin.context = context

        DetektPlugin.parseAndReport(
            loadFile("detekt_result_two_files.xml"),
            loadFile("detekt_result_single_file.xml"),
        )

        context.warnings shouldHaveSize 8
    }

    test("should process one file in report") {
        val context = FakeDangerContext()
        DetektPlugin.context = context

        DetektPlugin.parseAndReport(
            loadFile("detekt_result_single_file.xml")
        )

        context.warnings shouldHaveSize 2
    }

    test("should process no file in report") {
        val context = FakeDangerContext()
        DetektPlugin.context = context

        DetektPlugin.parseAndReport(
            loadFile("detekt_result_no_files.xml")
        )

        context.warnings.shouldBeEmpty()
    }

    test("should have relative path in filename") {
        val context = FakeDangerContext()
        DetektPlugin.context = context

        DetektPlugin.parseAndReport(
            loadFile("detekt_result_single_file.xml"),
        )

        context.warnings.first().file shouldBe "features/recipelist/src/main/java/cz/ackee/sample/recipelist/presentation/RecipeListFragment.kt"
    }
})

private fun loadFile(name: String): File {
    return File(ClassLoader.getSystemResource(name).toURI())
}
