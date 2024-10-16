plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mavenPublishVanniktech)
}

kotlin {
    jvm()
    
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
