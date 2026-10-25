package com.belalmomani.projecttree.structure

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

data class CreateReport(
    val foldersCreated: Int,
    val filesCreated: Int,
    val filesOverwritten: Int,
    val skippedExisting: Int,
    val errors: List<String>,
)

object StructureWriter {

    /** ينشئ الهيكل داخل المجلد المختار. الملفات فارغة دائمًا. لا يكتب فوق موجود إلا بموافقة (overwrite). */
    fun create(
        context: Context,
        treeUri: Uri,
        rootName: String,
        nodes: List<Node>,
        overwrite: Boolean,
    ): CreateReport {
        val base = DocumentFile.fromTreeUri(context, treeUri)
            ?: return CreateReport(0, 0, 0, 0, listOf("تعذّر فتح المجلد المختار."))

        val errors = mutableListOf<String>()
        var folders = 0
        var files = 0
        var overwritten = 0
        var skipped = 0

        val cache = HashMap<String, MutableMap<String, DocumentFile>>()
        fun children(dir: DocumentFile): MutableMap<String, DocumentFile> =
            cache.getOrPut(dir.uri.toString()) {
                val m = HashMap<String, DocumentFile>()
                for (f in dir.listFiles()) {
                    val n = f.name
                    if (n != null) m[n] = f
                }
                m
            }

        val rootDirName = ZipExporter.safeRootName(rootName)
        val existingRoot = children(base)[rootDirName]
        if (existingRoot != null && !existingRoot.isDirectory) {
            return CreateReport(0, 0, 0, 0, listOf("يوجد ملف بنفس اسم مجلد المشروع: $rootDirName"))
        }
        val rootDoc: DocumentFile
        if (existingRoot != null) {
            rootDoc = existingRoot
        } else {
            val createdRoot = base.createDirectory(rootDirName)
                ?: return CreateReport(0, 0, 0, 0, listOf("تعذّر إنشاء مجلد المشروع في المكان المختار."))
            children(base)[rootDirName] = createdRoot
            rootDoc = createdRoot
            folders++
        }

        val dirs = HashMap<String, DocumentFile>()
        dirs[""] = rootDoc

        for (n in nodes) {
            try {
                val parentPath = n.path.substringBeforeLast('/', "")
                val name = n.name
                val parent = dirs[parentPath]
                if (parent == null) {
                    errors.add("لا يوجد مجلد أب للعنصر: ${n.path}")
                    continue
                }
                val existing = children(parent)[name]
                if (n.isFolder) {
                    if (existing != null) {
                        if (existing.isDirectory) {
                            dirs[n.path] = existing
                        } else {
                            errors.add("يوجد ملف بنفس اسم المجلد: ${n.path}")
                        }
                    } else {
                        val created = parent.createDirectory(name)
                        if (created == null) {
                            errors.add("تعذّر إنشاء المجلد: ${n.path}")
                        } else {
                            children(parent)[name] = created
                            dirs[n.path] = created
                            folders++
                        }
                    }
                } else {
                    if (existing != null) {
                        if (existing.isDirectory) {
                            errors.add("يوجد مجلد بنفس اسم الملف: ${n.path}")
                        } else if (overwrite) {
                            context.contentResolver.openOutputStream(existing.uri, "wt")?.close()
                            overwritten++
                        } else {
                            skipped++
                        }
                    } else {
                        val created = parent.createFile("application/octet-stream", name)
                        if (created == null) {
                            errors.add("تعذّر إنشاء الملف: ${n.path}")
                        } else {
                            if (created.name != name) created.renameTo(name)
                            children(parent)[name] = created
                            if (created.length() != 0L) {
                                errors.add("الملف ليس فارغًا: ${n.path}")
                            }
                            files++
                        }
                    }
                }
            } catch (e: Exception) {
                errors.add("${n.path}: ${e.message}")
            }
        }
        return CreateReport(folders, files, overwritten, skipped, errors)
    }
}
