package io.github.vinceglb.autolaunch

import com.sun.jna.platform.win32.*
import kotlinx.coroutines.*
import kotlin.io.path.*

internal class PlatformAutoLaunchWindows(
    private val config: AutoLaunchConfig
) : PlatformAutoLaunch {
    override suspend fun isEnabled(): Boolean = withContext(Dispatchers.IO) {
        try {
            val value: String? = Advapi32Util.registryGetStringValue(
                /* root = */ WinReg.HKEY_CURRENT_USER,
                /* key = */ REGISTRY_KEY,
                /* value = */ config.appPackageName
            )
            value == "cmd /c start \"${config.appPackageName}\" /D \"${Path(config.appPath).parent}\" \"${config.appPath}\" --autostart=true"
        } catch (e: Win32Exception) {
            if (e.errorCode == 2) { // ERROR_FILE_NOT_FOUND
                false
            } else {
                throw e
            }
        }
    }

    override suspend fun enable(): Unit = withContext(Dispatchers.IO) {
        // Check if the application path exists
        val appPath = Path(config.appPath)
        if (appPath.notExists()) {
            throw NoSuchFileException(appPath.toFile())
        }

        // Create the registry key if it doesn't exist
        if (!isRegistryKeyExists()) {
            Advapi32Util.registryCreateKey(
                /* hKey = */ WinReg.HKEY_CURRENT_USER,
                /* keyName = */ REGISTRY_KEY
            )
        }

        // Set the value with the autostart argument
        Advapi32Util.registrySetStringValue(
            /* root = */ WinReg.HKEY_CURRENT_USER,
            /* keyPath = */ REGISTRY_KEY,
            /* name = */ config.appPackageName,
            /* value = */ "cmd /c start \"${config.appPackageName}\" /D \"${Path(config.appPath).parent}\" \"${config.appPath}\" --autostart=true"
        )
    }

    override suspend fun disable(): Unit = withContext(Dispatchers.IO) {
        if (isRegistryKeyExists()) {
            Advapi32Util.registryDeleteValue(
                /* root = */ WinReg.HKEY_CURRENT_USER,
                /* keyPath = */ REGISTRY_KEY,
                /* valueName = */ config.appPackageName
            )
        }
    }

    private fun isRegistryKeyExists(): Boolean =
        Advapi32Util.registryKeyExists(
            /* root = */ WinReg.HKEY_CURRENT_USER,
            /* key = */ REGISTRY_KEY
        )

    private companion object {
        private const val REGISTRY_KEY = "Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    }
}
