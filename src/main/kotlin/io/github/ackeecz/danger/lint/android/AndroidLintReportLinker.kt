package io.github.ackeecz.danger.lint.android

import io.github.ackeecz.danger.lint.PathRelativizer
import systems.danger.kotlin.sdk.DangerContext
import java.io.File

internal class AndroidLintReportLinker(
    private val context: DangerContext,
) {

    private val pathRelativizer = PathRelativizer()

    fun link(
        htmlFiles: List<File>,
        reportPathsWithIssues: Set<String>,
        config: AndroidLintConfig.Report,
    ) {
        htmlFiles
            .filter { it.absolutePath.removeSuffix(".html") in reportPathsWithIssues }
            .forEach { file ->
                val relativePath = pathRelativizer.relativize(file.absolutePath)
                val url = "https://${config.host}/${config.projectGroupName}/${config.projectName}" +
                    "/-/jobs/${config.lintJobId}/artifacts/raw/$relativePath"
                val fileName = file.name
                context.markdown("[$fileName]($url)")
            }
    }
}
