plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.xiaoyv.bangumi.shared.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.data)
        }
    }
}

dependencies {
//    debugImplementation(libs.compose.ui.tooling)
    "androidRuntimeClasspath"(libs.compose.ui.tooling)
}

