package com.belalmomani.projecttree.structure

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class Node(
    val path: String,
    val isFolder: Boolean,
    val purpose: String = "",
    val implicit: Boolean = false,
) {
    val name: String get() = path.substringAfterLast('/')
    val depth: Int get() = path.count { it == '/' }
}

data class ProjectMeta(
    val name: String = "",
    val type: String = "",
    val platform: String = "",
)

data class ParseResult(
    val meta: ProjectMeta,
    val nodes: List<Node>,
    val errors: List<String>,
    val warnings: List<String>,
) {
    val ok: Boolean get() = errors.isEmpty() && nodes.isNotEmpty()
    val folderCount: Int get() = nodes.count { it.isFolder }
    val fileCount: Int get() = nodes.count { !it.isFolder }
}

/**
 * يحلّل JSON القادم من الذكاء الاصطناعي كبيانات غير موثوقة.
 * لا ينفّذ شيئًا ولا يقرأ محتوى الملفات؛ حقل content يُتجاهل دائمًا.
 */
object StructureParser {
    const val MAX_NODES = 5000
    const val MAX_DEPTH = 30

    private val ITEM_KEYS = listOf("items", "structure", "files", "tree", "nodes")
    private val FOLDER_TYPES = setOf("folder", "directory", "dir")
    private val BAD_CHARS = Regex("[\\u0000-\\u001F:*?\"<>|\\\\]")
    private val DRIVE = Regex("^[A-Za-z]:")

    private class Raw(val path: String, val isFolder: Boolean, val purpose: String)

    private class State {
        val raws = mutableListOf<Raw>()
        val errors = mutableListOf<String>()
        var contentIgnored = false
    }

    private fun fail(msg: String) =
        ParseResult(ProjectMeta(), emptyList(), listOf(msg), emptyList())

    private fun str(obj: JSONObject, key: String): String =
        if (obj.isNull(key)) "" else obj.optString(key, "")

    fun parse(raw: String): ParseResult {
        val warnings = mutableListOf<String>()
        var text = raw.trim().removePrefix("\uFEFF").trim()
        if (text.isEmpty()) return fail("النص فارغ. الصق JSON أولًا.")

        if (text.startsWith("```")) {
            text = text.removePrefix("```").substringAfter('\n', "").removeSuffix("```").trim()
            warnings.add("تمت إزالة أسوار Markdown حول JSON.")
        }
        if (text.isEmpty()) return fail("لا يوجد JSON داخل النص.")

        var meta = ProjectMeta()
        var itemsArray: JSONArray? = null
        try {
            if (text.startsWith("[")) {
                itemsArray = JSONArray(text)
            } else if (text.startsWith("{")) {
                val root = JSONObject(text)
                meta = ProjectMeta(
                    name = str(root, "projectName").ifEmpty { str(root, "name") },
                    type = str(root, "projectType"),
                    platform = str(root, "platform"),
                )
                itemsArray = ITEM_KEYS.firstNotNullOfOrNull { root.optJSONArray(it) }
            } else {
                return fail("النص ليس JSON. يجب أن يبدأ بـ { أو [")
            }
        } catch (e: JSONException) {
            return fail("JSON غير صالح: ${e.message}")
        }

        val finalItems = itemsArray
        if (finalItems == null) {
            return fail("لا توجد قائمة عناصر. المطلوب مفتاح items يحتوي المجلدات والملفات.")
        }

        val state = State()
        collect(finalItems, "", 0, state)
        if (state.contentIgnored) {
            warnings.add("تم تجاهل حقل content: كل الملفات تُنشأ فارغة.")
        }

        // تجميع العناصر مع المجلدات الأب الضمنية وكشف التكرار والتعارض
        val seen = LinkedHashMap<String, Node>()
        val explicit = HashSet<String>()
        val errors = state.errors
        for (r in state.raws) {
            val parts = r.path.split('/')
            var acc = ""
            for (i in 0 until parts.size - 1) {
                acc = if (acc.isEmpty()) parts[i] else acc + "/" + parts[i]
                val ex = seen[acc]
                if (ex == null) {
                    seen[acc] = Node(acc, true, implicit = true)
                } else if (!ex.isFolder) {
                    errors.add("تعارض: '$acc' ملف لكن بداخله عناصر.")
                }
            }
            val existing = seen[r.path]
            if (existing == null) {
                seen[r.path] = Node(r.path, r.isFolder, r.purpose)
                explicit.add(r.path)
            } else if (r.path in explicit) {
                errors.add("مسار مكرر: ${r.path}")
            } else if (existing.isFolder && r.isFolder) {
                seen[r.path] = Node(r.path, true, r.purpose)
                explicit.add(r.path)
            } else {
                errors.add("تعارض بين ملف ومجلد: ${r.path}")
            }
        }

        if (errors.isEmpty() && seen.isEmpty()) {
            errors.add("لا توجد عناصر قابلة للإنشاء.")
        }
        return ParseResult(meta, seen.values.toList(), errors.distinct(), warnings)
    }

    private fun collect(arr: JSONArray, parent: String, depth: Int, state: State) {
        if (depth > MAX_DEPTH) {
            state.errors.add("العمق أكبر من المسموح ($MAX_DEPTH).")
            return
        }
        for (i in 0 until arr.length()) {
            if (state.raws.size >= MAX_NODES) {
                state.errors.add("عدد العناصر أكبر من المسموح ($MAX_NODES).")
                return
            }
            val el = arr.opt(i)
            if (el !is JSONObject) {
                state.errors.add("العنصر رقم ${i + 1} ليس كائنًا.")
                continue
            }
            val seg = if (parent.isEmpty()) {
                str(el, "path").ifEmpty { str(el, "name") }
            } else {
                str(el, "name").ifEmpty { str(el, "path") }
            }
            if (seg.isBlank()) {
                state.errors.add("العنصر رقم ${i + 1} بلا مسار.")
                continue
            }
            val children = el.optJSONArray("children")
            val typeStr = str(el, "type").trim().lowercase()
            val isFolder: Boolean? = when {
                typeStr in FOLDER_TYPES -> true
                typeStr == "file" -> false
                typeStr.isEmpty() && children != null -> true
                else -> null
            }
            if (isFolder == null) {
                state.errors.add(
                    if (typeStr.isEmpty()) "العنصر '$seg' بلا نوع (folder أو file)."
                    else "نوع غير معروف '$typeStr' للعنصر '$seg'."
                )
                continue
            }
            if (!isFolder && children != null) {
                state.errors.add("الملف '$seg' لا يمكن أن يحتوي children.")
                continue
            }
            if (el.has("content")) state.contentIgnored = true

            val full = if (parent.isEmpty()) seg else "$parent/$seg"
            val norm = normalize(full, state.errors) ?: continue
            state.raws.add(Raw(norm, isFolder, str(el, "purpose")))
            if (children != null) collect(children, norm, depth + 1, state)
        }
    }

    /** يعيد المسار منظّفًا أو null مع إضافة الخطأ. */
    fun normalize(path: String, errors: MutableList<String>): String? {
        val t = path.trim()
        if (t.startsWith("/") || t.startsWith("\\") || DRIVE.containsMatchIn(t)) {
            errors.add("مسار مطلق مرفوض: $t")
            return null
        }
        if (BAD_CHARS.containsMatchIn(t)) {
            errors.add("المسار يحتوي أحرفًا غير مسموحة: $t")
            return null
        }
        val segs = t.split('/').filter { it.isNotEmpty() && it != "." }
        if (segs.isEmpty()) {
            errors.add("مسار فارغ.")
            return null
        }
        if (segs.any { it == ".." }) {
            errors.add("مسار يخرج من مجلد المشروع مرفوض: $t")
            return null
        }
        if (segs.size > MAX_DEPTH || segs.any { it.length > 255 }) {
            errors.add("المسار طويل جدًا: $t")
            return null
        }
        return segs.joinToString("/")
    }
}
