package io.github.vinceglb.autolaunch

import java.io.File

internal class PlatformAutoLaunchLinux(private val config: AutoLaunchConfig) : PlatformAutoLaunch {

    // Checks if the application is installed by looking for a corresponding .desktop file in /usr/share/applications
    private fun isInstalled(): Boolean {
        val appPackageName = config.appPackageName
        val applicationsDirectory = File("/usr/share/applications")
        val desktopFile = applicationsDirectory.listFiles()?.find { it.name.contains(appPackageName) && it.name.endsWith(".desktop") }
        val isInstalled = desktopFile != null
        println("Checking if app is installed: $isInstalled (package: $appPackageName)")
        return isInstalled
    }

    // Retrieves the content of the application's .desktop file from /usr/share/applications
    private fun getDesktopFileContent(): String? {
        val appPackageName = config.appPackageName
        val applicationsDirectory = File("/usr/share/applications")
        val desktopFile = applicationsDirectory.listFiles()?.find { it.name.contains(appPackageName) && it.name.endsWith(".desktop") }
        return if (desktopFile != null && desktopFile.exists()) {
            println("Reading desktop file content from: ${desktopFile.path}")
            desktopFile.readText()
        } else {
            println("Desktop file not found for package: $appPackageName")
            null
        }
    }

    // Modifies the content of the .desktop file to add necessary entries for autostart
    private fun modifyDesktopFileContent(content: String): String {
        val updatedContent = content.lines().toMutableList()
        // Ensure the file contains the necessary entries for autostart
        val entries = listOf(
            "X-GNOME-Autostart-enabled=true",
            "StartupNotify=false",
            "X-GNOME-Autostart-Delay=10",
            "X-MATE-Autostart-Delay=10",
            "X-KDE-autostart-after=panel"
        )

        entries.forEach { entry ->
            val key = entry.substringBefore("=")
            val existingIndex = updatedContent.indexOfFirst { it.startsWith(key) }
            if (existingIndex != -1) {
                println("Updating existing entry: $key")
                updatedContent[existingIndex] = entry
            } else {
                println("Adding new entry: $entry")
                updatedContent.add(entry)
            }
        }

        return updatedContent.joinToString("\n")
    }

    // Writes the modified .desktop file to the ~/.config/autostart directory
    private fun writeAutostartDesktopFile(content: String) {
        val autostartDirectory = File(System.getProperty("user.home"), ".config/autostart")
        if (!autostartDirectory.exists()) {
            println("Creating autostart directory at: ${autostartDirectory.path}")
            autostartDirectory.mkdirs()
        }
        val autostartFile = File(autostartDirectory, "${config.appPackageName}.desktop")
        println("Writing autostart desktop file to: ${autostartFile.path}")
        autostartFile.writeText(content)
    }

    // Checks if autostart is enabled by looking for the .desktop file in ~/.config/autostart
    override suspend fun isEnabled(): Boolean {
        val autostartFile = File(System.getProperty("user.home"), ".config/autostart/${config.appPackageName}.desktop")
        val isEnabled = autostartFile.exists()
        println("Checking if autostart is enabled: $isEnabled (path: ${autostartFile.path})")
        return isEnabled
    }

    // Enables autostart by copying and modifying the application's .desktop file
    override suspend fun enable() {
        println("Enabling autostart for app: ${config.appPackageName}")
        if (isInstalled()) {
            val desktopFileContent = getDesktopFileContent()
            if (desktopFileContent != null) {
                val modifiedContent = modifyDesktopFileContent(desktopFileContent)
                writeAutostartDesktopFile(modifiedContent)
            } else {
                println("Failed to enable autostart: desktop file content is null")
            }
        } else {
            println("Failed to enable autostart: app is not installed")
        }
    }

    // Disables autostart by deleting the .desktop file in ~/.config/autostart
    override suspend fun disable() {
        println("Disabling autostart for app: ${config.appPackageName}")
        val autostartFile = File(System.getProperty("user.home"), ".config/autostart/${config.appPackageName}.desktop")
        if (autostartFile.exists()) {
            println("Deleting autostart desktop file at: ${autostartFile.path}")
            autostartFile.delete()
        } else {
            println("Autostart desktop file not found at: ${autostartFile.path}")
        }
    }
}

/*
To test this functionality, it is necessary to create a .deb package and install it on the system.
Without installation via a .deb, no .desktop file will be automatically created in /usr/share/applications,
which makes it impossible to verify the presence of the application and enable autostart.
*/