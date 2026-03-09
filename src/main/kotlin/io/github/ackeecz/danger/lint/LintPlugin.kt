package io.github.ackeecz.danger.lint

import io.github.ackeecz.danger.lint.android.AndroidLintConfig
import io.github.ackeecz.danger.lint.android.AndroidLintReportLinker
import io.github.ackeecz.danger.lint.android.AndroidLintReportProcessor
import io.github.ackeecz.danger.lint.detekt.DetektConfig
import io.github.ackeecz.danger.lint.detekt.DetektReportProcessor
import systems.danger.kotlin.sdk.DangerPlugin
import java.nio.file.Paths

/**
 * Plugin for danger-kotlin processing outputs of lint tools.
 */
public object LintPlugin : DangerPlugin() {

    override val id: String = "danger-kotlin-lint"

    /**
     * Finds and parses XML outputs of Detekt and reports inline comments to the pull request.
     *
     * @param config Config of Detekt report processing
     */
    public fun findAndProcessDetektReports(config: DetektConfig = DetektConfig()) {
        val reportFiles = FileFinder.findFiles(
            rootDirectoryPath = Paths.get(""),
            buildFoldersMatcher = config.discovery.buildFoldersMatcher,
            reportFilesFolderPath = config.discovery.detektFolderPath,
        )
        DetektReportProcessor(context).process(reportFiles)
    }

    /**
     * Finds and parses XML outputs of Android Lint and reports inline comments to the pull request.
     * Also posts Markdown links to HTML lint reports for modules that had issues.
     *
     * @param config Config of Android Lint report processing
     */
    public fun findAndProcessAndroidLintReports(config: AndroidLintConfig = AndroidLintConfig()) {
        val buildDirs = FileFinder.findBuildDirs(
            rootDirectoryPath = Paths.get(""),
            buildFoldersMatcher = config.discovery.buildFoldersMatcher,
        )
        val xmlReportFiles = FileFinder.findFiles(
            buildDirs = buildDirs,
            reportFilesFolderPath = config.discovery.reportFolderPath,
            filePrefix = config.discovery.filePrefix,
        )
        val reportPathsWithIssues = AndroidLintReportProcessor(context).process(xmlReportFiles, config.severities)

        val htmlReportFiles = FileFinder.findFiles(
            buildDirs = buildDirs,
            reportFilesFolderPath = config.discovery.reportFolderPath,
            filePrefix = config.discovery.filePrefix,
            fileExtension = "html",
        )
        AndroidLintReportLinker(context).link(htmlReportFiles, reportPathsWithIssues, config.report)
    }
}
