package io.github.ackeecz.danger.lint.detekt

import io.github.ackeecz.danger.lint.BuildFoldersMatcher

public class DetektConfig(
    public val discovery: Discovery = Discovery(),
) {

    public class Discovery(
        public val buildFoldersMatcher: BuildFoldersMatcher = BuildFoldersMatcher.All,
        public val detektFolderPath: String = "reports/detekt",
    )
}
