plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.dataWorkflow)

            api(projects.shared.uiPlatform)
            api(projects.shared.uiLiquid)
            api(projects.shared.uiMaterial3)
            api(projects.shared.uiVideo)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
