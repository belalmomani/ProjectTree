package com.belalmomani.projecttree.catalog

private fun s(vararg x: String): Set<String> = x.toSet()

private val ALL = ProjectTypes.all.map { it.first }.toSet()
private val MOBILE = s(T_AND, T_IOS, T_CROSS, T_AGAME)
private val GAMES = s(T_AGAME, T_PCGAME, T_WGAME)
private val WEBLIKE = s(T_WEB, T_SHOP, T_ADMIN, T_WGAME, T_EXT)
private val SERVER = s(T_API, T_SHOP, T_ADMIN, T_WEB, T_BOT)
private val NOTOOL = ALL - T_TOOL

private fun o(
    id: String,
    label: String,
    types: Set<String>? = null,
    langs: Set<String>? = null,
    ex: Boolean = false,
) = Option(id, label, types, langs, ex)

object Catalog {

    // ---------- 1) اللغات والتقنيات ----------
    private val lang = Question(
        id = "lang", title = "لغة البرمجة", mode = SelectionMode.MULTI,
        required = true, allowAuto = true, allowOther = true,
        options = listOf(
            o("kotlin", "Kotlin", s(T_AND, T_AGAME, T_API, T_DESK, T_TOOL)),
            o("java", "Java", s(T_AND, T_AGAME, T_PCGAME, T_API, T_DESK, T_BOT, T_TOOL)),
            o("swift", "Swift", s(T_IOS)),
            o("dart", "Dart", s(T_CROSS)),
            o("ts", "TypeScript", s(T_CROSS, T_WEB, T_SHOP, T_ADMIN, T_WGAME, T_EXT, T_API, T_BOT, T_TOOL, T_DESK)),
            o("js", "JavaScript", s(T_CROSS, T_WEB, T_SHOP, T_ADMIN, T_WGAME, T_EXT, T_API, T_BOT, T_TOOL, T_DESK)),
            o("html_css", "HTML وCSS", s(T_WEB, T_SHOP, T_ADMIN, T_WGAME, T_EXT)),
            o("csharp", "C#", s(T_AGAME, T_PCGAME, T_DESK, T_API, T_BOT, T_TOOL)),
            o("cpp", "C++", s(T_AGAME, T_PCGAME, T_DESK, T_TOOL)),
            o("gdscript", "GDScript", s(T_AGAME, T_PCGAME, T_WGAME)),
            o("python", "Python", s(T_PCGAME, T_WEB, T_SHOP, T_ADMIN, T_API, T_DESK, T_BOT, T_TOOL)),
            o("php", "PHP", s(T_WEB, T_SHOP, T_ADMIN, T_API)),
            o("go", "Go", s(T_API, T_BOT, T_TOOL)),
        ),
    )

    private val framework = Question(
        id = "framework", title = "إطار العمل / واجهة المستخدم", mode = SelectionMode.SINGLE,
        allowAuto = true, allowOther = true,
        options = listOf(
            o("compose", "Jetpack Compose", s(T_AND), s("kotlin")),
            o("views", "Android Views (XML)", s(T_AND), s("kotlin", "java")),
            o("swiftui", "SwiftUI", s(T_IOS), s("swift")),
            o("uikit", "UIKit", s(T_IOS), s("swift")),
            o("flutter", "Flutter", s(T_CROSS), s("dart")),
            o("react_native", "React Native", s(T_CROSS), s("ts", "js")),
            o("react", "React", s(T_WEB, T_SHOP, T_ADMIN, T_EXT), s("ts", "js")),
            o("nextjs", "Next.js", s(T_WEB, T_SHOP, T_ADMIN), s("ts", "js")),
            o("vue", "Vue", s(T_WEB, T_SHOP, T_ADMIN, T_EXT), s("ts", "js")),
            o("vanilla", "بدون إطار (HTML/CSS/JS خالص)", s(T_WEB, T_SHOP, T_ADMIN, T_EXT, T_WGAME)),
            o("laravel", "Laravel", s(T_API, T_WEB, T_SHOP, T_ADMIN), s("php")),
            o("django", "Django", s(T_API, T_WEB, T_SHOP, T_ADMIN), s("python")),
            o("fastapi", "FastAPI", s(T_API, T_BOT, T_ADMIN), s("python")),
            o("flask", "Flask", s(T_API, T_WEB, T_BOT), s("python")),
            o("express", "Node.js وExpress", s(T_API, T_WEB, T_SHOP, T_ADMIN, T_BOT), s("ts", "js")),
            o("spring", "Spring Boot", s(T_API, T_SHOP, T_ADMIN), s("java", "kotlin")),
            o("aspnet", "ASP.NET Core", s(T_API, T_WEB, T_SHOP, T_ADMIN), s("csharp")),
            o("gin", "Gin / Fiber", s(T_API, T_BOT), s("go")),
            o("pyqt", "PyQt", s(T_DESK), s("python")),
            o("tkinter", "Tkinter", s(T_DESK), s("python")),
            o("wpf", "WPF / WinForms", s(T_DESK), s("csharp")),
            o("javafx", "JavaFX", s(T_DESK), s("java")),
            o("qt", "Qt", s(T_DESK), s("cpp")),
            o("electron", "Electron", s(T_DESK), s("ts", "js")),
            o("bot_lib", "مكتبة بوت (Telegram / Discord ...)", s(T_BOT)),
            o("cli", "أداة سطر أوامر (CLI)", s(T_TOOL)),
        ),
    )

    private val engine = Question(
        id = "engine", title = "محرك اللعبة", mode = SelectionMode.SINGLE,
        types = GAMES, allowAuto = true, allowOther = true,
        options = listOf(
            o("unity", "Unity", s(T_AGAME, T_PCGAME, T_WGAME), s("csharp")),
            o("godot", "Godot", s(T_AGAME, T_PCGAME, T_WGAME), s("gdscript", "csharp")),
            o("unreal", "Unreal Engine", s(T_AGAME, T_PCGAME), s("cpp")),
            o("phaser", "Phaser", s(T_WGAME), s("ts", "js")),
            o("libgdx", "libGDX", s(T_AGAME, T_PCGAME), s("java", "kotlin")),
            o("pygame", "Pygame", s(T_PCGAME), s("python")),
            o("no_engine", "بدون محرك"),
        ),
    )

    // ---------- 2) قاعدة البيانات والتخزين ----------
    private val db = Question(
        id = "db", title = "قاعدة البيانات", mode = SelectionMode.MULTI,
        allowAuto = true, allowOther = true,
        options = listOf(
            o("db_none", "بدون قاعدة بيانات", ex = true),
            o("db_local", "تخزين محلي"),
            o("sqlite", "SQLite", MOBILE + s(T_DESK, T_TOOL, T_PCGAME)),
            o("room", "Room", s(T_AND, T_AGAME), s("kotlin", "java")),
            o("datastore", "DataStore", s(T_AND, T_AGAME), s("kotlin")),
            o("firestore", "Firebase Firestore", MOBILE + WEBLIKE),
            o("rtdb", "Firebase Realtime Database", MOBILE + WEBLIKE),
            o("supabase_pg", "Supabase وPostgreSQL"),
            o("mysql", "MySQL وMariaDB", SERVER + s(T_DESK)),
            o("postgres", "PostgreSQL", SERVER + s(T_DESK)),
            o("mongodb", "MongoDB", SERVER),
            o("server_db", "قاعدة بيانات على الخادم", MOBILE + s(T_DESK, T_PCGAME, T_WGAME)),
        ),
    )

    private val storageExt = Question(
        id = "storage_ext", title = "خيارات التخزين", mode = SelectionMode.MULTI,
        types = NOTOOL, allowOther = true,
        options = listOf(
            o("files_storage", "تخزين الصور والملفات"),
            o("device_sync", "المزامنة بين الأجهزة"),
            o("offline_first", "العمل دون اتصال والمزامنة لاحقًا", MOBILE + s(T_DESK)),
            o("backup_restore", "النسخ الاحتياطي والاستعادة"),
            o("secure_storage", "تخزين البيانات الحساسة بأمان"),
        ),
    )

    // ---------- 3) الحسابات ----------
    private val auth = Question(
        id = "auth", title = "الحسابات وتسجيل الدخول", mode = SelectionMode.MULTI,
        types = NOTOOL - T_EXT, allowOther = true,
        options = listOf(
            o("auth_none", "بدون حسابات", ex = true),
            o("email_pw", "البريد الإلكتروني وكلمة المرور"),
            o("google", "Google Sign-In"),
            o("apple", "تسجيل Apple", s(T_IOS, T_CROSS, T_WEB, T_SHOP)),
            o("phone_otp", "رقم الهاتف ورمز التحقق"),
            o("guest", "حساب ضيف"),
            o("profile", "الملف الشخصي"),
            o("roles", "الأدوار والصلاحيات"),
            o("admin_user", "المدير والمستخدم"),
            o("verify_account", "التحقق من الحساب"),
            o("reset_pw", "استعادة كلمة المرور"),
            o("two_fa", "المصادقة الثنائية"),
            o("sessions", "إدارة الجلسات"),
            o("delete_account", "حذف الحساب والبيانات"),
        ),
    )

    // ---------- 4) الذكاء الاصطناعي (لمشروع المستخدم) ----------
    private val aiProvider = Question(
        id = "ai_provider", title = "خدمة الذكاء الاصطناعي في مشروعك", mode = SelectionMode.MULTI,
        allowOther = true,
        options = listOf(
            o("ai_none", "بدون ذكاء اصطناعي", ex = true),
            o("gemini", "Gemini API"),
            o("openai", "OpenAI API"),
            o("claude_api", "Claude API"),
            o("groq", "Groq API"),
            o("cf_ai", "Cloudflare Workers AI"),
            o("mistral", "Mistral API"),
        ),
    )

    private val aiFeatures = Question(
        id = "ai_features", title = "ميزات الذكاء الاصطناعي", mode = SelectionMode.MULTI,
        options = listOf(
            o("ai_chat", "مساعد محادثة"),
            o("ai_text", "توليد النصوص"),
            o("ai_summary", "تلخيص المحتوى"),
            o("ai_image", "تحليل الصور"),
            o("ai_semantic", "البحث الدلالي"),
            o("ai_rag", "RAG والبحث في المستندات"),
            o("ai_switch", "اختيار مزود الخدمة من الإعدادات"),
        ),
    )

    // ---------- 5) الإعلانات وتحقيق الدخل ----------
    private val adsNetwork = Question(
        id = "ads_network", title = "شبكة الإعلانات", mode = SelectionMode.MULTI,
        types = MOBILE, allowOther = true,
        options = listOf(
            o("ads_none", "بدون إعلانات", ex = true),
            o("startio", "Start.io"),
            o("admob", "Google AdMob"),
        ),
    )

    private val adsFormat = Question(
        id = "ads_format", title = "أنواع الإعلانات", mode = SelectionMode.MULTI,
        types = MOBILE,
        options = listOf(
            o("ad_banner", "إعلانات البانر"),
            o("ad_interstitial", "الإعلانات البينية"),
            o("ad_rewarded", "الإعلانات بمكافأة"),
            o("ad_native", "الإعلانات الأصلية"),
            o("ad_placements", "إدارة أماكن الإعلانات"),
            o("ad_consent", "متطلبات الموافقة والخصوصية"),
        ),
    )

    private val monetization = Question(
        id = "monetization", title = "وسائل تحقيق الدخل", mode = SelectionMode.MULTI,
        types = NOTOOL, allowOther = true,
        options = listOf(
            o("remove_ads", "إزالة الإعلانات المدفوعة", MOBILE),
            o("subscriptions", "الاشتراكات"),
            o("iap", "عمليات الشراء داخل التطبيق", MOBILE + s(T_PCGAME)),
            o("points", "نظام النقاط والمكافآت"),
        ),
    )

    // ---------- 6) الخدمات الخارجية ----------
    private val firebase = Question(
        id = "firebase", title = "خدمات Firebase", mode = SelectionMode.MULTI,
        types = MOBILE + WEBLIKE + s(T_API, T_BOT),
        options = listOf(
            o("fb_auth", "Firebase Authentication"),
            o("fb_storage", "Cloud Storage"),
            o("fcm", "Firebase Cloud Messaging", MOBILE + WEBLIKE),
            o("crashlytics", "Crashlytics", MOBILE),
            o("fb_analytics", "Analytics", MOBILE + WEBLIKE),
            o("remote_config", "Remote Config", MOBILE + WEBLIKE),
        ),
    )

    private val supabase = Question(
        id = "supabase", title = "خدمات Supabase", mode = SelectionMode.MULTI,
        types = MOBILE + WEBLIKE + s(T_API, T_BOT),
        options = listOf(
            o("sb_auth", "Supabase Authentication"),
            o("sb_storage", "Supabase Storage"),
            o("sb_realtime", "Supabase Realtime"),
            o("sb_edge", "Supabase Edge Functions"),
        ),
    )

    private val integrations = Question(
        id = "integrations", title = "تكاملات وخدمات أخرى", mode = SelectionMode.MULTI,
        allowOther = true,
        options = listOf(
            o("rest", "REST API"),
            o("graphql", "GraphQL"),
            o("websocket", "WebSocket"),
            o("rss", "RSS"),
            o("maps", "الخرائط والموقع", NOTOOL),
            o("email_service", "البريد الإلكتروني"),
            o("sms", "رسائل SMS"),
            o("payment_gateway", "بوابات الدفع", NOTOOL),
            o("csv_json", "استيراد وتصدير CSV وJSON"),
            o("custom_service", "خدمة خارجية مخصصة"),
        ),
    )

    // ---------- 7) الوظائف الإضافية ----------
    private val features = Question(
        id = "features", title = "الميزات", mode = SelectionMode.MULTI,
        allowOther = true,
        options = listOf(
            o("search_filters", "البحث والفلاتر"),
            o("notifications", "الإشعارات", NOTOOL),
            o("upload", "رفع الصور والملفات", NOTOOL),
            o("camera", "الكاميرا والمعرض", MOBILE + s(T_WEB, T_SHOP)),
            o("mic_audio", "الميكروفون والصوت", NOTOOL),
            o("video", "تشغيل الفيديو", NOTOOL),
            o("location", "الموقع والخرائط", NOTOOL),
            o("bt_usb", "البلوتوث وUSB", MOBILE + s(T_DESK)),
            o("qr", "QR والباركود", MOBILE + s(T_DESK, T_WEB, T_SHOP)),
            o("pdf", "ملفات PDF"),
            o("share", "مشاركة المحتوى"),
            o("favorites", "المفضلة والتقييمات", NOTOOL),
            o("chat_comments", "التعليقات والمراسلة", NOTOOL),
            o("payments", "الدفع الإلكتروني", NOTOOL),
            o("admin_panel", "لوحة الإدارة", NOTOOL),
            o("activity_log", "سجل النشاط"),
            o("reports", "التقارير والإحصاءات"),
            o("multi_user", "تعدد المستخدمين", NOTOOL),
            o("sync", "المزامنة"),
            o("scheduled", "جدولة المهام"),
            o("background", "العمل في الخلفية", MOBILE + s(T_DESK)),
            o("deeplinks", "الروابط العميقة", MOBILE + s(T_WEB, T_SHOP)),
            o("a11y", "إمكانية الوصول", NOTOOL),
            o("themes", "الثيمات والوضع الداكن", NOTOOL),
            o("i18n", "دعم عدة لغات", NOTOOL),
        ),
    )

    private val gameFeatures = Question(
        id = "game_features", title = "ميزات الألعاب", mode = SelectionMode.MULTI,
        types = GAMES, allowOther = true,
        options = listOf(
            o("levels", "المراحل"),
            o("characters", "الشخصيات"),
            o("inventory", "المخزون"),
            o("physics", "الفيزياء"),
            o("enemy_ai", "الذكاء الاصطناعي للأعداء"),
            o("save_game", "الحفظ"),
            o("achievements", "الإنجازات"),
            o("leaderboard", "لوحة الصدارة"),
            o("multiplayer", "اللعب الجماعي"),
            o("game_audio", "الصوت والموسيقى"),
            o("touch_controls", "التحكم باللمس", s(T_AGAME, T_WGAME)),
        ),
    )

    // ---------- 8) بنية المشروع ----------
    private val arch = Question(
        id = "arch", title = "النمط المعماري", mode = SelectionMode.SINGLE,
        allowAuto = true, allowOther = true,
        options = listOf(
            o("single_file", "ملف واحد للمشاريع البسيطة"),
            o("by_feature", "تقسيم حسب الميزات"),
            o("mvvm", "MVVM", MOBILE + s(T_DESK, T_PCGAME)),
            o("mvc", "MVC", s(T_WEB, T_SHOP, T_ADMIN, T_API, T_DESK, T_BOT)),
            o("mvp", "MVP", MOBILE + s(T_DESK)),
            o("clean_arch", "Clean Architecture"),
            o("multi_module", "وحدات متعددة"),
        ),
    )

    private val archExtras = Question(
        id = "arch_extras", title = "عناصر البنية", mode = SelectionMode.MULTI,
        allowOther = true,
        options = listOf(
            o("ui_data_split", "فصل الواجهة عن البيانات"),
            o("repository", "Repository Pattern"),
            o("state_mgmt", "إدارة الحالة"),
            o("di", "حقن الاعتماديات"),
            o("error_handling", "إدارة الأخطاء"),
            o("logs", "نظام التسجيل Logs"),
            o("scalable", "بنية قابلة للتوسع"),
        ),
    )

    // ---------- 9) الاختبارات والجودة والأمان ----------
    private val quality = Question(
        id = "quality", title = "الاختبارات والجودة والأمان", mode = SelectionMode.MULTI,
        allowOther = true,
        options = listOf(
            o("unit_tests", "Unit Tests"),
            o("integration_tests", "Integration Tests"),
            o("ui_tests", "UI Tests", ALL - s(T_API, T_BOT, T_TOOL)),
            o("api_tests", "اختبارات API", SERVER),
            o("db_tests", "اختبارات قواعد البيانات"),
            o("sample_data", "بيانات تجريبية"),
            o("missing_files_check", "فحص الملفات المفقودة"),
            o("deps_check", "فحص التبعيات"),
            o("lint", "قواعد تنسيق الكود"),
            o("basic_security", "فحوصات أمنية أساسية"),
            o("secrets_safe", "تخزين آمن للأسرار"),
            o("input_validation", "التحقق من المدخلات"),
            o("rate_limit", "تحديد معدل الطلبات", SERVER),
            o("permission_policies", "سياسات الصلاحيات"),
            o("audit_logs", "سجلات التدقيق"),
            o("privacy_policy", "ملفات سياسة الخصوصية", NOTOOL),
        ),
    )

    // ---------- 10) البناء والنشر ----------
    private val build = Question(
        id = "build", title = "البناء والنشر", mode = SelectionMode.MULTI,
        allowOther = true,
        options = listOf(
            o("gradle_android", "مشروع Android Gradle", s(T_AND, T_AGAME)),
            o("apk_debug", "APK تجريبي", s(T_AND, T_AGAME, T_CROSS)),
            o("aab_release", "AAB للنشر", s(T_AND, T_AGAME, T_CROSS)),
            o("git_github", "Git وGitHub"),
            o("gh_actions", "GitHub Actions"),
            o("auto_build", "البناء التلقائي"),
            o("manual_build", "البناء عند الطلب"),
            o("readme", "README"),
            o("gitignore", "ملف .gitignore"),
            o("dev_env", "ملفات إعداد بيئة التطوير"),
            o("debug_release", "إعدادات Debug وRelease"),
            o("docker", "Docker", SERVER + s(T_TOOL)),
            o("cicd", "CI/CD للمواقع والخوادم", SERVER),
            o("run_docs", "تعليمات البناء والتشغيل"),
        ),
    )

    val sections: List<Section> = listOf(
        Section("tech", "اللغات والتقنيات", listOf(lang, framework, engine)),
        Section("data", "قاعدة البيانات والتخزين", listOf(db, storageExt)),
        Section("auth", "الحسابات وتسجيل الدخول", listOf(auth)),
        Section("ai", "خدمات الذكاء الاصطناعي (اختيارية)", listOf(aiProvider, aiFeatures)),
        Section("ads", "الإعلانات وتحقيق الدخل", listOf(adsNetwork, adsFormat, monetization)),
        Section("services", "الخدمات الخارجية والتكاملات", listOf(firebase, supabase, integrations)),
        Section("features", "الوظائف الإضافية", listOf(features, gameFeatures)),
        Section("arch", "بنية المشروع", listOf(arch, archExtras)),
        Section("quality", "الاختبارات والجودة والأمان", listOf(quality)),
        Section("build", "البناء والنشر", listOf(build)),
    )

    // ---------- قواعد الظهور والتوافق ----------
    fun isTypeVisible(types: Set<String>?, type: String?): Boolean =
        types == null || type == T_CUSTOM || (type != null && type in types)

    private fun selectedLangs(sel: Map<String, Set<String>>): Set<String> =
        (sel["lang"] ?: emptySet()) - setOf(AUTO, OTHER)

    fun optionVisible(opt: Option, type: String?, sel: Map<String, Set<String>>): Boolean {
        if (!isTypeVisible(opt.types, type)) return false
        val langs = opt.langs ?: return true
        val chosen = selectedLangs(sel)
        return chosen.isEmpty() || chosen.any { it in langs }
    }

    fun visibleOptions(q: Question, type: String?, sel: Map<String, Set<String>>): List<Option> =
        if (!isTypeVisible(q.types, type)) emptyList()
        else q.options.filter { optionVisible(it, type, sel) }

    fun visibleQuestions(sec: Section, type: String?, sel: Map<String, Set<String>>): List<Question> =
        sec.questions.filter { visibleOptions(it, type, sel).isNotEmpty() }
}
