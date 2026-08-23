plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.features.pixiv.user.setting"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}
