package io.github.vinceglb.autolaunch

import co.touchlab.kermit.Logger
import java.io.File

class AutoLaunch(
    appPackageName: String,
    appPath: String = PlatformUtil.resolvedExecutable.absolutePath,
) {
    /**
     * Check if the app is set to auto launch
     */
    suspend fun isEnabled(): Boolean = platformAutoLaunch.isEnabled()

    /**
     * Enable the app to auto launch
     */
    suspend fun enable() {
        if (!isRunningFromDistributable) {
            logger.w { "Application must be distributed for AutoLaunch to work properly." }
        }

        platformAutoLaunch.enable()
    }

    /**
     * Disable the app from auto launching
     */
    suspend fun disable() = platformAutoLaunch.disable()

    companion object {
        /**
         * Get the app resolved executable path
         */
        val resolvedExecutable: File =
            PlatformUtil.resolvedExecutable

        /**
         * Determine whether the app is distributable
         */
        val isRunningFromDistributable: Boolean =
            PlatformUtil.isRunningFromDistributable
    }

    private val config = AutoLaunchConfig(
        appName = appPackageName,
        appPath = appPath
    )

    private val platformAutoLaunch: PlatformAutoLaunch = when (PlatformUtil.currentPlatform) {
        Platform.Linux -> PlatformAutoLaunchLinux(config)
        Platform.MacOS -> PlatformAutoLaunchMacOS(config)
        Platform.Windows -> PlatformAutoLaunchWindows(config)
    }

    private val logger = Logger.withTag("AutoLaunch")
}
