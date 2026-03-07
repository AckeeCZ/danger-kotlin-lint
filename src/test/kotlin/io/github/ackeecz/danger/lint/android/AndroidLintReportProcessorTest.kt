package io.github.ackeecz.danger.lint.android

import io.github.ackeecz.danger.lint.FakeDangerContext
import io.github.ackeecz.danger.lint.util.loadResourceFile
import io.github.ackeecz.danger.lint.util.rootTempTestDir
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.io.File

private lateinit var context: FakeDangerContext
private lateinit var underTest: AndroidLintReportProcessor

internal class AndroidLintReportProcessorTest : FunSpec({

    beforeEach {
        context = FakeDangerContext()
        underTest = AndroidLintReportProcessor(context)
    }

    test("process report with single issue produces one warning") {
        underTest.process(listOf(loadResourceFile("android_lint_single_issue.xml")), AndroidLintSeverity.entries.toSet())

        context.warnings shouldHaveSize 1
    }

    test("process report with multiple issues produces correct warning count") {
        underTest.process(listOf(loadResourceFile("android_lint_multiple_issues.xml")), AndroidLintSeverity.entries.toSet())

        context.warnings shouldHaveSize 3
    }

    test("process report with no issues produces zero warnings") {
        underTest.process(listOf(loadResourceFile("android_lint_no_issues.xml")), AndroidLintSeverity.entries.toSet())

        context.warnings.shouldBeEmpty()
    }

    test("severity filtering - only configured severities produce warnings") {
        // multiple_issues has: Error, Warning, Hint (→ Information) — only Error and Warning are configured

        underTest.process(
            listOf(loadResourceFile("android_lint_multiple_issues.xml")),
            setOf(AndroidLintSeverity.Error, AndroidLintSeverity.Warning),
        )

        context.warnings shouldHaveSize 2
    }

    test("warning message follows 'Android Lint [Severity]: message (id)' format") {
        underTest.process(listOf(loadResourceFile("android_lint_single_issue.xml")), setOf(AndroidLintSeverity.Error))

        context.warnings.first().message shouldBe
            "Android Lint [Error]: Hardcoded string \"Hello\", should use `@string` resource (HardcodedText)"
    }

    test("warning file path is relative to current working directory") {
        val tempDir = rootTempTestDir
        val cwd = File("").absolutePath
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <issues format="6" by="lint 8.2.0">
                <issue id="HardcodedText" severity="Error" message="Hardcoded text" summary="s" explanation="e">
                    <location file="$cwd/app/src/main/res/layout/activity_main.xml" line="12" column="5"/>
                </issue>
            </issues>
        """.trimIndent().trim()
        val tmpFile = File(tempDir, "test.xml").also { it.writeText(xmlContent) }

        underTest.process(listOf(tmpFile), setOf(AndroidLintSeverity.Error))

        context.warnings.first().file shouldBe "app/src/main/res/layout/activity_main.xml"
    }

    test("multi-location issue uses only first location") {
        underTest.process(listOf(loadResourceFile("android_lint_multi_location.xml")), setOf(AndroidLintSeverity.Fatal))

        context.warnings shouldHaveSize 1
        context.warnings.first().line shouldBe 20
    }

    test("process multiple report files produces combined warnings") {
        underTest.process(
            listOf(
                loadResourceFile("android_lint_single_issue.xml"),
                loadResourceFile("android_lint_multiple_issues.xml"),
            ),
            AndroidLintSeverity.entries.toSet(),
        )

        context.warnings shouldHaveSize 4
    }

    test("returns absolute paths without extension for reports with matching issues") {
        val reportFile = loadResourceFile("android_lint_single_issue.xml")
        val result = underTest.process(listOf(reportFile), setOf(AndroidLintSeverity.Error))

        result shouldBe setOf(reportFile.absolutePath.removeSuffix(".xml"))
    }

    test("issue with unknown severity produces warning identifying unknown severity") {
        underTest.process(
            listOf(loadResourceFile("android_lint_unknown_severity.xml")),
            AndroidLintSeverity.entries.toSet(),
        )

        context.warnings shouldHaveSize 1
        context.warnings.first().message shouldBe
            "Android Lint [Unknown severity: CustomSeverity]: Some lint message (SomeCheck)"
    }

    test("unknown severity is reported regardless of configured severity filter") {
        underTest.process(
            listOf(loadResourceFile("android_lint_unknown_severity.xml")),
            setOf(AndroidLintSeverity.Error),
        )

        context.warnings shouldHaveSize 1
    }

    test("returns empty set when no issues match configured severities") {
        // multiple_issues only has Error, Warning, Hint (→ Information) — Ignore doesn't match any
        val result = underTest.process(listOf(loadResourceFile("android_lint_multiple_issues.xml")), setOf(AndroidLintSeverity.Ignore))

        result.shouldBeEmpty()
    }

    test("Hint severity in XML is treated as Information") {
        underTest.process(listOf(loadResourceFile("android_lint_hint_severity.xml")), setOf(AndroidLintSeverity.Information))

        context.warnings shouldHaveSize 1
        context.warnings.first().message shouldBe
            "Android Lint [Information]: This folder configuration (`v21`) is unnecessary; `minSdkVersion` is 21 (ObsoleteSdkInt)"
    }

    test("issue without line number in location is reported as non-inline warning") {
        underTest.process(listOf(loadResourceFile("android_lint_no_line.xml")), setOf(AndroidLintSeverity.Fatal))

        context.warnings shouldHaveSize 1
        context.warnings.first().message shouldBe
            "Android Lint [Fatal]: Invalid package reference in library; not included in Android: java.nio.file." +
            " Referenced from com/example/Foo.class (InvalidPackage)"
        context.warnings.first().file shouldBe null
        context.warnings.first().line shouldBe null
    }

    test("issue without location element is reported as non-inline warning") {
        underTest.process(listOf(loadResourceFile("android_lint_no_location.xml")), setOf(AndroidLintSeverity.Error))

        context.warnings shouldHaveSize 1
        context.warnings.first().file shouldBe null
        context.warnings.first().line shouldBe null
    }

    test("report with mix of inline and non-inline issues reports all") {
        underTest.process(
            listOf(
                loadResourceFile("android_lint_single_issue.xml"),
                loadResourceFile("android_lint_no_line.xml"),
                loadResourceFile("android_lint_no_location.xml"),
            ),
            AndroidLintSeverity.entries.toSet(),
        )

        context.warnings shouldHaveSize 3
    }
})
