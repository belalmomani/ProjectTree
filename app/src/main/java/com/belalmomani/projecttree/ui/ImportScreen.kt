package com.belalmomani.projecttree.ui

import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.belalmomani.projecttree.form.ImportViewModel
import com.belalmomani.projecttree.structure.ZipExporter

@Composable
fun ImportScreen(vm: ImportViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val result = vm.result

    val pickTree = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) vm.createProject(context, uri)
    }
    val createZip = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri != null) vm.exportZip(context, uri)
    }
    val pickJson = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.loadFromUri(context, uri)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("رجوع") }
                Text(
                    "استيراد الهيكل",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("الصق JSON الذي أرجعه الذكاء الاصطناعي", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = vm.json,
                        onValueChange = { vm.json = it },
                        minLines = 8,
                        maxLines = 14,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { pasteFromClipboard(context)?.let { vm.json = it } },
                            modifier = Modifier.weight(1f),
                        ) { Text("لصق") }
                        OutlinedButton(
                            onClick = { pickJson.launch(arrayOf("*/*")) },
                            modifier = Modifier.weight(1f),
                        ) { Text("من ملف") }
                        OutlinedButton(
                            onClick = { vm.clear() },
                            modifier = Modifier.weight(1f),
                        ) { Text("مسح") }
                    }
                    Button(
                        onClick = { vm.analyze() },
                        enabled = vm.json.isNotBlank() && !vm.busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("تحليل المخطط") }
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (result.ok) {
                            Text("المخطط صالح", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text("المجلدات: ${result.folderCount}   |   الملفات: ${result.fileCount}")
                            if (result.meta.name.isNotBlank()) Text("المشروع: ${result.meta.name}")
                            if (result.meta.type.isNotBlank()) Text("النوع: ${result.meta.type}")
                            if (result.meta.platform.isNotBlank()) Text("المنصة: ${result.meta.platform}")
                        } else {
                            Text("المخطط غير صالح، لن يُنشأ شيء", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                            result.errors.take(30).forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                            if (result.errors.size > 30) Text("... و${result.errors.size - 30} أخطاء أخرى")
                            Text(
                                "عدّل النص أعلاه ثم اضغط «تحليل المخطط» مرة أخرى.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        result.warnings.forEach {
                            Text("تنبيه: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (result.ok) {
                item { Text("معاينة الشجرة", style = MaterialTheme.typography.titleSmall) }
                items(result.nodes, key = { it.path }) { n ->
                    Column(Modifier.padding(start = (n.depth * 16).dp)) {
                        Text((if (n.isFolder) "📁 " else "📄 ") + n.name)
                        if (n.purpose.isNotBlank()) {
                            Text(
                                n.purpose,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = vm.rootName,
                                onValueChange = { vm.rootName = it },
                                label = { Text("اسم مجلد المشروع") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = vm.overwrite, onCheckedChange = { vm.overwrite = it })
                                Text("السماح بإفراغ الملفات الموجودة بنفس المسار (الافتراضي: تخطيها)")
                            }
                            Text(
                                "كل الملفات تُنشأ فارغة تمامًا. لا يُكتب أي كود أو نص داخلها.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Button(
                                onClick = { pickTree.launch(null) },
                                enabled = !vm.busy,
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("إنشاء المشروع (اختيار مكان الحفظ)") }
                            OutlinedButton(
                                onClick = {
                                    createZip.launch(zipFileName(vm.rootName, result.meta.name))
                                },
                                enabled = !vm.busy,
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("تصدير ZIP") }
                        }
                    }
                }
            }
        }

        if (vm.busy) {
            item { Text("جارٍ التنفيذ...", color = MaterialTheme.colorScheme.primary) }
        }
        vm.report?.let { rep ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Text(rep, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

private fun zipFileName(rootName: String, metaName: String): String {
    val base = ZipExporter.safeRootName(rootName.ifBlank { metaName })
    return "$base.zip"
}

private fun pasteFromClipboard(context: Context): String? {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = cm.primaryClip ?: return null
    if (clip.itemCount == 0) return null
    return clip.getItemAt(0).coerceToText(context)?.toString()
}
