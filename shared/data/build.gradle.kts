plugins {
    id("bgm.library")
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.kotlinKtorfit)
}

android {
    namespace = "com.xiaoyv.bangumi.shared.data"
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
        }
    }
}