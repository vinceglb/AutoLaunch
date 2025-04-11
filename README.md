<div align="center">
  <img src="https://github.com/vinceglb/AutoLaunch/assets/24540801/4bb241fb-7a23-47bb-99fa-cbc937ef9966" alt="AutoLaunch Kotlin" />

  <br>
  
  <h1>AutoLaunch Kotlin</h1>
  <p>Lightweight Kotlin library to enable auto-launch on system startup.</p>

  <div>
    <img src="https://img.shields.io/maven-central/v/io.github.vinceglb/auto-launch" alt="AutoLaunch Maven Version" />
    <img src="https://img.shields.io/badge/Platform-JVM-red.svg?logo=openjdk" alt="Badge JVM" />
  </div>
</div>

## 📦 Installation

AutoLaunch targets JVM platform.

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.vinceglb:auto-launch:0.6.0")
}
```

## 🚀 Quick start

```kotlin
// Create an instance of AutoLaunch.
val autoLaunch = AutoLaunch(appPackageName = "com.autolaunch.sample")

// Enable or disable launch at startup.
autoLaunch.enable()
autoLaunch.disable()

// Check if auto launch is enabled.
val isEnabled = autoLaunch.isEnabled()

// Check if the app was started by autostart.
val isStartedViaAutostart = autoLaunch.isStartedViaAutostart()
```

> ℹ️ **Note**: To test the auto-launch feature, your application must be distributed. With Compose Multiplatform, you can run a distributable package using `./gradlew :runDistributable`

## 📖 Advanced

### ⚡️ Convenient methods

```kotlin
// Get the app resolved executable path
val appPath = AutoLaunch.resolvedExecutable

// Determine whether the app is distributable
val isDistributable = AutoLaunch.isRunningFromDistributable
```

### 🔧 Customizing the application path

By default, your application path is detected automatically. You can customize the application path that will be launched at startup:
- MacOS: something like `/Applications/JetBrains Toolbox.app/Contents/MacOS/jetbrains-toolbox`.
- Windows: the path to the `.exe` file.

```kotlin
val autoLaunch = AutoLaunch(
    appPackageName = "com.autolaunch.sample",
    appPath = "/path/to/your/app"
)
```

## ✨ Behind the scene

Depending on the platform, AutoLaunch uses the following techniques:

- MacOS: create a plist file in `~/Library/LaunchAgents/` directory.
- Windows: create a registry key in `HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run`.
- Linux: create a `.desktop` file in `~/.config/autostart/` directory.
    If there is no `xdg-desktop-menu` -> systemd will be used to enable a daemon.

## 🌱 Sample project

You can find a sample project in the `sample` directory. Run the following command to test the auto-launch feature:

```shell
:sample:runDistributable
``` 

## 😎 Contribution

Your contributions are welcome 🔥 Here are some features that are missing:

- [x] Linux support
- [ ] Pass optional arguments to the application

---

Made with ❤️ by Vince
