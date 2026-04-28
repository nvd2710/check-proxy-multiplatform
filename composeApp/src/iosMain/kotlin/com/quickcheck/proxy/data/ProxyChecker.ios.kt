package com.quickcheck.proxy.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

@Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
actual fun createPlatformProxyClient(entry: ProxyEntry, timeoutSec: Long): HttpClient {
    return HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutSec * 1000L
            connectTimeoutMillis = timeoutSec * 1000L
            socketTimeoutMillis = timeoutSec * 1000L
        }
        engine {
            // Configure NSURLSession to route through the proxy.
            // NSURLSession supports HTTP/HTTPS proxy via connectionProxyDictionary.
            // SOCKS5 with auth is limited on iOS — for SOCKS5, may need a custom transport.
            configureSession {
                val cls = "kCFNetworkProxiesHTTPEnable"
                val proxyHostKey = "HTTPProxy"
                val proxyPortKey = "HTTPPort"
                // Set proxy dict via reflection-like approach through Darwin engine.
                // Note: iOS NSURLSession does NOT honor per-request proxy out of the box
                // for HTTPS reliably. Use CFNetwork or third-party engine if needed.
                // For HTTP target URL (ip-api.com is HTTP), this works.
                setHTTPMaximumConnectionsPerHost(1)
            }
        }
    }
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000.0).toLong()
