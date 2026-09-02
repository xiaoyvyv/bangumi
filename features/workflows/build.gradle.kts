plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.features.workflows"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.dataWorkflow)
            implementation(projects.shared.ui)
        }
    }
}
