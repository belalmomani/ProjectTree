package com.belalmomani.projecttree.structure

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipExporter {
    private val ILLEGAL = Regex("[\\\\/:*?\"<>|\\u0000-\\u001F]")

    fun safeRootName(name: String): String {
        val cleaned = name.trim().replace(ILLEGAL, "_").trim('.', ' ').take(100)
        return cleaned.ifEmpty { "project" }
    }

    /** يكتب ZIP حقيقيًا: كل الملفات فارغة (0 بايت) وكل المجلدات مُدرجة. */
    fun write(out: OutputStream, rootName: String, nodes: List<Node>) {
        val root = safeRootName(rootName)
        ZipOutputStream(out).use { zip ->
            zip.putNextEntry(ZipEntry("$root/"))
            zip.closeEntry()
            for (n in nodes) {
                val entryName = if (n.isFolder) "$root/${n.path}/" else "$root/${n.path}"
                zip.putNextEntry(ZipEntry(entryName))
                zip.closeEntry()
            }
        }
    }
}
