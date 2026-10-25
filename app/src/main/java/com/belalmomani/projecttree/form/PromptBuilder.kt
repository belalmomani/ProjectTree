package com.belalmomani.projecttree.form

import com.belalmomani.projecttree.catalog.AUTO
import com.belalmomani.projecttree.catalog.Catalog
import com.belalmomani.projecttree.catalog.OTHER
import com.belalmomani.projecttree.catalog.ProjectTypes

/** يبني البرومبت النهائي: المقدمة الثابتة + مخطط JSON + اختيارات المستخدم. */
object PromptBuilder {

    private val INTRO = """
أنت مهندس برمجيات محترف ومتخصص في تصميم الهياكل التنظيمية للمشاريع البرمجية.

أريد منك إنشاء الهيكل التنظيمي الكامل والدقيق والاحترافي للمشروع الموصوف أدناه، وفق التقنيات والميزات التي حددتها.

مهمتك هي التخطيط البنيوي فقط، وليست برمجة المشروع.

أخرج الهيكل التنظيمي فقط، دون مقدمة أو خاتمة أو شرح أو نصائح أو تعليقات خارج بيانات المخطط.

لا تكتب الكود التنفيذي داخل ملفات المصدر.

حدّد جميع المجلدات والملفات اللازمة، مع مساراتها النسبية، ونوع كل عنصر، والغرض منه، وحالة الملف، والتبعيات الضرورية عند الحاجة.

ضمّن ملفات الإعداد والموارد والاختبارات الضرورية وفق التقنية المختارة.

تجنب الملفات الزائدة والتكرار والتعقيد غير الضروري.

التزم بمتطلبات المشروع، ولا تضف ميزات غير مطلوبة.

أخرج النتيجة بصيغة JSON صحيحة ومتوافقة مع مخطط بيانات ثابت يستطيع تطبيق آخر قراءته لإنشاء المجلدات والملفات تلقائيًا.

لا تضع أي نص خارج JSON، ولا تستخدم Markdown أو أسوار كود.

إذا كانت هناك معلومة غير محددة، فاختر الافتراض التقني الأبسط والأنسب، وسجّله ضمن بيانات المخطط عند الضرورة، دون تحويل الرد إلى شرح أو حوار.

لا تدّعِ أن المشروع قابل للبناء أو التشغيل قبل اختباره فعليًا.
""".trim()

    private val SCHEMA = """
مخطط JSON المطلوب (استخدم هذه المفاتيح بالضبط وبالإنجليزية):
{
  "schemaVersion": 1,
  "projectName": "",
  "projectType": "",
  "platform": "",
  "languages": [],
  "frameworks": [],
  "database": [],
  "services": [],
  "assumptions": [],
  "items": [
    {
      "path": "app/src/main/Example.kt",
      "type": "file",
      "purpose": "",
      "status": "empty",
      "dependencies": []
    }
  ]
}

قواعد المخطط:
- "type" إما "folder" أو "file".
- "status" إما "empty" (فارغ) أو "starter" (قالب أولي) أو "config" (ملف إعداد). للمجلدات استخدم "empty".
- المسارات نسبية وتستخدم "/" ولا تبدأ بـ "/" ولا تحتوي ".." ولا حرف القرص.
- لا تكرر المسار، ولا تجعل الاسم الواحد مجلدًا وملفًا في آن واحد.
- ضع كل مجلد في القائمة قبل الملفات التي بداخله.
- لا تضف حقل content ولا أي كود أو نص داخل الملفات؛ التطبيق ينشئ كل الملفات فارغة.
- "dependencies" قائمة مسارات عناصر أخرى في المخطط أو أسماء مكتبات، وقد تكون فارغة.
""".trim()

    fun build(vm: FormViewModel): String {
        val type = vm.type
        val sb = StringBuilder()
        sb.append(INTRO).append("\n\n").append(SCHEMA).append("\n\n")
        sb.append("بيانات المشروع:\n")
        sb.append("- اسم المشروع: ").append(vm.name.trim()).append('\n')
        sb.append("- نوع المشروع: ").append(ProjectTypes.label(type)).append('\n')

        for (sec in Catalog.sections) {
            val lines = mutableListOf<String>()
            for (q in Catalog.visibleQuestions(sec, type, vm.selections)) {
                val chosen = vm.selections[q.id] ?: continue
                val labels = mutableListOf<String>()
                for (opt in q.options) if (opt.id in chosen) labels.add(opt.label)
                if (AUTO in chosen) labels.add("اختيار تلقائي (اختر الأبسط والأنسب)")
                if (OTHER in chosen) {
                    val txt = vm.otherText[q.id]?.trim().orEmpty()
                    labels.add(if (txt.isEmpty()) "أخرى" else "أخرى: $txt")
                }
                if (labels.isNotEmpty()) lines.add("  • ${q.title}: ${labels.joinToString("، ")}")
            }
            if (lines.isNotEmpty()) {
                sb.append("\n").append(sec.title).append(":\n")
                lines.forEach { sb.append(it).append('\n') }
            }
        }

        sb.append("\nفكرة المشروع:\n").append(vm.description.trim()).append('\n')
        if (vm.notes.isNotBlank()) {
            sb.append("\nملاحظات إضافية:\n").append(vm.notes.trim()).append('\n')
        }
        return sb.toString().trim()
    }
}
