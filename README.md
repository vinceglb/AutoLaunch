<div align="center">
  <img src="https://github.com/vinceglb/AutoLaunch/assets/24540801/4bb241fb-7a23-47bb-99fa-cbc937ef9966" alt="AutoLaunch Kotlin" />

  <br>
  
  <h1>AutoLaunch Kotlin</h1>
  <p>Launch your Kotlin Desktop / JVM app automatically on system startup.</p>

  <div>
    <img src="https://img.shields.io/maven-central/v/io.github.vinceglb/auto-launch" alt="AutoLaunch Maven Version" />
    <img src="https://img.shields.io/badge/Platform-JVM-red.svg?logo=openjdk" alt="Badge JVM" />
  </div>
</div>

## 📦 Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.vinceglb:auto-launch:0.1.0")
}
```

## 🧑‍💻 Usage

Create AutoLauch instance by passing your application package name.

```kotlin
val autoLaunch = AutoLaunch(appPackageName = "com.autolaunch.sample")
```

## Behind the scene

