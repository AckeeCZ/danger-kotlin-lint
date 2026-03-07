package io.github.ackeecz.danger.lint.android

import java.io.File

internal fun createTestReport(tempDir: File): AndroidLintConfig.Report {
    val jobIdFile = File(tempDir, "job-id.txt").also { it.writeText("12345") }
    return AndroidLintConfig.Report(
        host = "gitlab.test.com",
        projectGroupName = "testgroup",
        projectName = "testproject",
        lintJobIdFilePath = jobIdFile.absolutePath,
    )
}
