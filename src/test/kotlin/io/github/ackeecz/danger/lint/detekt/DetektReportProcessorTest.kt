package io.github.ackeecz.danger.lint.detekt

import io.github.ackeecz.danger.lint.FakeDangerContext
import io.github.ackeecz.danger.lint.util.loadResourceFile
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

private lateinit var context: FakeDangerContext
private lateinit var underTest: DetektReportProcessor

internal class DetektReportProcessorTest : FunSpec({

    beforeEach {
        context = FakeDangerContext()
        underTest = DetektReportProcessor(context)
    }

    test("should process two files in report") {
        underTest.process(listOf(loadResourceFile("detekt_result_two_files.xml")))

        context.warnings shouldHaveSize 6
    }

    test("should process two report files") {
        underTest.process(
            listOf(
                loadResourceFile("detekt_result_two_files.xml"),
                loadResourceFile("detekt_result_single_file.xml"),
            )
        )

        context.warnings shouldHaveSize 8
    }

    test("should process one file in report") {
        underTest.process(listOf(loadResourceFile("detekt_result_single_file.xml")))

        context.warnings shouldHaveSize 2
    }

    test("should process no file in report") {
        underTest.process(listOf(loadResourceFile("detekt_result_no_files.xml")))

        context.warnings.shouldBeEmpty()
    }

    test("should have relative path in filename") {
        underTest.process(listOf(loadResourceFile("detekt_result_single_file.xml")))

        context.warnings.first().file shouldBe "features/recipelist/src/main/java/cz/ackee/sample/recipelist/presentation/RecipeListFragment.kt"
    }
})
