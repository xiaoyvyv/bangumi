plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.xiaoyv.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(project(":lib-live2d"))
    api(project(":lib-emoji"))
    api(project(":lib-thunder"))

    api(libs.blueprint)
    api(libs.blueprint.floater)

    api(libs.android.lottie)
    api(libs.androidx.swiperefreshlayout)

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.activity)
    api(libs.androidx.fragment.ktx)
    api(libs.persistent.cookie.jar)

    api(libs.androidx.work.runtime.ktx)
    api(libs.subsampling.scale.image.view.androidx)
    api(libs.speed.dial)

    api(libs.androidx.core.splashscreen)

    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    api(libs.vico.core)
    api(libs.vico.views)

    api(libs.jsoup)
    api(libs.avif.integration)
    ksp(libs.glide.ksp)

    api(libs.jieba.analysis)

    api(platform(libs.firebase.bom))
    api(libs.firebase.analytics.ktx)
    api(libs.firebase.crashlytics.ktx)

    compileOnly(files("../lib-live2d/libs/Live2DCubismCore.aar"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}