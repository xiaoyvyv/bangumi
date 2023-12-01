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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(project(":lib-live2d"))
    api(project(":lib-emoji"))

    api(libs.blueprint)
    api(libs.blueprint.floater)

    api(libs.android.lottie)
    api(libs.androidx.swiperefreshlayout)
    api(libs.touch.image.view)

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.activity)
    api(libs.androidx.fragment.ktx)
    api(libs.persistent.cookie.jar)

    api(libs.jsoup)

    ksp(libs.glide.ksp)
    annotationProcessor(libs.glide.compiler)

    compileOnly(files("../lib-live2d/libs/Live2DCubismCore.aar"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}