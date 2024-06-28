/*
 * Copyright (c) 2024 Paysafe Group
 */

import org.gradle.api.JavaVersion
import java.io.File

object ConfigData {
    const val compileSdkVersion = 34 // Android 14
    const val minSdkVersion = 23 // Android 6
    const val targetSdkVersion = 34 // Android 14
    const val buildToolsVersion = "34.0.0"
    const val minifyEnabled = false
    const val versionName = "1.0.0"
    const val versionCode = 15
    const val kotlinJvmTarget = "1.8"
    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = JavaVersion.VERSION_1_8
}

object TopLevelGradle {
    val androidLibPlugin by lazy { "com.android.library" }
    val androidAppPlugin by lazy { "com.android.application" }
    val kotlinParcelizePlugin by lazy { "kotlin-parcelize" }
    val kotlinSerializationPlugin by lazy { "kotlinx-serialization" }
    val jetbrainsKotlinPlugin by lazy { "org.jetbrains.kotlin.android" }
    val jetbrainsSerializationPlugin by lazy { "org.jetbrains.kotlin.plugin.serialization" }
    val dokkaPlugin by lazy { "org.jetbrains.dokka" }
    val navigationSafeArgsPlugin by lazy { "androidx.navigation.safeargs.kotlin" }
    val navigationSafeArgsClasspath by lazy { "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeArgs}" }
    val mavenPublishPlugin by lazy { "maven-publish" }
}

object LintConfig {
    const val htmlReport = false
    const val textReport = false
    const val abortOnError = true
    const val checkAllWarnings = true
    const val ignoreTestSources = true
    const val explainIssues = false
    const val noLines = false
    val disableRules = mutableSetOf(
        "VectorPath",
        "UnusedAttribute",
        "UnusedResources",
        "UnusedIds",
        "DuplicateStrings"
    )

    fun getTextOutputFileName(moduleName: String) =
        File("../reports/lint/lint-results-$moduleName.txt")

    fun getHtmlOutputFile(moduleName: String) =
        File("../reports/lint/lint-results-$moduleName.html")

}

object Versions {
    // Top Level Gradle
    const val safeArgs = "2.6.0"
    const val androidPlugins = "8.2.2"
    const val jetbrainsKotlinPlugin = "1.8.22"
    const val jetbrainsSerializationPlugin = "1.6.10"
    const val dokka = "1.9.0"
    const val ktxCompilerExt = "1.4.8"

    // Android Base
    const val appCompat = "1.6.1"
    const val recyclerView = "1.3.1"
    const val constraintLayout = "2.1.4"
    const val desugarJdk = "2.0.3"
    const val activity = "1.8.1"

    // Jetpack
    const val coreKtx = "1.12.0"
    const val lifecycleKtx = "2.6.2"
    const val navigationKtx = "2.6.0"
    const val testMonitor = "1.6.1"
    const val testJUnit = "1.1.5"

    // Compose
    const val composeUi = "1.5.1"
    const val composeUiTest = "1.5.4"
    const val composeBom = "2023.08.00"
    const val material3 = "material3"

    // Google
    const val material = "1.9.0"
    const val wallet = "19.2.1"
    const val gPayButton = "0.1.1"
    const val playServicesTask = "18.0.1"
    const val coroutinesPlayServices = "1.6.4"

    // Kotlin
    const val coroutines = "1.7.3"
    const val jsonSerialization = "1.5.1"

    // Network
    const val okHttp3Bom = "4.10.0"

    // Retrofit
    const val retrofit = "2.9.0"

    //Venmo
    const val venmoDependency = "4.39.0"

    // Testing
    const val jUnit = "4.13.2"
    const val mockK = "1.13.7"
    const val archCoreTesting = "2.2.0"
    const val robolectric = "4.11.1"
    const val lifecycleRuntimeTesting = "2.7.0"

    // Android Testing
    const val hamcrest = "2.2"
    const val espresso = "3.5.1"
    const val androidJUnit = "1.1.5"
}

object AndroidBase {
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    val recyclerView by lazy { "androidx.recyclerview:recyclerview:${Versions.recyclerView}" }
    val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}" }
    val desugarJdk by lazy { "com.android.tools:desugar_jdk_libs:${Versions.desugarJdk}" }
    val activity by lazy { "androidx.activity:activity-ktx:${Versions.activity}" }
}

object Jetpack {
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val lifecycleLiveData by lazy { "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleKtx}" }
    val lifecycleViewModel by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleKtx}" }
    val lifecycleProcess by lazy { "androidx.lifecycle:lifecycle-process:${Versions.lifecycleKtx}" }
    val navigationFragment by lazy { "androidx.navigation:navigation-fragment-ktx:${Versions.navigationKtx}" }
    val navigationUi by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigationKtx}" }
    val testMonitor by lazy { "androidx.test:monitor:${Versions.testMonitor}" }
    val testJUnit by lazy { "androidx.test.ext:junit-ktx:${Versions.testJUnit}" }
}

object Compose {
    val ui by lazy { "androidx.compose.ui:ui:${Versions.composeUi}" }
    val uiTooling by lazy { "androidx.compose.ui:ui-tooling" }
    val uiPreview by lazy { "androidx.compose.ui:ui-tooling-preview" }
    val bom by lazy { "androidx.compose:compose-bom:${Versions.composeBom}" }
    val material3 by lazy { "androidx.compose.material3:${Versions.material3}" }
    val uiTestJUnit4 by lazy { "androidx.compose.ui:ui-test-junit4:${Versions.composeUiTest}" }
    val uiTestManifest by lazy { "androidx.compose.ui:ui-test-manifest:${Versions.composeUiTest}" }
}

object Google {
    val material by lazy { "com.google.android.material:material:${Versions.material}" }
    val payWallet by lazy { "com.google.android.gms:play-services-wallet:${Versions.wallet}" }
    val gPayButton by lazy { "com.google.pay.button:compose-pay-button:${Versions.gPayButton}" }
    val playServicesTask by lazy { "com.google.android.gms:play-services-tasks:${Versions.playServicesTask}" }
    val coroutinesPlayServices by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutinesPlayServices}" }
}

object Kotlin {
    val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}" }
    val jsonSerialization by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.jsonSerialization}" }
}

object Network {
    val okHttp3Bom by lazy { "com.squareup.okhttp3:okhttp-bom:${Versions.okHttp3Bom}" }
    val okHttp3 by lazy { "com.squareup.okhttp3:okhttp" }
    val okHttp3logInterceptor by lazy { "com.squareup.okhttp3:logging-interceptor" }
}

object Retrofit {
    val retrofit by lazy { "com.squareup.retrofit2:retrofit:${Versions.retrofit}" }
    val gsonConverter by lazy { "com.squareup.retrofit2:converter-gson:${Versions.retrofit}" }
}

object Venmo {
    val venmoPayments by lazy { "com.braintreepayments.api:venmo:${Versions.venmoDependency}" }
}

object Testing {
    val jUnit by lazy { "junit:junit:${Versions.jUnit}" }
    val mockK by lazy { "io.mockk:mockk:${Versions.mockK}" }
    val archCoreTesting by lazy { "androidx.arch.core:core-testing:${Versions.archCoreTesting}" }
    val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}" }
    val robolectric by lazy { "org.robolectric:robolectric:${Versions.robolectric}" }
    val lifecycleRuntimeTesting by lazy { "androidx.lifecycle:lifecycle-runtime-testing:${Versions.lifecycleRuntimeTesting}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-test:${Versions.jetbrainsKotlinPlugin}" }
    val work by lazy { "androidx.work:work-runtime-ktx:2.9.0" }
}

object AndroidTesting {
    val jUnit by lazy { "androidx.test.ext:junit:${Versions.androidJUnit}" }
    val hamcrest by lazy { "org.hamcrest:hamcrest:${Versions.hamcrest}" }
    val espressoCore by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
    val espressoContrib by lazy { "androidx.test.espresso:espresso-contrib:${Versions.espresso}" }
}