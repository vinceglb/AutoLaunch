plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mavenPublishVanniktech)
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // JNA Platform
            // https://github.com/java-native-access/jna/tree/master?tab=readme-ov-file
            implementation(libs.jna.platform)
        }
    }
}
