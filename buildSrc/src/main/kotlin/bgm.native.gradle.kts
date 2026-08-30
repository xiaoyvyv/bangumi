@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.targets.native.tasks.PodGenTask

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"

        ios.deploymentTarget = "15"
    }

    sourceSets {
        androidMain.dependencies {

        }

        commonMain.dependencies {

        }
    }
}

/**
 * 将 Kotlin 侧 Pod 插件生成的 iOS Podfile 最低版本提升至目标版本。
 */
tasks.withType<PodGenTask>().configureEach {
    doLast {
        podfile.get().apply {
            val minimumDeploymentTarget = 15
            val text = readText()
                .replace(Regex("""deployment_target_major < \d+"""), "deployment_target_major < $minimumDeploymentTarget")
                .replace(Regex("""deployment_target_major == \d+"""), "deployment_target_major == $minimumDeploymentTarget")
                .replace(Regex("""#\{\d+}\.#\{0}""""), "#{$minimumDeploymentTarget}.#{0}\"")
            writeText(text)
        }
    }
}
