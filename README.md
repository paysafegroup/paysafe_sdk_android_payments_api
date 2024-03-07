To get a Git project into your build:
Step 1. Add the JitPack repository to your build file Add it in your root build.gradle (or settings.gradle.kts) at the end of repositories:

    dependencyResolutionManagement {        
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)        
        repositories {            
            mavenCentral()            
            maven { url = uri("https://jitpack.io") }
        }
    }

Step 2. Add the dependency

    dependencies {
        implementation("com.github.paysafegroup:paysafe_sdk_android_payments_api:0.0.11")    
    }