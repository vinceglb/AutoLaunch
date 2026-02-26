import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.autolaunch.sample"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    // Compose
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(compose.desktop.currentOs)

    // AutoLaunch
    implementation(projects.autoLaunch)
}
