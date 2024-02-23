class AutoLaunch(config: AutoLaunchConfig) {
    private val platformAutoLaunch: PlatformAutoLaunch = when (currentPlatform) {
        Platform.Linux -> PlatformAutoLaunchLinux(config)
        Platform.MacOS -> PlatformAutoLaunchMacOS(config)
        Platform.Windows -> PlatformAutoLaunchWindows(config)
    }

    suspend fun isEnabled(): Boolean = platformAutoLaunch.isEnabled()

    suspend fun enable() = platformAutoLaunch.enable()

    suspend fun disable() = platformAutoLaunch.disable()
}

data class AutoLaunchConfig(
    val appName: String,
    val appPath: String,
)