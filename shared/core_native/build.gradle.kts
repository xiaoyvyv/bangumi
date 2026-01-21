@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("bgm.library")
    id("bgm.native")
    id("app.cash.sqldelight") version "2.2.1"
    alias(libs.plugins.googleKsp)
}



kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.libnative"
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    cocoapods {
        pod("libavif")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.compilations.getByName("main") {
            val myInterop by cinterops.creating {
                definitionFile.set(project.file("src/iosMain/cinterop/BridgeSwift.def"))
                includeDirs(project.file("headers"))
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(files("libs/Live2DCubismCore.aar"))
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.xiaoyv.bangumi.shared.native")
        }
    }
}
