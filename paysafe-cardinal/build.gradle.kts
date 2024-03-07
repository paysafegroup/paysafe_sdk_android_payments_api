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
    id(TopLevelGradle.mavenPublishPlugin)
}

configurations.maybeCreate("default")
artifacts.add("default", file("./paysafe-cardinal.aar"))


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {

                groupId = "com.paysafegroup"
                artifactId = project.name
                version = getVersionFromProperties()
                artifact("paysafe-cardinal.aar")
            }
        }

        repositories {
            mavenLocal()
        }
    }
}
