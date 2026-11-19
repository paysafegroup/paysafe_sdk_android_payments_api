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

}



subprojects {
    apply(plugin = "maven-publish")

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            when {
                requested.group == "io.netty" -> useVersion(Versions.netty)

                requested.group == "com.google.guava" && requested.name == "guava" ->
                    useVersion(Versions.guava)

                requested.group == "com.google.protobuf" && requested.name.startsWith("protobuf-") ->
                    useVersion(Versions.protobuf)

                requested.group == "com.google.android.gms" && requested.name == "play-services-basement" ->
                    useVersion(Versions.playServicesBasement)

                requested.group == "com.google.code.gson" && requested.name == "gson" ->
                    useVersion(Versions.gson)
            }
        }
    }
}

subprojects {
    tasks.withType<Task>().configureEach {
        if (name.contains("ReleaseUnitTest")) {
            enabled = false
        }
    }
}
