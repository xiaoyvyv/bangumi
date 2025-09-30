plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.features.groups.detail"
}
kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
            implementation(projects.features.friend)
            implementation(projects.features.topic.page)
        }
    }
}
