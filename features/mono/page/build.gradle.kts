plugins {
    id("bgm.library")
}


kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.mono.page"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}
