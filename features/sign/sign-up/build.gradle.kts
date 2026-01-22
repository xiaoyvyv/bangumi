plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.sign.sign_up"
    }


    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
        }
    }
}