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

/*
private val githubProperties = Properties().apply {
    val file = File(rootProject.projectDir, "github.properties")
    if (file.exists())
        load(file.inputStream())
    else
        throw Exception("Error: version.properties file does not exists.")
}
*/

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
}

android {
    namespace = "com.paysafe.android.core"
    compileSdk = ConfigData.compileSdkVersion
    buildToolsVersion = ConfigData.buildToolsVersion

    defaultConfig {
        buildConfigField("String", "APP_VERSION", "\"${ConfigData.versionName}\"")
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
        buildConfig = true
    }
    lint {
        htmlReport = LintConfig.htmlReport
        textReport = LintConfig.textReport
        abortOnError = LintConfig.abortOnError
        checkAllWarnings = LintConfig.checkAllWarnings
        ignoreTestSources = LintConfig.ignoreTestSources
        explainIssues = LintConfig.explainIssues
        noLines = LintConfig.noLines
        textOutput = LintConfig.getTextOutputFileName("core")
        htmlOutput = LintConfig.getHtmlOutputFile("core")
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
    // Dependencies
    api(platform(Network.okHttp3Bom))
    api(Kotlin.coroutines)
    api(Kotlin.jsonSerialization)
    api(Network.okHttp3)
    api(Network.okHttp3logInterceptor)
    api(Jetpack.lifecycleProcess)
    coreLibraryDesugaring(AndroidBase.desugarJdk)

    // Tests
    testImplementation(Testing.jUnit)
    testImplementation(Testing.mockK)
    testImplementation(Testing.coroutines)
    testImplementation(Testing.robolectric)
    testImplementation(Testing.lifecycleRuntimeTesting)
    testImplementation(Testing.kotlin)
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
