plugins {
    id("bgm.library")
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.kotlinKtorfit)
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.coreNative)
        }
    }
}
