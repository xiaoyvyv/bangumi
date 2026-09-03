plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.ui.platform"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.coreNative)
            implementation(projects.shared.coreResource)
            implementation(projects.shared.data)
            implementation(projects.shared.uiPlatform)
            implementation(projects.shared.uiMaterial3)
        }
    }
}
