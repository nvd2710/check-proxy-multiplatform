package com.quickcheck.proxy.data

enum class ProxyType { HTTP, SOCKS5 }

data class ProxyEntry(
    val raw: String,
    val host: String,
    val port: Int,
    val user: String? = null,
    val pass: String? = null,
    val type: ProxyType = ProxyType.HTTP,
)

enum class CheckStatus { LIVE, DEAD }

data class ProxyResult(
    val entry: ProxyEntry,
    val status: CheckStatus,
    val latencyMs: Long? = null,
    val error: String? = null,
)
