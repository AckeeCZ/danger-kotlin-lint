package io.github.ackeecz.danger.detekt

import io.github.ackeecz.danger.detekt.util.rootTempTestDir
import io.github.ackeecz.danger.detekt.util.tempdir
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.TestConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import java.io.File
import java.nio.file.Paths

private lateinit var underTest: FileFinder

internal class FileFinderTest : FunSpec({

    beforeEach {
        underTest = FileFinder
    }

    // Detekt creates report files with names other than "detekt.xml" for Detekt runs with type resolution,
    // e.g. debug.xml for detektDebug task.
    test("find any *.xml files in reports/detekt folder with default config") {
        // Arrange
        val detektDir = createDetektDir()
        File(detektDir, "debug.html").also { it.createNewFile() }
        val expectedFiles = listOf(
            File(detektDir, "debug.xml").also { it.createNewFile() },
            File(detektDir, "beta.xml").also { it.createNewFile() },
        )

        // Act
        val actualFiles = underTest.findFiles(
            rootDirectoryPath = rootTempTestDir.toPath(),
            config = DetektPlugin.Config.FileDiscovery(),
        )

        // Assert
        actualFiles.shouldContainExactlyInAnyOrder(expectedFiles)
    }

    test("find any *.xml files in custom folder with custom config") {
        // Arrange
        val customDetektDirPath = "custom/more-custom"
        val detektDir = createDetektDir(detektDirPath = customDetektDirPath)
        File(detektDir, "debug.html").also { it.createNewFile() }
        val expectedFiles = listOf(
            File(detektDir, "debug.xml").also { it.createNewFile() },
            File(detektDir, "beta.xml").also { it.createNewFile() },
        )

        // Act
        val actualFiles = underTest.findFiles(
            rootDirectoryPath = rootTempTestDir.toPath(),
            config = DetektPlugin.Config.FileDiscovery(detektFolderPath = customDetektDirPath),
        )

        // Assert
        actualFiles.shouldContainExactlyInAnyOrder(expectedFiles)
    }

    test("fail when no files found") {
        shouldThrow<NoFilesFoundException> {
            underTest.findFiles(
                rootDirectoryPath = tempdir(rootTempTestDir, "detekt").toPath(),
                config = DetektPlugin.Config.FileDiscovery(),
            )
        }
    }

    test("do not fail when folder without parent is passed as root directory for search") {
        shouldNotThrow<NullPointerException> {
            try {
                underTest.findFiles(
                    rootDirectoryPath = Paths.get(""),
                    config = DetektPlugin.Config.FileDiscovery(),
                )
            } catch (_: NoFilesFoundException) {
                // Might be thrown because we check real build folder here and there might be no detek reports
            }
        }
    }

    test("search only inside build folders and ignore others") {
        // Arrange
        val detektDir = createDetektDir()
        val expectedFiles = listOf(File(detektDir, "detekt.xml").also { it.createNewFile() })

        val otherDir = createDetektDir(buildDirName = "other")
        File(otherDir, "other.xml").also { it.createNewFile() }

        // Act
        val actualFiles = underTest.findFiles(
            rootDirectoryPath = rootTempTestDir.toPath(),
            config = DetektPlugin.Config.FileDiscovery(),
        )

        // Assert
        actualFiles shouldHaveSize expectedFiles.size
        actualFiles.shouldContainExactlyInAnyOrder(expectedFiles)
    }

    test("search in all build folders by default") {
        val expectedFiles = listOf("module1", "module2", "module3").map { moduleDirName ->
            File(createDetektDir(moduleDirName = moduleDirName), "$moduleDirName.xml").also { it.createNewFile() }
        }

        val actualFiles = underTest.findFiles(
            rootDirectoryPath = rootTempTestDir.toPath(),
            config = DetektPlugin.Config.FileDiscovery(),
        )

        actualFiles shouldHaveSize expectedFiles.size
        actualFiles.shouldContainExactlyInAnyOrder(expectedFiles)
    }

    test("search in specific build folders if configured") {
        val expectedModules = listOf("module1", "module2")
        val expectedBuildDirs = expectedModules.map { "$it/build" }
        val expectedFiles = expectedModules.map { moduleDirName ->
            File(createDetektDir(moduleDirName = moduleDirName), "$moduleDirName.xml").also { it.createNewFile() }
        }
        File(createDetektDir(moduleDirName = "module3"), "module3.xml").also { it.createNewFile() }

        val actualFiles = underTest.findFiles(
            rootDirectoryPath = rootTempTestDir.toPath(),
            config = DetektPlugin.Config.FileDiscovery(
                buildFoldersMatcher = BuildFoldersMatcher.Specific(*expectedBuildDirs.toTypedArray()),
            ),
        )

        actualFiles shouldHaveSize expectedFiles.size
        actualFiles.shouldContainExactlyInAnyOrder(expectedFiles)
    }
})

private fun TestConfiguration.createDetektDir(
    moduleDirName: String = "app",
    buildDirName: String = "build",
    detektDirPath: String = "reports/detekt",
): File {
    val moduleDir = File(rootTempTestDir, moduleDirName)
    val buildDir = File(moduleDir, buildDirName)
    return File(buildDir, detektDirPath).also { it.mkdirs() }
}
