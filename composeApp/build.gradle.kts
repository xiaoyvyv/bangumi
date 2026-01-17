import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose.hot-reload")
    id("com.android.kotlin.multiplatform.library")
}


kotlin {
    jvmToolchain(21)

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
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    androidLibrary {
        namespace = "com.xiaoyv.bangumi.compose"

        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        lint {
            targetSdk = libs.versions.android.targetSdk.get().toInt()
        }

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        enableCoreLibraryDesugaring = false

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
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