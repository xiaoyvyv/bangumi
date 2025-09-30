plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.features.pixiv.login"
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}