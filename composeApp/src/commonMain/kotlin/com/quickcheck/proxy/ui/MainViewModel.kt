package com.quickcheck.proxy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickcheck.proxy.data.CheckStatus
import com.quickcheck.proxy.data.ProxyChecker
import com.quickcheck.proxy.data.ProxyFormat
import com.quickcheck.proxy.data.ProxyParser
import com.quickcheck.proxy.data.ProxyResult
import com.quickcheck.proxy.data.ProxyType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class UiState(
    val input: String = "",
    val format: ProxyFormat = ProxyFormat.AUTO,
    val type: ProxyType = ProxyType.HTTP,
    val concurrency: Int = 20,
    val timeoutSec: Int = 15,
    val isChecking: Boolean = false,
    val total: Int = 0,
    val checked: Int = 0,
    val live: List<ProxyResult> = emptyList(),
    val dead: List<ProxyResult> = emptyList(),
)

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private var job: Job? = null
    private val mutex = Mutex()

    fun setInput(v: String) = _state.update { it.copy(input = v) }
    fun setFormat(v: ProxyFormat) = _state.update { it.copy(format = v) }
    fun setType(v: ProxyType) = _state.update { it.copy(type = v) }
    fun setConcurrency(v: Int) = _state.update { it.copy(concurrency = v.coerceIn(1, 100)) }
    fun setTimeout(v: Int) = _state.update { it.copy(timeoutSec = v.coerceIn(1, 60)) }

    fun start(): Boolean {
        if (_state.value.isChecking) return false
        val s = _state.value
        val entries = s.input.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { ProxyParser.parse(it, s.format, s.type) }
            .toList()
        if (entries.isEmpty()) return false

        _state.update {
            it.copy(
                isChecking = true,
                total = entries.size,
                checked = 0,
                live = emptyList(),
                dead = emptyList(),
            )
        }

        val checker = ProxyChecker(s.concurrency, s.timeoutSec.toLong())
        job = viewModelScope.launch(Dispatchers.Default) {
            try {
                checker.check(entries) { result -> applyResult(result) }
            } finally {
                _state.update { it.copy(isChecking = false) }
            }
        }
        return true
    }

    private suspend fun applyResult(result: ProxyResult) {
        mutex.withLock {
            _state.update { st ->
                val live = if (result.status == CheckStatus.LIVE) st.live + result else st.live
                val dead = if (result.status == CheckStatus.DEAD) st.dead + result else st.dead
                st.copy(checked = st.checked + 1, live = live, dead = dead)
            }
        }
    }

    fun stop() {
        job?.cancel()
        _state.update { it.copy(isChecking = false) }
    }

    fun clearResults() = _state.update {
        it.copy(live = emptyList(), dead = emptyList(), checked = 0, total = 0)
    }

    fun clearInput() = _state.update { it.copy(input = "") }

    fun removeDuplicates(): Int {
        val original = _state.value.input
        val originalCount = original.lineSequence().count { it.isNotBlank() }
        val seen = mutableSetOf<String>()
        val deduped = original.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && seen.add(it) }
            .toList()
        _state.update { it.copy(input = deduped.joinToString("\n")) }
        return originalCount - deduped.size
    }

    fun returnDeadToInput() {
        val st = _state.value
        if (st.dead.isEmpty()) return
        val deadRaw = st.dead.joinToString("\n") { it.entry.raw }
        _state.update {
            it.copy(input = deadRaw, live = emptyList(), dead = emptyList(), checked = 0, total = 0)
        }
    }

    fun sortLiveByLatency() = _state.update {
        it.copy(live = it.live.sortedBy { r -> r.latencyMs ?: Long.MAX_VALUE })
    }
}
