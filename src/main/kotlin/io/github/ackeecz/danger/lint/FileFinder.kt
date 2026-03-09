package io.github.ackeecz.danger.lint

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

internal object FileFinder {

    private const val BUILD_DIR_NAME = "build"

    fun findBuildDirs(
        rootDirectoryPath: Path,
        buildFoldersMatcher: BuildFoldersMatcher,
    ): List<File> {
        return when (buildFoldersMatcher) {
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
                val resolvedRoot = rootDirectoryPath.toAbsolutePath().toFile()
                buildFoldersMatcher.paths.map { File(resolvedRoot, it) }
            }
        }
    }

    fun findFiles(
        rootDirectoryPath: Path,
        buildFoldersMatcher: BuildFoldersMatcher,
        reportFilesFolderPath: String,
        filePrefix: String = "",
        fileExtension: String = "xml",
    ): List<File> {
        return findFiles(
            buildDirs = findBuildDirs(rootDirectoryPath, buildFoldersMatcher),
            reportFilesFolderPath = reportFilesFolderPath,
            filePrefix = filePrefix,
            fileExtension = fileExtension,
        )
    }

    fun findFiles(
        buildDirs: List<File>,
        reportFilesFolderPath: String,
        filePrefix: String = "",
        fileExtension: String = "xml",
    ): List<File> {
        return buildDirs
            .flatMap { buildDir ->
                File(buildDir, reportFilesFolderPath)
                    .listFiles()
                    ?.filter { it.extension == fileExtension && it.nameWithoutExtension.startsWith(filePrefix) }
                    .orEmpty()
            }.ifEmpty {
                throw NoFilesFoundException()
            }
    }
}
