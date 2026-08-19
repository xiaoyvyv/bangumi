plugins {
    id("bgm.library")
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.kotlinKtorfit)
}

//ktorfit {
//    // see https://github.com/Foso/Ktorfit/tree/master/ktorfit-compiler-plugin
//    compilerPluginVersion.set("2.3.5")
//}

/*afterEvaluate {
    tasks.named("extractAndroidMainAnnotations") {
        dependsOn(tasks.named("kspAndroidMain"))
        dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
    }
}*/

kotlin {
    android {
        namespace = "com.xiaoyv.bangumi.shared.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
        }
    }
}
