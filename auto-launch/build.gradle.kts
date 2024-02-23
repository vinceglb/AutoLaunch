plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mavenPublishVanniktech)
    id("maven-publish")
}

group = "io.github.vinceglb"
version = "0.1.0"

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // Kotlin Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // JNA Platform
            // https://github.com/java-native-access/jna/tree/master?tab=readme-ov-file
            implementation(libs.jna.platform)
        }
    }
}
