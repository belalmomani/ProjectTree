# ProjectTree

تطبيق أندرويد (Kotlin + Jetpack Compose) يساعد على إنشاء الهياكل التنظيمية للمشاريع البرمجية عبر قوائم متفرعة وبرومبت جاهز لـ ChatGPT/Claude، ثم استيراد JSON الهيكل وإنشاء الملفات وتصديرها ZIP.

## الإصدارات والتحديث المباشر
- `applicationId` ثابت: `com.belalmomani.projecttree`
- التوقيع بمفتاح ثابت من GitHub Secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`)
- أرقام الإصدار من `version.properties` + رقم تشغيل GitHub Actions
- كل بناء ينشر `ProjectTree-vX.Y.Z.apk` في GitHub Releases
