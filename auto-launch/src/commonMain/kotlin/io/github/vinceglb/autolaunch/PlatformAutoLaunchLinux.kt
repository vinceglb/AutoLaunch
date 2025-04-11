package io.github.vinceglb.autolaunch

import co.touchlab.kermit.Logger
import kotlin.io.path.*

internal class PlatformAutoLaunchLinux(private val config: AutoLaunchConfig) : PlatformAutoLaunch {
    private val logger = Logger.withTag("PlatformAutoLaunchLinux")
    // Checks if the application is installed
    // by looking for a corresponding .desktop file in /usr/share/applications and /opt
    private fun isInstalled(): Boolean {
        val appPackageName = config.appPackageName
        val applicationsDirectory = Path("/usr/share/applications")
        // The path will be lowercase and without spaces regarding JPackage
        val desktopFile = applicationsDirectory.listDirectoryEntries().find {
            it.name.endsWith("${appPackageName.replace(" ", "_")}.desktop")
        }

        // Check if the app is installed in /opt. The path will be lowercase and without spaces regarding JPackage
        val optFile = Path("/opt").listDirectoryEntries().find {
            it.name.equals(other = appPackageName.replace(" ", "-"), ignoreCase = true)
        }

        val isInstalled = desktopFile != null || optFile != null
        logger.d { "Checking if app is installed: $isInstalled (package: $appPackageName)" }
        return isInstalled
    }

    // Retrieves the content of the application's .desktop file from /usr/share/applications
    private fun getDesktopFileContent(): String? {
        val appPackageName = config.appPackageName
        val applicationsDirectory = Path("/usr/share/applications")
        val desktopFile = applicationsDirectory.listDirectoryEntries().find {
            it.name.endsWith("${appPackageName.replace(" ", "_")}.desktop")
        }
        return if (desktopFile != null && desktopFile.exists()) {
            logger.d { "Reading desktop file content from: $desktopFile" }
            desktopFile.readText()
        } else {
            logger.d { "Desktop file not found for package: $appPackageName" }
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
                logger.d { "Updating existing entry: $key" }
                updatedContent[existingIndex] = entry
            } else {
                logger.d { "Adding new entry: $entry" }
                updatedContent.add(entry)
            }
        }

        // Add --autostart=true to the Exec line
        val execIndex = updatedContent.indexOfFirst { it.startsWith("Exec=") }
        if (execIndex != -1) {
            val execLine = updatedContent[execIndex]
            if (!execLine.contains("--autostart=true")) {
                logger.d { "Adding --autostart=true to the Exec line" }
                updatedContent[execIndex] = "$execLine --autostart=true"
            }
        } else {
            // If Exec line does not exist, create a new one
            logger.d { "Adding new Exec line with --autostart=true" }
            updatedContent.add("Exec=${config.appPath} --autostart=true")
        }

        return updatedContent.joinToString("\n")
    }


    // Writes the modified .desktop file to the ~/.config/autostart directory
    private fun writeAutostartDesktopFile(content: String) {
        val autostartDirectory = Path(System.getProperty("user.home"), ".config/autostart")
        if (!autostartDirectory.exists()) {
            logger.d { "Creating autostart directory at: $autostartDirectory" }
            autostartDirectory.createDirectories()
        }
        val autostartFile = autostartDirectory.resolve("${config.appPackageName}.desktop")
        logger.d { "Writing autostart desktop file to: $autostartFile" }
        autostartFile.writeText(content)
    }

    // Writes a systemd service file to enable autostart
    private fun writeSystemdService() {
        val appPackageName = config.appPackageName
        val appPath = appPackageName.replace(" ", "-").lowercase()
        val servicePath = Path(System.getProperty("user.home"))
            .resolve(".config/systemd/user/$appPath.service")
        val serviceContent = """
            |[Unit]
            |Description=$appPackageName
            |After=network.target
            |
            |[Service]
            |Restart=on-failure
            |ExecStart=/opt/$appPath/bin/'$appPackageName'
            |
            |[Install]
            |WantedBy=default.target
        """.trimMargin()
        if (servicePath.exists()) {
            logger.d { "Service $appPath already exists at $servicePath" }

            if (servicePath.readText() == serviceContent) {
                logger.d { "Service $appPath is already up to date" }
                return
            }
            servicePath.deleteExisting()
        }
        try {
            if (!servicePath.exists()) {
                try {
                    servicePath.createParentDirectories()
                    servicePath.createFile()
                } catch (_: FileAlreadyExistsException) {
                    // Ignore
                }
                servicePath.writeText(serviceContent)
            }
            enableSystemdService(appPath)
            logger.d { "Service $servicePath has been updated" }
        } catch (e: Exception) {
            logger.d { "Failed to enable auto start as systemd service: $e" }
        }
    }

    // Enables the systemd service to start the application on boot
    private fun enableSystemdService(appPath: String) {
        ProcessBuilder("systemctl", "--user", "daemon-reload")
            .inheritIO()
            .start()
            .waitFor()
        ProcessBuilder("systemctl", "--user", "enable", "$appPath.service")
            .inheritIO()
            .start()
            .waitFor()
        ProcessBuilder("systemctl", "--user", "restart", "$appPath.service")
            .inheritIO()
            .start()
            .waitFor()
    }

    // Disables the systemd service
    private fun disableSystemdService(appPath: String) {
        ProcessBuilder("systemctl", "--user", "disable", "$appPath.service")
            .inheritIO()
            .start()
            .waitFor()
        ProcessBuilder("systemctl", "--user", "stop", "$appPath.service")
            .inheritIO()
            .start()
            .waitFor()
    }

    // Checks if autostart is enabled by looking for the .desktop file in ~/.config/autostart
    override suspend fun isEnabled(): Boolean {
        val appPackageName = config.appPackageName
        val autostartFile = Path(System.getProperty("user.home"), ".config/autostart/$appPackageName.desktop")
        val isEnabledDesktop = autostartFile.exists()
        logger.d { "Checking if autostart is enabled: $isEnabledDesktop (path: $autostartFile)" }

        val appPath = appPackageName.replace(" ", "-").lowercase()
        val statusProcess = ProcessBuilder("systemctl", "--user", "status", "$appPath.service")
            .redirectErrorStream(true)
            .start()
        val statusOutput = statusProcess.inputStream.bufferedReader().readText()
        statusProcess.waitFor()
        val isEnabledSystemd = statusOutput.contains("Active: active (running)")
        logger.d {"Checking if systemd is enabled: $isEnabledSystemd (path: $appPath.service)" }

        val isEnabled = isEnabledDesktop || isEnabledSystemd
        return isEnabled
    }

    // Enables autostart by copying and modifying the application's .desktop file
    override suspend fun enable() {
        logger.d { "Enabling autostart for app: ${config.appPackageName}" }
        if (isInstalled()) {
            val desktopFileContent = getDesktopFileContent()
            if (desktopFileContent != null) {
                val modifiedContent = modifyDesktopFileContent(desktopFileContent)
                writeAutostartDesktopFile(modifiedContent)
            } else {
                logger.d { "Desktop file content is null. Trying to enable autostart via systemd service." }
                writeSystemdService()
            }
        } else {
            logger.d { "Failed to enable autostart: app is not installed" }
        }
    }

    // Disables autostart by deleting the .desktop file in ~/.config/autostart
    override suspend fun disable() {
        val appPackageName = config.appPackageName
        logger.d { "Disabling autostart for app: $appPackageName" }
        val autostartFile = Path(System.getProperty("user.home"), ".config/autostart/$appPackageName.desktop")
        if (autostartFile.exists()) {
            logger.d { "Deleting autostart desktop file at: $autostartFile" }
            autostartFile.deleteIfExists()
        } else {
            logger.d { "Autostart desktop file not found at: $autostartFile" }
            logger.d { "Disabling systemd for app: $appPackageName" }
            disableSystemdService(appPackageName.replace(" ", "-").lowercase())
        }
    }
}
