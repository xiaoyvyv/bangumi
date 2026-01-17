plugins {
    id("bgm.library")
}


kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.main"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
            implementation(projects.features.mainTab.home)
            implementation(projects.features.mainTab.timeline)
            implementation(projects.features.mainTab.topic)
            implementation(projects.features.mainTab.profile)
            implementation(projects.features.mainTab.tracking)
            implementation(projects.features.subject.browser)
            implementation(projects.features.tag.detail)
            implementation(projects.features.message.main)
            implementation(projects.features.calendar)
        }
    }
}