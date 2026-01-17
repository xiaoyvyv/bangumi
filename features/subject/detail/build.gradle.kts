plugins {
    id("bgm.library")
}


kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.features.subject.detail"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
            implementation(projects.shared.ui)
            implementation(projects.features.mono.page)
            implementation(projects.features.blog.page)
            implementation(projects.features.topic.page)
            implementation(projects.features.preview.album)
            implementation(projects.features.index.page)
            implementation(projects.features.subject.page)
        }
    }
}
