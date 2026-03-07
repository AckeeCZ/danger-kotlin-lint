package io.github.ackeecz.danger.lint.detekt

import io.github.ackeecz.danger.lint.BuildFoldersMatcher

/**
 * Configuration of Detekt report processing.
 *
 * @param discovery Allows to configure how Detekt report files are discovered.
 */
public class DetektConfig(
    public val discovery: Discovery = Discovery(),
) {

    public class Discovery(
        public val buildFoldersMatcher: BuildFoldersMatcher = BuildFoldersMatcher.All,
        public val detektFolderPath: String = "reports/detekt",
    )
}
