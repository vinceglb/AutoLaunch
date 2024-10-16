package io.github.vinceglb.autolaunch

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinReg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

internal class PlatformAutoLaunchWindows(
    private val config: AutoLaunchConfig
) : PlatformAutoLaunch {
    override suspend fun isEnabled(): Boolean = withContext(Dispatchers.IO) {
        try {
            val value: String? = Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER,
                REGISTRY_KEY,
                config.appPackageName
            )
            value == "${config.appPath} --autostart=true"
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
        val appPath = File(config.appPath)
        if (!appPath.exists()) {
            throw FileNotFoundException("File not found: ${config.appPath}")
        }

        // Create the registry key if it doesn't exist
        if (!isRegistryKeyExists()) {
            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, REGISTRY_KEY)
        }

        // Set the value with the autostart argument
        Advapi32Util.registrySetStringValue(
            WinReg.HKEY_CURRENT_USER,
            REGISTRY_KEY,
            config.appPackageName,
            "${config.appPath} --autostart=true"
        )
    }

    override suspend fun disable(): Unit = withContext(Dispatchers.IO) {
        if (isRegistryKeyExists()) {
            Advapi32Util.registryDeleteValue(
                WinReg.HKEY_CURRENT_USER,
                REGISTRY_KEY,
                config.appPackageName
            )
        }
    }

    private fun isRegistryKeyExists(): Boolean {
        return Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, REGISTRY_KEY)
    }

    private companion object {
        private const val REGISTRY_KEY = "Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    }
}