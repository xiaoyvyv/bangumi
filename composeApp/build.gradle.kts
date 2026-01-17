import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose.hot-reload")
    alias(libs.plugins.baselineprofile)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

kotlin {
    jvmToolchain(21)

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop") {

    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.android.immersionbar)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.preview)
            implementation(compose.components.resources)
            implementation(libs.bundles.compose.common)
            implementation(libs.bundles.kotlinx)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin.common)
            implementation(libs.bundles.coil3.common)

            implementation(projects.shared.ui)
            implementation(projects.shared.core)
            implementation(projects.shared.coreNative)
            implementation(projects.shared.coreResource)
            implementation(projects.shared.data)
            implementation(projects.features.detect)
            implementation(projects.features.splash)
            implementation(projects.features.sign.signIn)
            implementation(projects.features.sign.signUp)
            implementation(projects.features.main)
            implementation(projects.features.mainTab.home)
            implementation(projects.features.mainTab.timeline)
            implementation(projects.features.mainTab.topic)
            implementation(projects.features.mainTab.tracking)
            implementation(projects.features.mainTab.profile)
            implementation(projects.features.mainTab.newest)
            implementation(projects.features.settings.main)
            implementation(projects.features.settings.account)
            implementation(projects.features.settings.bar)
            implementation(projects.features.settings.block)
            implementation(projects.features.settings.live2d)
            implementation(projects.features.settings.network)
            implementation(projects.features.settings.privacy)
            implementation(projects.features.settings.translate)
            implementation(projects.features.settings.ui)
            implementation(projects.features.message.main)
            implementation(projects.features.message.chat)
            implementation(projects.features.notification)
            implementation(projects.features.mikan.detail)
            implementation(projects.features.mikan.studio)
            implementation(projects.features.search.input)
            implementation(projects.features.search.result)
            implementation(projects.features.subject.detail)
            implementation(projects.features.subject.page)
            implementation(projects.features.subject.browser)
            implementation(projects.features.mono.detail)
            implementation(projects.features.mono.page)
            implementation(projects.features.mono.browser)
            implementation(projects.features.preview.album)
            implementation(projects.features.preview.gallery)
            implementation(projects.features.preview.main)
            implementation(projects.features.preview.text)
            implementation(projects.features.web)
            implementation(projects.features.article)
            implementation(projects.features.dollars)
            implementation(projects.features.friend)
            implementation(projects.features.blog.page)
            implementation(projects.features.index.page)
            implementation(projects.features.index.detail)
            implementation(projects.features.groups.page)
            implementation(projects.features.groups.detail)
            implementation(projects.features.topic.page)
            implementation(projects.features.topic.detail)
            implementation(projects.features.user)
            implementation(projects.features.almanac)
            implementation(projects.features.timeline.page)
            implementation(projects.features.timeline.detail)
            implementation(projects.features.calendar)
            implementation(projects.features.garden)
            implementation(projects.features.tag.page)
            implementation(projects.features.tag.detail)
            implementation(projects.features.pixiv.main)
            implementation(projects.features.pixiv.login)
        }
    }
}

android {
    namespace = "com.xiaoyv.bangumi"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.xiaoyv.bangumi.multiplatform"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunnerArguments.put("clearPackageData", "false")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/composeApp/keystore/why.keystore")
            storePassword = "why981229"
            keyAlias = "whykey"
            keyPassword = "why981229"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard.pro"
            )
        }

        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    debugImplementation(libs.compose.ui.tooling)
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.xiaoyv.bangumi.resources"
    generateResClass = auto
}


compose.desktop {
    application {

        buildTypes.release.proguard {
            version.set("7.5.0")
            configurationFiles.from("proguard.pro")
        }

        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")

        if (System.getProperty("os.name").contains("Mac", true)) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }

        mainClass = "com.xiaoyv.bangumi.MainKt"
        nativeDistributions {
            modules("java.base", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.xiaoyv.bangumi"
            packageVersion = "1.0.0"
        }
    }
}

/*

tasks.whenTaskAdded {
    if (name.contains("NativeLibs")) {
        doFirst {
            println("list so files begin")
            inputs.files.forEach { file ->
                printSoPath(file)
            }
            println("list so files end")
        }
    }
}

fun printSoPath(file: File?) {
    if (file != null) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                printSoPath(it)
            }
        } else if (file.absolutePath.endsWith(".so")) {
            println("so file: ${file.absolutePath}")
        }
    }
}
*/
