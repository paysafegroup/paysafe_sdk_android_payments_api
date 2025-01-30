/*
 * Copyright (c) 2024 Paysafe Group
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath(TopLevelGradle.navigationSafeArgsClasspath)
    }
}

plugins {
    id(TopLevelGradle.androidAppPlugin) version Versions.androidPlugins apply false
    id(TopLevelGradle.jetbrainsKotlinPlugin) version Versions.jetbrainsKotlinPlugin apply false
    id(TopLevelGradle.androidLibPlugin) version Versions.androidPlugins apply false
    id(TopLevelGradle.jetbrainsSerializationPlugin) version Versions.jetbrainsSerializationPlugin apply false
    id(TopLevelGradle.dokkaPlugin) version Versions.dokka apply false
}



subprojects {
    apply(plugin = "maven-publish")
}

subprojects {
    tasks.withType<Task>().configureEach {
        if (name.contains("ReleaseUnitTest")) {
            enabled = false
        }
    }
}
