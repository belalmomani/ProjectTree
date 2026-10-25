package com.belalmomani.projecttree.form

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belalmomani.projecttree.structure.ParseResult
import com.belalmomani.projecttree.structure.StructureParser
import com.belalmomani.projecttree.structure.StructureWriter
import com.belalmomani.projecttree.structure.ZipExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImportViewModel : ViewModel() {
    var json by mutableStateOf("")
    var result by mutableStateOf<ParseResult?>(null)
    var rootName by mutableStateOf("")
    var overwrite by mutableStateOf(false)
    var busy by mutableStateOf(false)
    var report by mutableStateOf<String?>(null)

    fun analyze() {
        report = null
        val r = StructureParser.parse(json)
        result = r
        if (rootName.isBlank()) rootName = r.meta.name
    }

    fun clear() {
        json = ""
        result = null
        rootName = ""
        overwrite = false
        report = null
    }

    fun loadFromUri(context: Context, uri: Uri) {
        val app = context.applicationContext
        viewModelScope.launch {
            busy = true
            val text = withContext(Dispatchers.IO) {
                runCatching {
                    app.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                }.getOrNull()
            }
            busy = false
            if (text == null) {
                report = "تعذّرت قراءة الملف."
            } else {
                json = text
                analyze()
            }
        }
    }

    fun createProject(context: Context, treeUri: Uri) {
        val r = result ?: return
        if (!r.ok) return
        val app = context.applicationContext
        val name = rootName.ifBlank { r.meta.name }
        val over = overwrite
        viewModelScope.launch {
            busy = true
            report = null
            val rep = withContext(Dispatchers.IO) {
                runCatching { StructureWriter.create(app, treeUri, name, r.nodes, over) }
            }
            busy = false
            report = rep.fold(
                onSuccess = { c ->
                    buildString {
                        append("تم الإنشاء.\n")
                        append("مجلدات جديدة: ${c.foldersCreated}\n")
                        append("ملفات جديدة (فارغة): ${c.filesCreated}\n")
                        if (c.filesOverwritten > 0) append("ملفات أُفرغت (استبدال): ${c.filesOverwritten}\n")
                        if (c.skippedExisting > 0) append("ملفات موجودة تم تخطيها: ${c.skippedExisting}\n")
                        if (c.errors.isNotEmpty()) {
                            append("أخطاء (${c.errors.size}):\n")
                            c.errors.take(20).forEach { append("• $it\n") }
                        }
                    }.trim()
                },
                onFailure = { "فشل الإنشاء: ${it.message}" },
            )
        }
    }

    fun exportZip(context: Context, uri: Uri) {
        val r = result ?: return
        if (!r.ok) return
        val app = context.applicationContext
        val name = rootName.ifBlank { r.meta.name }
        viewModelScope.launch {
            busy = true
            report = null
            val res = withContext(Dispatchers.IO) {
                runCatching {
                    val out = app.contentResolver.openOutputStream(uri)
                        ?: error("تعذّر فتح ملف الوجهة")
                    ZipExporter.write(out, name, r.nodes)
                }
            }
            busy = false
            report = res.fold(
                onSuccess = { "تم تصدير ZIP: ${r.folderCount} مجلد، ${r.fileCount} ملف (كلها فارغة)." },
                onFailure = { "فشل التصدير: ${it.message}" },
            )
        }
    }
}
