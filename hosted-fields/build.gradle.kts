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
    id(TopLevelGradle.mavenPublishPlugin)
}

android {
    namespace = "com.paysafe.android.hostedfields"
    compileSdk = ConfigData.compileSdkVersion
    buildToolsVersion = ConfigData.buildToolsVersion

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
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    lint {
        htmlReport = LintConfig.htmlReport
        textReport = LintConfig.textReport
        abortOnError = LintConfig.abortOnError
        checkAllWarnings = LintConfig.checkAllWarnings
        ignoreTestSources = LintConfig.ignoreTestSources
        explainIssues = LintConfig.explainIssues
        noLines = LintConfig.noLines
        textOutput = LintConfig.getTextOutputFileName("hosted-fields")
        htmlOutput = LintConfig.getHtmlOutputFile("hosted-fields")
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
    api(project(":tokenization"))
    val platformComposeBom = platform(Compose.bom)

    // Dependencies
    implementation(platformComposeBom)
    implementation(Compose.material3)
    implementation(Compose.uiPreview)
    implementation(Jetpack.testMonitor)
    implementation(Jetpack.testJUnit)
    implementation(Jetpack.lifecycleLiveData)

    // Debugging
    debugImplementation(Compose.uiTooling)
    debugImplementation(Compose.uiTestManifest)

    // Tests
    testImplementation(Testing.jUnit)
    testImplementation(Testing.mockK)
    testImplementation(Testing.archCoreTesting)
    testImplementation(Testing.robolectric)
    testImplementation(Compose.uiTestJUnit4)
    testCompileOnly(AndroidBase.appCompat)
    androidTestImplementation(platformComposeBom)
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
