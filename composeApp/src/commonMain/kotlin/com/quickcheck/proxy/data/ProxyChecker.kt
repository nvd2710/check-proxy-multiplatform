package com.quickcheck.proxy.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeout

class ProxyChecker(
    private val concurrency: Int = 20,
    private val timeoutSec: Long = 15,
    private val testUrl: String = DEFAULT_TEST_URL,
) {
    suspend fun check(
        entries: List<ProxyEntry>,
        onProgress: suspend (ProxyResult) -> Unit,
    ): List<ProxyResult> = coroutineScope {
        val sem = Semaphore(concurrency)
        entries.map { e ->
            async(Dispatchers.Default) {
                sem.withPermit {
                    val r = checkOne(e)
                    onProgress(r)
                    r
                }
            }
        }.awaitAll()
    }

    private suspend fun checkOne(entry: ProxyEntry): ProxyResult {
        // Currently Ktor has no first-class SOCKS5 support across all engines.
        // For SOCKS5 we rely on platform-specific HttpClient builder via createPlatformProxyClient.
        val client: HttpClient = createPlatformProxyClient(entry, timeoutSec)
        val start = currentTimeMillis()

        return try {
            val response: HttpResponse = withTimeout(timeoutSec * 1000L) {
                if (entry.type == ProxyType.HTTP && !entry.user.isNullOrEmpty() && entry.pass != null) {
                    val basic = "Basic " + "${entry.user}:${entry.pass}"
                        .encodeToByteArray()
                        .encodeBase64()
                    client.get(testUrl) {
                        header(HttpHeaders.ProxyAuthorization, basic)
                    }
                } else {
                    client.get(testUrl)
                }
            }
            val ms = currentTimeMillis() - start
            if (response.status.value in 200..299 || response.status.value == 204) {
                ProxyResult(entry, CheckStatus.LIVE, ms)
            } else {
                ProxyResult(entry, CheckStatus.DEAD, ms, "HTTP ${response.status.value}")
            }
        } catch (e: Throwable) {
            val msg = (e.message ?: e::class.simpleName ?: "error").take(60)
            ProxyResult(entry, CheckStatus.DEAD, null, msg)
        } finally {
            client.close()
        }
    }

    companion object {
        const val DEFAULT_TEST_URL = "http://ip-api.com/json"
    }
}

/** Platform-specific HttpClient configured with the given proxy entry. */
expect fun createPlatformProxyClient(entry: ProxyEntry, timeoutSec: Long): HttpClient

/** Wall clock millis (System.currentTimeMillis on JVM, NSDate on iOS). */
expect fun currentTimeMillis(): Long
