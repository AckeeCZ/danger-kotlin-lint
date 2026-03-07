package io.github.ackeecz.danger.lint.android

import io.github.ackeecz.danger.lint.FakeDangerContext
import io.github.ackeecz.danger.lint.util.rootTempTestDir
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import java.io.File

private lateinit var context: FakeDangerContext
private lateinit var underTest: AndroidLintReportLinker

internal class AndroidLintReportLinkerTest : FunSpec({

    beforeEach {
        context = FakeDangerContext()
        underTest = AndroidLintReportLinker(context)
    }

    test("no HTML files produces no markdown") {
        val report = createTestReport(rootTempTestDir)

        underTest.link(emptyList(), setOf("/some/path/lint-results-debug"), report)

        context.markdowns.shouldBeEmpty()
    }

    test("single HTML file in reportPathsWithIssues produces one markdown link") {
        val tempDir = rootTempTestDir
        val report = createTestReport(tempDir)
        val htmlFile = createHtmlFile("app/build/reports/lint-results-debug.html", tempDir)
        val reportPathsWithIssues = setOf(htmlFile.absolutePath.removeSuffix(".html"))

        underTest.link(listOf(htmlFile), reportPathsWithIssues, report)

        context.markdowns shouldHaveSize 1
    }

    test("single HTML file NOT in reportPathsWithIssues produces no markdown") {
        val tempDir = rootTempTestDir
        val report = createTestReport(tempDir)
        val htmlFile = createHtmlFile("app/build/reports/lint-results-debug.html", tempDir)

        underTest.link(listOf(htmlFile), emptySet(), report)

        context.markdowns.shouldBeEmpty()
    }

    test("multiple HTML files across modules - only matching ones get links") {
        val tempDir = rootTempTestDir
        val report = createTestReport(tempDir)
        val appHtml = createHtmlFile("app/build/reports/lint-results-debug.html", tempDir)
        val featureHtml = createHtmlFile("feature/build/reports/lint-results-debug.html", tempDir)
        val reportPathsWithIssues = setOf(appHtml.absolutePath.removeSuffix(".html"))

        underTest.link(listOf(appHtml, featureHtml), reportPathsWithIssues, report)

        context.markdowns shouldHaveSize 1
    }

    test("two modules with same report name - only the module with issues gets linked") {
        val tempDir = rootTempTestDir
        val report = createTestReport(tempDir)
        val appHtml = createHtmlFile("app/build/reports/lint-results-debug.html", tempDir)
        val featureHtml = createHtmlFile("feature/build/reports/lint-results-debug.html", tempDir)
        // Only feature module had issues
        val reportPathsWithIssues = setOf(featureHtml.absolutePath.removeSuffix(".html"))

        underTest.link(listOf(appHtml, featureHtml), reportPathsWithIssues, report)

        context.markdowns shouldHaveSize 1
        context.markdowns.first().message shouldContain "feature"
    }

    test("empty reportPathsWithIssues set produces no markdown at all") {
        val tempDir = rootTempTestDir
        val report = createTestReport(tempDir)
        val htmlFiles = listOf(
            createHtmlFile("app/build/reports/lint-results-debug.html", tempDir),
            createHtmlFile("feature/build/reports/lint-results-debug.html", tempDir),
        )

        underTest.link(htmlFiles, emptySet(), report)

        context.markdowns.shouldBeEmpty()
    }

    test("URL format uses host, group, project, jobId and relative path") {
        val tempDir = rootTempTestDir
        val jobIdFile = File(tempDir, "job-id.txt").also { it.writeText("99999") }
        val report = AndroidLintConfig.Report(
            host = "gitlab.example.com",
            projectGroupName = "mygroup",
            projectName = "myproject",
            lintJobIdFilePath = jobIdFile.absolutePath,
        )
        val htmlFile = createHtmlFile("app/build/reports/lint-results-debug.html", tempDir)
        val reportPathsWithIssues = setOf(htmlFile.absolutePath.removeSuffix(".html"))

        underTest.link(listOf(htmlFile), reportPathsWithIssues, report)

        val markdown = context.markdowns.first().message
        markdown shouldContain "https://gitlab.example.com/mygroup/myproject/-/jobs/99999/artifacts/raw"
        markdown shouldContain "app/build/reports/lint-results-debug.html"
    }
})

private fun createHtmlFile(relativePath: String, tempDir: File): File {
    return File(tempDir, relativePath).also {
        it.parentFile.mkdirs()
        it.createNewFile()
    }
}
