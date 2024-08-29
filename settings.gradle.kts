/*
 * Copyright (c) 2024 Paysafe Group
 */

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://artifactory.neterra.paysafe.com/artifactory/paysafe-wallet-mobile/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "paysafe-android"

include(":example")
include(":google-pay")
include(":paysafe-core")
include(":threedsecure")
include(":card-payments")
include(":tokenization")
include(":paysafe-cardinal")
include(":venmo")
