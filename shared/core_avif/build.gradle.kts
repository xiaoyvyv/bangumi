@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("bgm.library")
}

kotlin {

    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.libavif"
    }

    sourceSets {
        androidMain.dependencies {
            implementation("com.github.awxkee:avif-coder:2.1.4")
        }

        iosMain.dependencies {
            implementation(projects.shared.coreNative)
        }

        jvmMain.dependencies {
            implementation("com.github.ustc-zzzz:avif-imageio-native-reader:master-SNAPSHOT")
        }
    }
}

