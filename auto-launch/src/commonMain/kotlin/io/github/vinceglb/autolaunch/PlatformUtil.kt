package io.github.vinceglb.autolaunch

import java.io.File

internal object PlatformUtil {
    val resolvedExecutable: File =
        File(ProcessHandle.current().info().command().get())

    val isRunningFromDistributable: Boolean =
        resolvedExecutable.nameWithoutExtension != "java"

    val currentPlatform: Platform
        get() {
            val system = System.getProperty("os.name").lowercase()
            return if (system.contains("win")) {
                Platform.Windows
            } else if (
                system.contains("nix") ||
                system.contains("nux") ||
                system.contains("aix")
            ) {
                Platform.Linux
            } else if (system.contains("mac")) {
                Platform.MacOS
            } else {
                Platform.Linux
            }
        }
}

internal enum class Platform {
    Linux,
    MacOS,
    Windows
}
