plugins {
    `kotlin-dsl`
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.bundles.gradle.plugins)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
