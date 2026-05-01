plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.bbcode.helper"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.bbcode.bbobTypes)
        }
    }
}