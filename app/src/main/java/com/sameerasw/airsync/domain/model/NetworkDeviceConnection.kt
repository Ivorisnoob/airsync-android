package com.sameerasw.airsync.domain.model

data class NetworkDeviceConnection(
    val deviceName: String,
    val networkConnections: Map<String, String>, // Map of our IP -> client IP
    val port: String,
    val lastConnected: Long,
    val isPlus: Boolean,
    val symmetricKey: String? = null
) {
    // get client IP for current network
    fun getClientIpForNetwork(ourIp: String): String? {
        return networkConnections[ourIp]
    }

    // create ConnectedDevice for current network
    fun toConnectedDevice(ourIp: String): ConnectedDevice? {
        val clientIp = getClientIpForNetwork(ourIp)
        return if (clientIp != null) {
            ConnectedDevice(
                name = deviceName,
                ipAddress = clientIp,
                port = this.port,
                lastConnected = this.lastConnected,
                isPlus = this.isPlus,
                symmetricKey = this.symmetricKey
            )
        } else null
    }
}