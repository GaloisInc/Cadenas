pluginManagement {
    plugins {
        id("com.google.devtools.ksp") version "1.9.0-1.0.11" apply false
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

rootProject.name = "Cadenas"

include("app")
includeBuild("cadenas-core")

val artifactoryUrl: String by settings
val artifactoryUser: String by settings
val artifactoryPassword: String by settings

val cachePush: String by settings

buildCache {
    local {
        isEnabled = false
    }
    remote<HttpBuildCache> {
        url = uri("$artifactoryUrl/cadenas_generic-local/")
        credentials {
            username = artifactoryUser
            password = artifactoryPassword
        }
        isPush = cachePush == "true"
    }
}
