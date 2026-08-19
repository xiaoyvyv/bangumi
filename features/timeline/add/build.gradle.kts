plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.features.timeline.add"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}
