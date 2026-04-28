@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.quickcheck.proxy.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/**
 * iOS HttpClient — uses Ktor Darwin engine.
 *
 * NOTE: Proxy routing through NSURLSession requires custom configuration via
 * `connectionProxyDictionary` which Ktor's Darwin engine does not expose
 * cleanly. Right now this returns a vanilla client without proxy support.
 * Will add proxy routing in a follow-up once the basic skeleton is verified
 * to build & launch on a real iPhone.
 */
actual fun createPlatformProxyClient(entry: ProxyEntry, timeoutSec: Long): HttpClient {
    return HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutSec * 1000L
            connectTimeoutMillis = timeoutSec * 1000L
            socketTimeoutMillis = timeoutSec * 1000L
        }
    }
}

actual fun currentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000.0).toLong()
