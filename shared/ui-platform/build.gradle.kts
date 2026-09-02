plugins {
    id("bgm.library")
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.ui.platform"
    }

    iosArm64()
    iosSimulatorArm64()
}
