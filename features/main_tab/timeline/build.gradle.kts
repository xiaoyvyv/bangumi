plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.main.tab.timeline"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
            implementation(projects.features.timeline.page)
        }
    }
}