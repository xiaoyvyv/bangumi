plugins {
    id("bgm.library")
}


kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.main.tab.home"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)

            implementation(projects.features.subject.page)
            implementation(projects.features.mono.page)
            implementation(projects.features.index.page)
            implementation(projects.features.blog.page)
            implementation(projects.features.groups.page)
            implementation(projects.features.friend)
        }
    }
}