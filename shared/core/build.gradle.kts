plugins {
    id("bgm.library")
}

kotlin {

    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.core"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.shared.coreNative)
            api(projects.shared.coreAvif)
            api(projects.shared.coreResource)
        }
    }
}