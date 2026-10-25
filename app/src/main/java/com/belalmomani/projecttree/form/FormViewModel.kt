package com.belalmomani.projecttree.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.belalmomani.projecttree.catalog.AUTO
import com.belalmomani.projecttree.catalog.Catalog
import com.belalmomani.projecttree.catalog.OTHER
import com.belalmomani.projecttree.catalog.Question
import com.belalmomani.projecttree.catalog.SelectionMode

class FormViewModel : ViewModel() {
    var name by mutableStateOf("")
    var type by mutableStateOf<String?>(null)
        private set
    var description by mutableStateOf("")
    var notes by mutableStateOf("")

    /** questionId -> معرّفات الخيارات المختارة */
    val selections = mutableStateMapOf<String, Set<String>>()

    /** questionId -> نص خيار «أخرى» */
    val otherText = mutableStateMapOf<String, String>()

    fun reset() {
        name = ""
        type = null
        description = ""
        notes = ""
        selections.clear()
        otherText.clear()
    }

    fun selectType(id: String) {
        if (type == id) return
        type = id
        prune()
    }

    fun toggle(q: Question, optionId: String) {
        val current = selections[q.id] ?: emptySet()
        val exclusiveIds = q.options.filter { it.exclusive }.map { it.id }.toSet() + AUTO
        val next: Set<String> = when {
            q.mode == SelectionMode.SINGLE ->
                if (optionId in current) emptySet() else setOf(optionId)
            optionId in current -> current - optionId
            optionId in exclusiveIds -> setOf(optionId)
            else -> (current - exclusiveIds) + optionId
        }
        if (next.isEmpty()) selections.remove(q.id) else selections[q.id] = next
        prune()
    }

    /** يزيل الاختيارات التي لم تعد متوافقة مع النوع أو اللغة المختارة. */
    private fun prune() {
        repeat(3) {
            for (sec in Catalog.sections) for (q in sec.questions) {
                val current = selections[q.id] ?: continue
                val allowed = Catalog.visibleOptions(q, type, selections).map { it.id }.toSet() + AUTO + OTHER
                val kept = current.filter { it in allowed }.toSet()
                if (kept.size != current.size) {
                    if (kept.isEmpty()) selections.remove(q.id) else selections[q.id] = kept
                }
            }
        }
    }

    fun missingItems(): List<String> = buildList {
        if (name.isBlank()) add("اسم المشروع")
        val t = type
        if (t == null) {
            add("نوع المشروع")
        } else {
            for (sec in Catalog.sections) for (q in Catalog.visibleQuestions(sec, t, selections)) {
                if (q.required && selections[q.id].isNullOrEmpty()) add(q.title)
            }
        }
        if (description.isBlank()) add("وصف المشروع")
    }
}
