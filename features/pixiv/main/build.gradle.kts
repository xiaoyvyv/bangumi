plugins {
    id("bgm.library")
}

kotlin {

    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.pixiv.main"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}