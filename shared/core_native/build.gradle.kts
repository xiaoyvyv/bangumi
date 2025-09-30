@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("bgm.library")
    id("bgm.native")
    id("app.cash.sqldelight") version "2.1.0"
    alias(libs.plugins.googleKsp)
}


android {
    namespace = "com.xiaoyv.bangumi.shared.libnative"
}

kotlin {
    cocoapods {
        podfile = project.file("../../iosApp/Podfile")
        pod("libavif")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.compilations.getByName("main") {
            val myInterop by cinterops.creating {
                definitionFile.set(project.file("src/iosMain/cinterop/BridgeSwift.def"))
                includeDirs(project.file("headers"))
            }

            val live2d by cinterops.creating {
                definitionFile.set(project.file("src/iosMain/cinterop/Live2DSDK.def"))
                includeDirs(rootProject.file("iosApp/iosApp"), rootProject.file("iosApp/iosApp/Live2D"))
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
