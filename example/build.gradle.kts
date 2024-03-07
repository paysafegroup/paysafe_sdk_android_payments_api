/*
 * Copyright (c) 2024 Paysafe Group
 */

import java.util.Properties

plugins {
    id(TopLevelGradle.androidAppPlugin)
    id(TopLevelGradle.jetbrainsKotlinPlugin)
    id(TopLevelGradle.kotlinParcelizePlugin)
    id(TopLevelGradle.navigationSafeArgsPlugin)
}

private val privateKeysProperties = Properties().apply {
    val file = File(rootProject.projectDir, "private-keys.properties")
    if (file.exists())
        load(file.inputStream())
}

private val releaseBuild = "release"

private val keystorePassword = if (System.getenv("KEYSTORE_PASSWORD") != null)
    System.getenv("KEYSTORE_PASSWORD")
else
    privateKeysProperties["keystorePassword"] as String?

private val keystoreAlias = if (System.getenv("KEYSTORE_ALIAS") != null)
    System.getenv("KEYSTORE_ALIAS")
else
    privateKeysProperties["keystoreAlias"] as String?

// if (keystorePassword.isNullOrEmpty() || keystoreAlias.isNullOrEmpty())
//     throw Exception("Error: Cant load keystore credentials")

android {
    namespace = "com.paysafe.example"
    compileSdk = ConfigData.compileSdkVersion
    buildToolsVersion = ConfigData.buildToolsVersion

    defaultConfig {
        applicationId = "com.paysafe.example"
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    signingConfigs {
        create(releaseBuild) {
            keyAlias = keystoreAlias
            keyPassword = keystorePassword
            storeFile = file("../paysafe-sample-keystore.jks")
            storePassword = keystorePassword
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName(releaseBuild)
            isMinifyEnabled = ConfigData.minifyEnabled
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    lint {
        htmlReport = LintConfig.htmlReport
        textReport = LintConfig.textReport
        abortOnError = false
        checkAllWarnings = false
        ignoreTestSources = LintConfig.ignoreTestSources
        explainIssues = LintConfig.explainIssues
        noLines = LintConfig.noLines
        textOutput = LintConfig.getTextOutputFileName("example")
        htmlOutput = LintConfig.getHtmlOutputFile("example")
        disable += LintConfig.disableRules.apply {
            addAll(
                arrayOf(
                    "SelectableText",
                    "HardcodedText",
                    "SetTextI18n"
                )
            )
        }
    }
    testOptions {
        unitTests.all { test ->
            test.enabled = false
        }
    }
}

dependencies {
    implementation(project(":hosted-fields"))
    implementation(project(":google-pay"))
    implementation(project(":paypal"))

    // Dependencies
    implementation(AndroidBase.appCompat)
    implementation(AndroidBase.recyclerView)
    implementation(AndroidBase.constraintLayout)
    implementation(Jetpack.coreKtx)
    implementation(Jetpack.lifecycleLiveData)
    implementation(Jetpack.lifecycleViewModel)
    implementation(Jetpack.navigationFragment)
    implementation(Jetpack.navigationUi)
    implementation(Compose.ui)
    implementation(Google.material)
    implementation(Retrofit.retrofit)
    implementation(Retrofit.gsonConverter)

    // Runtime
    runtimeOnly(Kotlin.coroutines)
}