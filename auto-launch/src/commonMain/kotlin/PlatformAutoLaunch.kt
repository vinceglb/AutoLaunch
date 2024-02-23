internal interface PlatformAutoLaunch {
    suspend fun isEnabled(): Boolean
    suspend fun enable()
    suspend fun disable()
}
