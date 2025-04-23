package fyi.fyw.wynnsource.utils

object StringUtils {
    fun splitByCodePoint(str: String): Array<String> {
        return str.codePoints().toArray().let { codepoints ->
            Array(codepoints.size) { index -> String(codepoints, index, 1) }
        }
    }
}