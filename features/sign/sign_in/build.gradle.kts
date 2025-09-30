plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.features.sign.sign_in"
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