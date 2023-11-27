@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven {
            credentials {
                username = "6063d39b68e8851acef6a97a"
                password = "1Vl]B4PKU2ee"
            }
            url = uri("https://packages.aliyun.com/maven/repository/2090598-release-Z0kMEw")
        }
        google()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/releases")
        }
    }
}

rootProject.name = "Bangumi"
include(":app")
include(":lib-common")
include(":lib-live2d")
include(":lib-emoji")


