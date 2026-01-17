plugins {
    id("bgm.library")
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.kotlinKtorfit)
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
        }
    }
}