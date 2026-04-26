plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            api(projects.shared.uiLiquid)
            api(projects.shared.uiMaterial3)
        }
    }
}

