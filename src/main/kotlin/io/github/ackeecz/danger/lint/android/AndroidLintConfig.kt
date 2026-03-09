package io.github.ackeecz.danger.lint.android

import io.github.ackeecz.danger.lint.BuildFoldersMatcher
import io.github.ackeecz.danger.lint.android.AndroidLintSeverity.Error
import io.github.ackeecz.danger.lint.android.AndroidLintSeverity.Fatal
import io.github.ackeecz.danger.lint.android.AndroidLintSeverity.Warning
import java.io.File

/**
 * Configuration of Android Lint report processing.
 *
 * @param discovery Allows to configure how Android Lint report files are discovered.
 * @param report Allows to configure how Android Lint HTML report links are constructed.
 * @param severities Allows to configure which [AndroidLintSeverity] levels produce inline warnings.
 * Defaults to [Fatal], [Error], and [Warning].
 */
public class AndroidLintConfig(
    public val discovery: Discovery = Discovery(),
    public val report: Report = Report(),
    public val severities: Set<AndroidLintSeverity> = setOf(Fatal, Error, Warning),
) {

    /**
     * Configuration of Android Lint report file discovery.
     *
     * @param buildFoldersMatcher Allows to configure which build folders are scanned.
     * Defaults to [BuildFoldersMatcher.Specific] with "app/build".
     * @param reportFolderPath Allows to configure a path to the folder containing lint reports.
     * This path must be relative to the `build` directory. Defaults to "reports".
     * @param filePrefix Allows to configure a prefix used to filter report files by name.
     * Defaults to "lint-results-".
     */
    public class Discovery(
        public val buildFoldersMatcher: BuildFoldersMatcher = BuildFoldersMatcher.Specific("app/build"),
        public val reportFolderPath: String = "reports",
        public val filePrefix: String = "lint-results-",
    )

    /**
     * Configuration for constructing GitLab artifact URLs pointing to HTML lint reports.
     *
     * @param host Allows to configure the GitLab host. Defaults to "gitlab.ack.ee".
     * @param projectGroupName Allows to configure the GitLab project group name.
     * Defaults to the CI_PROJECT_NAMESPACE environment variable.
     * @param projectName Allows to configure the GitLab project name.
     * Defaults to the CI_PROJECT_NAME environment variable.
     * @param lintJobIdFilePath Allows to configure path to a file containing the lint CI job ID.
     * Defaults to the path provided by the LINT_JOB_ID_FILE environment variable.
     */
    public class Report(
        public val host: String = "gitlab.ack.ee",
        public val projectGroupName: String = System.getenv("CI_PROJECT_NAMESPACE")
            ?: error("CI_PROJECT_NAMESPACE env not set"),
        public val projectName: String = System.getenv("CI_PROJECT_NAME")
            ?: error("CI_PROJECT_NAME env not set"),
        lintJobIdFilePath: String = System.getenv("LINT_JOB_ID_FILE")
            ?: error("LINT_JOB_ID_FILE env not set"),
    ) {

        internal val lintJobId: String = readLintJobId(lintJobIdFilePath)

        private fun readLintJobId(filePath: String): String {
            return File(filePath.trim()).readText().trim()
        }
    }
}
