import java.util.regex.Pattern

group = "com.galois"
version = "0.2"

// The version of PyTorch needed to build.
// Currently set to 2.1.0 for evident JVM/Android compatibility, but should
// be safe to use newer versions. Note that if you do that, you shouldn't use
// this variable in the dependencies defined below!
val libtorchVersion = "2.1.0"

plugins {
    kotlin("multiplatform") version "1.9.0"
    id("io.kotest.multiplatform") version "5.6.2"
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(17)
        testRuns["test"].executionTask.configure {
            // Require that LIBTORCH_HOME be set.
            val libtorchHome: String = System.getenv("LIBTORCH_HOME")
                ?: throw RuntimeException("LIBTORCH_HOME not present in environment.")

            // Confirm we have a build-version file.
            val buildVersionFile = File(libtorchHome, "build-version")
            if (!buildVersionFile.isFile) {
                throw RuntimeException(
                    "Cannot find $buildVersionFile. " +
                            "Make sure LIBTORCH_HOME refers to the root of the libtorch distribution."
                )
            }

            // Make sure the installed PyTorch matches what we expect.
            val installedVersion = buildVersionFile.readLines()[0].replace("\"", "")
            val versionPattern = Regex("^" + Pattern.quote(libtorchVersion) + "\\b.*")
            if (!installedVersion.matches(versionPattern)) {
                throw RuntimeException("Found libtorch version $installedVersion, but build.gradle.kts expects $libtorchVersion.")
            }
            useJUnitPlatform()
            systemProperty(
                "java.library.path",
                "$libtorchHome/lib"
            )
        }
    }
    androidTarget()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation("com.google.crypto.tink:tink:1.7.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-framework-engine:5.6.2")
                implementation("io.kotest:kotest-property:5.6.2")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.pytorch:pytorch_java_only:$libtorchVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.6.2")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.pytorch:pytorch_android_lite:$libtorchVersion")
            }
        }
        val androidInstrumentedTest by getting {
            dependsOn(commonMain)
            dependsOn(androidMain)
            dependencies {
                implementation("androidx.test:core:1.6.1")
                implementation("androidx.test:rules:1.6.1")
            }
        }
    }
}

android {
    namespace = "com.galois.cadenas"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}