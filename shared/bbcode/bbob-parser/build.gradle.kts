plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.bbcode.parser"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.bbcode.bbobTypes)
            implementation(projects.shared.bbcode.bbobPluginHelper)
        }
    }
}