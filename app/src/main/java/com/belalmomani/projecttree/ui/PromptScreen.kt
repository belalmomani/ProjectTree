package com.belalmomani.projecttree.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.belalmomani.projecttree.form.FormViewModel

private const val CHATGPT_PKG = "com.openai.chatgpt"
private const val CLAUDE_PKG = "com.anthropic.claude"

@Composable
fun PromptScreen(vm: FormViewModel, onEdit: () -> Unit, onImport: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "البرومبت الجاهز",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "راجع النص وعدّله إن أردت، ثم انسخه أو شاركه مع ChatGPT أو Claude.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = vm.prompt,
            onValueChange = { vm.prompt = it },
            minLines = 12,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    copyToClipboard(context, vm.prompt)
                    toast(context, "تم نسخ البرومبت")
                },
                modifier = Modifier.weight(1f),
            ) { Text("نسخ البرومبت") }
            Button(
                onClick = { share(context, vm.prompt) },
                modifier = Modifier.weight(1f),
            ) { Text("مشاركة") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { openAssistant(context, vm.prompt, CHATGPT_PKG, "https://chatgpt.com") },
                modifier = Modifier.weight(1f),
            ) { Text("فتح ChatGPT") }
            OutlinedButton(
                onClick = { openAssistant(context, vm.prompt, CLAUDE_PKG, "https://claude.ai") },
                modifier = Modifier.weight(1f),
            ) { Text("فتح Claude") }
        }

        Text(
            "عند فتح ChatGPT أو Claude يُنسخ البرومبت تلقائيًا. الصقه في المحادثة وأرسله بنفسك، فالتطبيق لا يرسل أي رسالة نيابة عنك.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
            Text("لدي رد الذكاء الاصطناعي: استيراد الهيكل")
        }
        OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text("تعديل الاختيارات")
        }
    }
}

private fun toast(context: Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("ProjectTree prompt", text))
}

private fun share(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    runCatching { context.startActivity(Intent.createChooser(send, "مشاركة البرومبت")) }
}

private fun openAssistant(context: Context, text: String, pkg: String, webUrl: String) {
    copyToClipboard(context, text)
    val intent = context.packageManager.getLaunchIntentForPackage(pkg)
        ?: Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
    val ok = runCatching { context.startActivity(intent) }.isSuccess
    if (ok) {
        toast(context, "تم نسخ البرومبت. الصقه في المحادثة ثم أرسله")
    } else {
        toast(context, "تعذّر فتح التطبيق. البرومبت منسوخ فالصقه في أي محادثة")
    }
}
