package com.belalmomani.projecttree.catalog

const val AUTO = "auto"
const val OTHER = "other"

// معرّفات أنواع المشاريع
const val T_AND = "android_native"
const val T_IOS = "ios"
const val T_CROSS = "cross_platform"
const val T_AGAME = "android_game"
const val T_PCGAME = "pc_game"
const val T_WGAME = "web_game"
const val T_WEB = "website"
const val T_SHOP = "ecommerce"
const val T_ADMIN = "admin_dashboard"
const val T_API = "api_backend"
const val T_DESK = "desktop_app"
const val T_EXT = "browser_extension"
const val T_BOT = "chatbot"
const val T_TOOL = "dev_tool"
const val T_CUSTOM = "custom"

object ProjectTypes {
    val all: List<Pair<String, String>> = listOf(
        T_AND to "تطبيق أندرويد أصلي",
        T_IOS to "تطبيق iOS",
        T_CROSS to "تطبيق متعدد المنصات",
        T_AGAME to "لعبة أندرويد",
        T_PCGAME to "لعبة كمبيوتر",
        T_WGAME to "لعبة ويب",
        T_WEB to "موقع ويب",
        T_SHOP to "متجر إلكتروني",
        T_ADMIN to "لوحة تحكم إدارية",
        T_API to "واجهة API وخادم خلفي",
        T_DESK to "برنامج كمبيوتر",
        T_EXT to "إضافة متصفح",
        T_BOT to "بوت محادثة",
        T_TOOL to "أداة للمطورين",
        T_CUSTOM to "مشروع مخصص",
    )

    fun label(id: String?): String = all.firstOrNull { it.first == id }?.second ?: ""
}

enum class SelectionMode { SINGLE, MULTI }

/**
 * types: أنواع المشاريع التي يظهر فيها الخيار (null = كلها).
 * langs: يظهر الخيار فقط إذا اختار المستخدم إحدى هذه اللغات (أو لم يختر لغة بعد).
 * exclusive: اختيار هذا الخيار يلغي بقية الخيارات (مثل «بدون»).
 */
data class Option(
    val id: String,
    val label: String,
    val types: Set<String>? = null,
    val langs: Set<String>? = null,
    val exclusive: Boolean = false,
)

data class Question(
    val id: String,
    val title: String,
    val mode: SelectionMode,
    val options: List<Option>,
    val types: Set<String>? = null,
    val required: Boolean = false,
    val allowAuto: Boolean = false,
    val allowOther: Boolean = false,
)

data class Section(val id: String, val title: String, val questions: List<Question>)
