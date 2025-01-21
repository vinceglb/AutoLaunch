package io.github.vinceglb.autolaunch

import kotlin.io.path.*

internal object PlatformUtil {
    val resolvedExecutable =
        Path(ProcessHandle.current().info().command().get())

    val isRunningFromDistributable: Boolean =
        resolvedExecutable.nameWithoutExtension != "java"

    val currentPlatform: Platform
        get() = System.getProperty("os.name").lowercase().let { system ->
            when {
                system.contains("win") -> Platform.Windows
                system.contains("mac") -> Platform.MacOS
                else -> Platform.Linux
            }
        }
}

internal enum class Platform {
    Linux,
    MacOS,
    Windows
}
