plugins {
    id("bgm.library")
}

android {
    namespace = "com.xiaoyv.bangumi.shared.resource"
}

kotlin {
}

compose.resources {
    publicResClass = true
    generateResClass = always

    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = provider { layout.projectDirectory.dir("src/androidMain/res") }
    )
}