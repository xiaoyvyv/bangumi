plugins {
    id("bgm.library")
}

tasks.withType<Test>().configureEach {
    testLogging {
        showStandardStreams = true
    }
}

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.data.workflow"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.coreNative)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
