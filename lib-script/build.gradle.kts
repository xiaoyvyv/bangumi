import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}


kotlin {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(libs.jsoup)
    api(libs.sqlite.jdbc)
    api(libs.log4j.core)
    api(libs.slf4j.simple)

    api("com.huaban:jieba-analysis:1.0.2")
    api("com.google.code.gson:gson:2.10.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}