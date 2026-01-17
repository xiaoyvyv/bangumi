plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.mono.detail"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
            implementation(projects.features.friend)
            implementation(projects.features.preview.album)
            implementation(projects.features.subject.page)
            implementation(projects.features.index.page)
        }
    }
}
