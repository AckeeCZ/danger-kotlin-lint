package io.github.ackeecz.danger.lint

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
}
