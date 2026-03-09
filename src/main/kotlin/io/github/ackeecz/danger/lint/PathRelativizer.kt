package io.github.ackeecz.danger.lint

import java.nio.file.Path
import java.nio.file.Paths

internal class PathRelativizer(private val workingDirectory: Path = Paths.get("").toAbsolutePath()) {

    fun relativize(filePath: String): String {
        val path = Path.of(filePath)
        return when {
            path.isAbsolute && path.startsWith(workingDirectory) -> workingDirectory.relativize(path).toString()
            path.isAbsolute -> tryRelativizeBySuffixMatch(path) ?: filePath
            else -> filePath
        }
    }

    private fun tryRelativizeBySuffixMatch(filePath: Path): String? {
        if (filePath.nameCount <= workingDirectory.nameCount) return null
        val mismatchCount = (0 until workingDirectory.nameCount).count {
            workingDirectory.getName(it) != filePath.getName(it)
        }
        return filePath.subpath(workingDirectory.nameCount, filePath.nameCount).toString()
            .takeIf { mismatchCount == 1 }
    }
}
