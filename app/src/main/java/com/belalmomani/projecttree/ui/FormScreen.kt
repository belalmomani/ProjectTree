package com.belalmomani.projecttree.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.belalmomani.projecttree.catalog.AUTO
import com.belalmomani.projecttree.catalog.Catalog
import com.belalmomani.projecttree.catalog.OTHER
import com.belalmomani.projecttree.catalog.ProjectTypes
import com.belalmomani.projecttree.catalog.Question
import com.belalmomani.projecttree.catalog.Section
import com.belalmomani.projecttree.form.FormViewModel

@Composable
fun FormScreen(vm: FormViewModel, onBack: () -> Unit, onContinue: () -> Unit) {
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }
    val type = vm.type
    val visibleSections = Catalog.sections.filter {
        Catalog.visibleQuestions(it, type, vm.selections).isNotEmpty()
    }
    val missing = vm.missingItems()

    LazyColumn(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("رجوع") }
                Text(
                    "مشروع جديد",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        item { BasicsCard(vm) }
        if (type != null) {
            items(visibleSections, key = { it.id }) { sec ->
                SectionCard(
                    sec = sec,
                    vm = vm,
                    expanded = expandedId == sec.id,
                    onToggle = { expandedId = if (expandedId == sec.id) null else sec.id },
                )
            }
            item { DescriptionCard(vm) }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (missing.isNotEmpty()) {
                    Text(
                        "ينقص: " + missing.joinToString("، "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Button(
                    onClick = onContinue,
                    enabled = missing.isEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("متابعة") }
            }
        }
    }
}

@Composable
private fun BasicsCard(vm: FormViewModel) {
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = vm.name,
                onValueChange = { vm.name = it },
                label = { Text("اسم المشروع *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("نوع المشروع *", style = MaterialTheme.typography.titleSmall)
            Box {
                OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (vm.type == null) "اختر نوع المشروع" else ProjectTypes.label(vm.type))
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    ProjectTypes.all.forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                vm.selectType(id)
                                menuOpen = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(sec: Section, vm: FormViewModel, expanded: Boolean, onToggle: () -> Unit) {
    val questions = Catalog.visibleQuestions(sec, vm.type, vm.selections)
    val count = questions.sumOf { vm.selections[it.id]?.size ?: 0 }
    val hasRequired = questions.any { it.required }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = sec.title + if (hasRequired) " *" else "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                if (count > 0) {
                    Text(
                        "$count",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
                Text(if (expanded) "▲" else "▼")
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    questions.forEach { QuestionBlock(it, vm) }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun QuestionBlock(q: Question, vm: FormViewModel) {
    val options = Catalog.visibleOptions(q, vm.type, vm.selections)
    val chosen = vm.selections[q.id] ?: emptySet()
    var query by rememberSaveable(q.id) { mutableStateOf("") }
    val shown = if (query.isBlank()) options
    else options.filter { it.label.contains(query, ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = q.title + if (q.required) " *" else "",
            style = MaterialTheme.typography.titleSmall,
        )
        if (options.size > 8) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("بحث...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            shown.forEach { opt ->
                FilterChip(
                    selected = opt.id in chosen,
                    onClick = { vm.toggle(q, opt.id) },
                    label = { Text(opt.label) },
                )
            }
            if (q.allowAuto) {
                FilterChip(
                    selected = AUTO in chosen,
                    onClick = { vm.toggle(q, AUTO) },
                    label = { Text("اختيار تلقائي") },
                )
            }
            if (q.allowOther) {
                FilterChip(
                    selected = OTHER in chosen,
                    onClick = { vm.toggle(q, OTHER) },
                    label = { Text("أخرى") },
                )
            }
        }
        if (OTHER in chosen) {
            OutlinedTextField(
                value = vm.otherText[q.id] ?: "",
                onValueChange = { vm.otherText[q.id] = it },
                label = { Text("حدّد") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DescriptionCard(vm: FormViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = vm.description,
                onValueChange = { vm.description = it },
                label = { Text("اشرح فكرة مشروعك *") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = vm.notes,
                onValueChange = { vm.notes = it },
                label = { Text("ملاحظات إضافية (اختياري)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
