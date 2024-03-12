package io.github.vinceglb.autolaunch

internal interface PlatformAutoLaunch {
    suspend fun isEnabled(): Boolean
    suspend fun enable()
    suspend fun disable()
}

internal data class AutoLaunchConfig(
    val appName: String,
    val appPath: String,
)
