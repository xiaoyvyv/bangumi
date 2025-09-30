plugins {
    id("bgm.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.multiplatform")
}

android {
    namespace = "com.xiaoyv.bangumi.features.detect"
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}