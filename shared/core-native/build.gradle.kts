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
    android {
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
            val myInterop = cinterops.create("myInterop") {
                defFile(project.file("src/iosMain/cinterop/BridgeSwift.def"))
                includeDirs(project.file("headers"))
            }
            val live2dInterop = cinterops.create("live2d") {
                defFile(project.file("src/iosMain/cinterop/live2d.def"))
                includeDirs(project.file("src/cpp"))
                val targetDir = if (iosTarget.name.contains("Simulator")) "iphonesimulator" else "iphoneos"
                extraOpts("-libraryPath", project.file("native/ios/$targetDir").absolutePath)
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.webkit)
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
