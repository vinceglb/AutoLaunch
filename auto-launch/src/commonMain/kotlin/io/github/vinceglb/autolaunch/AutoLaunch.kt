package io.github.vinceglb.autolaunch

import co.touchlab.kermit.Logger
import kotlin.io.path.absolutePathString

class AutoLaunch(
    appPackageName: String,
    appPath: String = PlatformUtil.resolvedExecutable.absolutePathString(),
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


    /**
     * Checks if the application was started with the '--autostart=true' argument.
     *
     * This method inspects the JVM input arguments to determine if the application
     * was launched through the autostart mechanism, by verifying if the specific
     * argument is present.
     *
     * @return true if the application was started with the '--autostart=true' argument, false otherwise.
     */
    fun isStartedViaAutostart(): Boolean {
        val inputArguments = System.getProperty("sun.java.command")?.split(" ") ?: emptyList()
        println("Arguments fournis: $inputArguments")
        return inputArguments.contains("--autostart=true")
    }



    companion object {
        /**
         * Get the app resolved executable path
         */
        val resolvedExecutable =
            PlatformUtil.resolvedExecutable

        /**
         * Determine whether the app is distributable
         */
        val isRunningFromDistributable: Boolean =
            PlatformUtil.isRunningFromDistributable
    }

    private val config = AutoLaunchConfig(
        appPackageName = appPackageName,
        appPath = appPath
    )

    private val platformAutoLaunch: PlatformAutoLaunch = when (PlatformUtil.currentPlatform) {
        Platform.Linux -> PlatformAutoLaunchLinux(config)
        Platform.MacOS -> PlatformAutoLaunchMacOS(config)
        Platform.Windows -> PlatformAutoLaunchWindows(config)
    }

    private val logger = Logger.withTag("AutoLaunch")
}
