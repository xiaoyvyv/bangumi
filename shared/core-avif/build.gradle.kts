@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("bgm.library")
}

kotlin {

    android {
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

// Skip iOS metadata compilation on non-macOS hosts (cocoapods interop unavailable)
tasks.matching { it.name.contains("IosMainKotlinMetadata") || it.name.contains("Ios") && it.name.contains("Metadata") }.configureEach {
    enabled = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)
}
