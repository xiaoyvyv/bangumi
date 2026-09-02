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
            implementation(libs.kmp.zip)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.ktor.client.mock)
        }
    }
}
