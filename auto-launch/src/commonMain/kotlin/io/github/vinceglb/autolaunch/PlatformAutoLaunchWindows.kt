package io.github.vinceglb.autolaunch

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PlatformAutoLaunchWindows(private val config: AutoLaunchConfig) :
    PlatformAutoLaunch {
    override suspend fun isEnabled(): Boolean = withContext(Dispatchers.IO) {
        val value: String? = Advapi32Util.registryGetStringValue(
            WinReg.HKEY_CURRENT_USER,
            REGISTRY_KEY,
            config.appName
        )
        value == config.appPath
    }

    override suspend fun enable(): Unit = withContext(Dispatchers.IO) {
        // Create the registry key if it doesn't exist
        if (!isRegistryKeyExists()) {
            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, REGISTRY_KEY)
        }

        // Set the value
        Advapi32Util.registrySetStringValue(
            WinReg.HKEY_CURRENT_USER,
            REGISTRY_KEY,
            config.appName,
            config.appPath
        )
    }

    override suspend fun disable(): Unit = withContext(Dispatchers.IO) {
        if (isRegistryKeyExists()) {
            Advapi32Util.registryDeleteValue(
                WinReg.HKEY_CURRENT_USER,
                REGISTRY_KEY,
                config.appName
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
