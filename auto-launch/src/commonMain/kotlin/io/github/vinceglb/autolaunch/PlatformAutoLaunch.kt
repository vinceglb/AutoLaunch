package io.github.vinceglb.autolaunch

internal interface PlatformAutoLaunch {
    suspend fun isEnabled(): Boolean
    suspend fun enable()
    suspend fun disable()
}

internal data class AutoLaunchConfig(
    val appPackageName: String,
    val appPath: String,
)
