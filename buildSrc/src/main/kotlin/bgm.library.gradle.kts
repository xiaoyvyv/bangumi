@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.kotlin.multiplatform.library")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes"
        )
    }

    jvmToolchain(21)

    applyDefaultHierarchyTemplate()

    iosArm64()
    iosSimulatorArm64()

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    android {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()
        withHostTest {}

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

    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.android.immersionbar)

            implementation(libs.bundles.coil3.android)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)

            implementation(libs.tinypinyin.android)
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.graphics)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.resources)

            implementation(libs.bundles.compose.common)

            implementation(libs.cryptohash)
            implementation(libs.cryptorandom)

            implementation(libs.ksoup)
            implementation(libs.ksoup.html)

            implementation(libs.tinypinyin.core)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin.common)

            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.ktorfit.common)

            implementation(libs.bundles.coil3.common)
            implementation(libs.bundles.markdown)
            implementation(libs.bundles.zoomimage)
            implementation(libs.bundles.vico)
            implementation(libs.bundles.file.kit)
            implementation(libs.bundles.krop)
            implementation(libs.bundles.kotlinx)

            // Sqlite
            implementation(libs.sqldelight.runtime)

            // DataStore
            api(libs.androidx.datastore.preferences)
            api(libs.androidx.datastore.preferences.core)
            api(libs.androidx.datastore.core.okio)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)

            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.tinypinyin.jvm)
        }

        all {
            languageSettings {
                optIn("kotlin.io.encoding.ExperimentalEncodingApi")
                optIn("org.koin.core.annotation.KoinExperimentalAPI")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
                optIn("androidx.compose.foundation.layout.ExperimentalLayoutApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.paging.ExperimentalPagingApi")
                optIn("org.orbitmvi.orbit.annotation.OrbitExperimental")
                optIn("androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi")
            }
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}

compose.resources {
    publicResClass = false
    generateResClass = never
    packageOfResClass = "com.xiaoyv.bangumi.$composeResourceId.resources"
}

tasks.register<GenerateMviTask>("generateMvi") {
    description = "generate mvi files"
    group = "bangumi"

    val parentName = project.parent?.name.orEmpty()
        .let { if (it == "features") "" else it }
        .uppercaseFirstChar()

    val codeNamespace = requireNotNull(project.kotlin.android.namespace)
    val codeDir = layout.projectDirectory
        .dir("src/commonMain/kotlin/${codeNamespace.replace(".", "/")}")
        .also { mkdir(it) }

    moduleName.set(parentName + project.name.split("_").joinToString("") { it.uppercaseFirstChar() })
    namespace.set(codeNamespace)
    namespaceDir.set(codeDir)
}
