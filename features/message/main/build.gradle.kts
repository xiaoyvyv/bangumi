plugins {
    id("bgm.library")
}


kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.message.main"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)

            implementation(projects.features.friend)
        }
    }
}