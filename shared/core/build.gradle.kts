plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.shared.core"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.shared.coreNative)
            api(projects.shared.coreAvif)
            api(projects.shared.coreResource)
        }
    }
}