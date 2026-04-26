plugins {
    id("bgm.library")
}

kotlin {
    androidLibrary {
        namespace = "com.kyant.backdrop"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.ui.graphics)
        }

        val skikoMain by creating {
            dependsOn(commonMain.get())
        }
        iosMain.get().dependsOn(skikoMain)
        jvmMain.get().dependsOn(skikoMain)
    }
}
