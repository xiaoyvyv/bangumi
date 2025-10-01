@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.native.tasks.PodGenTask

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.android.library")
}

kotlin {
    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"

        ios.deploymentTarget = "14"

        framework {
            baseName = frameworkBaseName
            isStatic = true
        }

        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets {
        androidMain.dependencies {

        }

        commonMain.dependencies {

        }
    }
}

tasks.withType<PodGenTask>().configureEach {
    doLast {
        podfile.get().apply {
            val text = readText()
                .replace("deployment_target_major < 11", "deployment_target_major < 12")
                .replace("deployment_target_major == 11", "deployment_target_major == 12")
                .replace("#{11}", "#{12}")
            writeText(text)
        }
    }
}