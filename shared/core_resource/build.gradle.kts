plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.resource"
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always

    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = provider { layout.projectDirectory.dir("src/androidMain/res") }
    )
}