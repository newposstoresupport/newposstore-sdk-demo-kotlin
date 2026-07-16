import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

/** 读取凭据：环境变量 > Gradle -P > 根目录 local.properties（demo 测试 key 已随仓库提供）。 */
fun storeSdkProp(key: String): String {
    System.getenv(key)?.let { return it }
    if (project.hasProperty(key)) return project.property(key).toString()
    val lp = rootProject.file("local.properties")
    if (lp.exists()) {
        val p = Properties()
        lp.inputStream().use { p.load(it) }
        p.getProperty(key)?.let { return it }
    }
    return ""
}

android {
    namespace = "com.android.newpos.store.sdk.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.newpos.store.sdk.demo"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APPID", "\"${storeSdkProp("STORESDK_DEMO_APP_ID")}\"")
        buildConfigField("String", "APPKEY", "\"${storeSdkProp("STORESDK_DEMO_APP_KEY")}\"")
        buildConfigField("String", "APPSECRET", "\"${storeSdkProp("STORESDK_DEMO_APP_SECRET")}\"")
    }

    signingConfigs {
        create("newstore"){
            keyAlias = "newstore"
            keyPassword = "newstore"
            storeFile = file("newstore.jks")
            storePassword = "newpos"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("newstore")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("newstore")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    android.applicationVariants.all {
        val buildType = this.buildType.name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        outputs.all {
            if (this is com.android.build.gradle
                .internal.api.ApkVariantOutputImpl) {
                this.outputFileName = "NewStoreSdkDemoKotlin+" +
                        "_${android.defaultConfig.versionName}_${buildType}.apk" +
                        "_${timeStamp}.apk"
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.mmkv)

    implementation(libs.androidx.preference)
    implementation(libs.androidx.work.runtime)

    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    //from api sdk（Maven：api 1.0.3 含 Param V2 + Firmware）
    implementation(libs.okhttp)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.google.gson)
    implementation(libs.newposstoresupport.api)
    implementation(libs.newposstoresupport.aidl)
    implementation(libs.filedownloader)
    implementation(files("libs/android-baserecyle-master-v1.1.aar"))

    // ROM SDK：固件查询依赖 DevConfig / SDKManager
    compileOnly(files("libs/sdk.jar"))
}
