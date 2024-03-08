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
    id(TopLevelGradle.dokkaPlugin)
    id(TopLevelGradle.kotlinParcelizePlugin)
    id(TopLevelGradle.kotlinSerializationPlugin)
    id(TopLevelGradle.mavenPublishPlugin)
}

android {
    namespace = "com.paysafe.android.paypal"
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        viewBinding = true
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
    lint {
        htmlReport = LintConfig.htmlReport
        textReport = LintConfig.textReport
        abortOnError = LintConfig.abortOnError
        checkAllWarnings = LintConfig.checkAllWarnings
        ignoreTestSources = LintConfig.ignoreTestSources
        explainIssues = LintConfig.explainIssues
        noLines = LintConfig.noLines
        textOutput = LintConfig.getTextOutputFileName("paypal")
        htmlOutput = LintConfig.getHtmlOutputFile("paypal")
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
    api(project(":tokenization"))
    api(project(":paysafe-cardinal"))
    api(PayPal.webPayments)
    api(PayPal.nativePayments) {
        exclude(
            "org.jfrog.cardinalcommerce.gradle",
            "cardinalmobilesdk"
        )
    }

    implementation(AndroidBase.appCompat)
    // Tests
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
