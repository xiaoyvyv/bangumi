plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.features.mono.browser"
}
kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
            implementation(projects.features.mono.page)
        }
    }
}
