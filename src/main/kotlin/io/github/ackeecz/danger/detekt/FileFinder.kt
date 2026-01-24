package io.github.ackeecz.danger.detekt

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

internal object FileFinder {

    private const val BUILD_DIR_NAME = "build"
    private const val REPORT_FILE_EXTENSION = "xml"

    fun findFiles(
        rootDirectoryPath: Path,
        config: DetektPlugin.Config.FileDiscovery,
    ): List<File> {
        val buildDirs = when (val matcher = config.buildFoldersMatcher) {
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
                matcher.paths.map { File(rootDirectoryPath.toFile(), it) }
            }
        }
        return buildDirs
            .flatMap { buildDir ->
                File(buildDir, config.detektFolderPath)
                    .listFiles()
                    ?.filter { it.extension == REPORT_FILE_EXTENSION }
                    .orEmpty()
            }.ifEmpty {
                throw NoFilesFoundException()
            }.also { println(it) }
    }
}

internal class NoFilesFoundException : IllegalStateException("No Detekt report files found. Check your FileDiscovery configuration.")
