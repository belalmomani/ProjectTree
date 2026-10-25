import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// مصدر الإصدارات الوحيد: version.properties
val versionProps = Properties().apply {
    rootProject.file("version.properties").inputStream().use { load(it) }
}
val buildNumber = (System.getenv("BUILD_NUMBER") ?: versionProps.getProperty("LOCAL_BUILD")).toInt()
val appVersionCode = versionProps.getProperty("CODE_OFFSET").toInt() + buildNumber
val appVersionName = "${versionProps.getProperty("VERSION_PREFIX")}.$buildNumber"

// بيانات التوقيع تأتي من متغيرات البيئة (GitHub Secrets) فقط
val keystorePath: String? = System.getenv("KEYSTORE_PATH")

android {
    namespace = "com.belalmomani.projecttree"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.belalmomani.projecttree" // ثابت طوال عمر التطبيق
        minSdk = 24
        targetSdk = 34
        versionCode = appVersionCode
        versionName = appVersionName
    }

    signingConfigs {
        if (keystorePath != null) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
}
