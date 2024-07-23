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
    namespace = "com.paysafe.android.threedsecure"
    compileSdk = ConfigData.compileSdkVersion
    buildToolsVersion = ConfigData.buildToolsVersion

    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    lint {
        htmlReport = LintConfig.htmlReport
        textReport = LintConfig.textReport
        abortOnError = LintConfig.abortOnError
        checkAllWarnings = LintConfig.checkAllWarnings
        ignoreTestSources = LintConfig.ignoreTestSources
        explainIssues = LintConfig.explainIssues
        noLines = LintConfig.noLines
        textOutput = LintConfig.getTextOutputFileName("threedsecure")
        htmlOutput = LintConfig.getHtmlOutputFile("threedsecure")
        disable += LintConfig.disableRules
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(project(":paysafe-cardinal"))
    implementation(project(":paysafe-core"))

    implementation(AndroidBase.appCompat)
    testImplementation(Testing.jUnit)
    testImplementation(Testing.mockK)
    testImplementation(Testing.coroutines)
    testImplementation(Testing.robolectric)
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
