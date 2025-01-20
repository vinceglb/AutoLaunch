package io.github.vinceglb.autolaunch

import kotlinx.coroutines.*
import kotlin.io.path.*

internal class PlatformAutoLaunchMacOS(private val config: AutoLaunchConfig) : PlatformAutoLaunch {
    private val file =
        Path("${System.getProperty("user.home")}/Library/LaunchAgents/${config.appPackageName}.plist")

    override suspend fun isEnabled(): Boolean = withContext(Dispatchers.IO) {
        file.exists()
    }

    override suspend fun enable(): Unit = withContext(Dispatchers.IO) {
        file.writeText(
            """
            |<?xml version="1.0" encoding="UTF-8"?>
            |<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            |<plist version="1.0">
            |<dict>
            |    <key>Label</key>
            |    <string>${config.appPackageName}</string>
            |    <key>ProgramArguments</key>
            |    <array>
            |        <string>${config.appPath}</string>
            |        <string>--autostart=true</string>
            |    </array>
            |    <key>RunAtLoad</key>
            |    <true/>
            |</dict>
            |</plist>
            """.trimMargin()
        )
    }

    override suspend fun disable(): Unit = withContext(Dispatchers.IO) {
        file.deleteIfExists()
    }
}
