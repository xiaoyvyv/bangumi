plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.shared.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
}

