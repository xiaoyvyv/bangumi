plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.bbcode.compose"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared.bbcode.bbobTypes)
            api(projects.shared.bbcode.bbobPluginHelper)
            api(projects.shared.bbcode.bbobParser)
        }
    }
}