package io.github.ackeecz.danger.lint

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

internal object FileFinder {

    private const val BUILD_DIR_NAME = "build"
    private const val REPORT_FILE_EXTENSION = "xml"

    fun findFiles(
        rootDirectoryPath: Path,
        buildFoldersMatcher: BuildFoldersMatcher,
        reportFilesFolderPath: String,
    ): List<File> {
        val buildDirs = when (buildFoldersMatcher) {
            is BuildFoldersMatcher.All -> {
                val maxDepth = 6
                Files.find(
                    rootDirectoryPath,
                    maxDepth,
                    { path, attributes ->
                        attributes.isDirectory && path.fileName.name == BUILD_DIR_NAME
                    },
                ).map { it.toFile() }.toList()
            }
            is BuildFoldersMatcher.Specific -> {
                buildFoldersMatcher.paths.map { File(rootDirectoryPath.toFile(), it) }
            }
        }
        return buildDirs
            .flatMap { buildDir ->
                File(buildDir, reportFilesFolderPath)
                    .listFiles()
                    ?.filter { it.extension == REPORT_FILE_EXTENSION }
                    .orEmpty()
            }.ifEmpty {
                throw NoFilesFoundException()
            }
    }
}
