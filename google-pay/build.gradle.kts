/*
 * Copyright (c) 2024 Paysafe Group
 */

import java.util.Properties

private val versionPropertiesFile = Properties().apply {
    val file = File(rootProject.projectDir, "version.properties")
    if (file.exists())
        load(file.inputStream())
    else
        throw Exception("Error: version.properties file does not exists.")
}

// Function to read version from version.properties
fun getVersionFromProperties(): String {
    val version = versionPropertiesFile["VERSION"] as String?

    println("${project.name} version: $version")

    if (version == null)
        throw Exception("Error: Version is null.")
    else
        return version
}

plugins {
    id(TopLevelGradle.androidLibPlugin)
    id(TopLevelGradle.jetbrainsKotlinPlugin)
    id(TopLevelGradle.kotlinSerializationPlugin)
    id(TopLevelGradle.dokkaPlugin)
    id(TopLevelGradle.mavenPublishPlugin)
}

android {
    namespace = "com.paysafe.android.google_pay"
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = ConfigData.minifyEnabled
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = ConfigData.sourceCompatibility
        targetCompatibility = ConfigData.targetCompatibility
    }
    kotlinOptions {
        jvmTarget = ConfigData.kotlinJvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.ktxCompilerExt
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    lint {
        htmlReport = LintConfig.htmlReport
        textReport = LintConfig.textReport
        abortOnError = LintConfig.abortOnError
        checkAllWarnings = LintConfig.checkAllWarnings
        ignoreTestSources = LintConfig.ignoreTestSources
        explainIssues = LintConfig.explainIssues
        noLines = LintConfig.noLines
        textOutput = LintConfig.getTextOutputFileName("google-pay")
        htmlOutput = LintConfig.getHtmlOutputFile("google-pay")
        disable += LintConfig.disableRules
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(project(":tokenization")) {
        exclude(
            "*",
            module = "paysafe-cardinal"
        )
    }
    implementation(project(":paysafe-core"))

    api(Google.playServicesTask)
    implementation(Google.payWallet)
    implementation(Google.gPayButton)
    implementation(Google.coroutinesPlayServices)
    implementation(Compose.ui)
    implementation(AndroidBase.activity)
    implementation(AndroidBase.appCompat)

    // Debugging
    debugImplementation(Compose.uiTestManifest)

    // Tests
    testImplementation(Compose.uiTestJUnit4)
    testImplementation(Testing.jUnit)
    testImplementation(Testing.mockK)
    testImplementation(Testing.archCoreTesting)
    testImplementation(Testing.robolectric)
    testImplementation(Testing.coroutines)
    testImplementation(Testing.lifecycleRuntimeTesting)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.paysafegroup"
                artifactId = project.name
                version = getVersionFromProperties()
            }
        }
    }
}
