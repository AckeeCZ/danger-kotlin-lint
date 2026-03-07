package io.github.ackeecz.danger.lint.util

import io.kotest.core.TestConfiguration
import io.kotest.engine.spec.TempDirDeletionException
import io.kotest.engine.spec.TempFileDeletionException
import java.io.File
import kotlin.io.path.createTempDirectory

internal fun loadResourceFile(name: String): File =
    File(ClassLoader.getSystemResource(name).toURI())

internal val TestConfiguration.rootTempTestDir: File
    get() {
        val projectRootDirectory = File(System.getProperty("user.dir"))
        val buildDir = File(projectRootDirectory, "build").also { it.mkdir() }
        return File(buildDir, "temp-test").also { tempDir ->
            tempDir.mkdir()
            afterEach { tempDir.deleteRecursively() }
        }
    }

internal fun TestConfiguration.tempdir(parent: File, prefix: String? = null): File {
    val dir = createTempDirectory(parent.toPath(), prefix ?: javaClass.name).toFile()
    afterEach {
        if (!dir.deleteRecursively()) throw TempDirDeletionException(dir)
    }
    return dir
}

internal fun TestConfiguration.tempfile(dir: File, prefix: String? = null, suffix: String? = null): File {
    val file = kotlin.io.path.createTempFile(dir.toPath(), prefix ?: javaClass.name, suffix).toFile()
    afterEach {
        if (!file.delete()) throw TempFileDeletionException(file)
    }
    return file
}
