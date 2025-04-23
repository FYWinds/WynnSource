package fyi.fyw.wynnsource.utils

import java.nio.file.Path

object FileUtils {
    fun writeToFile(path: Path, data: String) {
        if (!path.parent.toFile().exists()) {
            path.parent.toFile().mkdirs()
        }
        path.toFile().writeText(data)
    }

    fun readFromFile(path: Path): String? {
        if (!path.toFile().exists()) {
            return null
        }
        return path.toFile().readText()
    }
}