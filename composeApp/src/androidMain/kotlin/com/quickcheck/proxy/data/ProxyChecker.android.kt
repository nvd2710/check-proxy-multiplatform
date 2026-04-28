package com.quickcheck.proxy.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import okhttp3.Authenticator
import okhttp3.Credentials
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

actual fun createPlatformProxyClient(entry: ProxyEntry, timeoutSec: Long): HttpClient {
    val javaProxyType = if (entry.type == ProxyType.SOCKS5) Proxy.Type.SOCKS else Proxy.Type.HTTP
    val javaProxy = Proxy(javaProxyType, InetSocketAddress.createUnresolved(entry.host, entry.port))

    return HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutSec * 1000L
            connectTimeoutMillis = timeoutSec * 1000L
            socketTimeoutMillis = timeoutSec * 1000L
        }
        engine {
            config {
                proxy(javaProxy)
                retryOnConnectionFailure(false)
                connectTimeout(timeoutSec, TimeUnit.SECONDS)
                readTimeout(timeoutSec, TimeUnit.SECONDS)
                writeTimeout(timeoutSec, TimeUnit.SECONDS)

                if (entry.type == ProxyType.HTTP && !entry.user.isNullOrEmpty() && entry.pass != null) {
                    proxyAuthenticator(Authenticator { _, response ->
                        val cred = Credentials.basic(entry.user, entry.pass)
                        response.request.newBuilder()
                            .header("Proxy-Authorization", cred)
                            .build()
                    })
                }
            }
        }
    }
}

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
