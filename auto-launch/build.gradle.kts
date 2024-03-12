plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mavenPublishVanniktech)
    id("maven-publish")
}

group = "io.github.vinceglb"
version = "0.1.0"

kotlin {
    jvm()
    jvmToolchain(17)
    
    sourceSets {
        commonMain.dependencies {
            // Kotlin Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // JNA Platform
            // https://github.com/java-native-access/jna/tree/master?tab=readme-ov-file
            implementation(libs.jna.platform)

            // Kermit logger
            // https://github.com/touchlab/Kermit?tab=readme-ov-file
            implementation(libs.kermit)
        }
    }
}
