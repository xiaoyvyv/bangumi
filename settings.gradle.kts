@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jogamp.org/deployment/maven")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://jitpack.io")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "bangumi-multiplatform"

include(":baselineprofile")
include(":android")
include(":composeApp")
include(":shared:core")
include(":shared:core_native")
include(":shared:core_avif")
include(":shared:core_resource")
include(":shared:data")
include(":shared:ui")
include(":features:almanac")
include(":features:sign:sign_in")
include(":features:sign:sign_up")
include(":features:splash")
include(":features:message:main")
include(":features:message:chat")
include(":features:notification")
include(":features:detect")
include(":features:main")
include(":features:main_tab:home")
include(":features:main_tab:profile")
include(":features:main_tab:timeline")
include(":features:main_tab:topic")
include(":features:main_tab:tracking")
include(":features:main_tab:newest")
include(":features:settings:main")
include(":features:settings:account")
include(":features:settings:bar")
include(":features:settings:block")
include(":features:settings:live2d")
include(":features:settings:network")
include(":features:settings:privacy")
include(":features:settings:translate")
include(":features:settings:ui")
include(":features:mikan:studio")
include(":features:mikan:detail")
include(":features:search:input")
include(":features:search:result")
include(":features:subject:detail")
include(":features:subject:page")
include(":features:subject:browser")
include(":features:mono:detail")
include(":features:mono:page")
include(":features:mono:browser")
include(":features:preview:gallery")
include(":features:preview:album")
include(":features:preview:text")
include(":features:preview:main")
include(":features:pixiv:main")
include(":features:pixiv:login")
include(":features:article")
include(":features:web")
include(":features:dollars")
include(":features:blog:page")
include(":features:groups:detail")
include(":features:groups:page")
include(":features:index:detail")
include(":features:index:page")
include(":features:tag:detail")
include(":features:tag:page")
include(":features:topic:page")
include(":features:topic:detail")
include(":features:friend")
include(":features:user")
include(":features:calendar")
include(":features:garden")
include(":features:timeline:page")
include(":features:timeline:detail")