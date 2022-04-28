package uk.dioxic.muon.common

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var debounceJob: Job? = null

fun <T> debounce(value: T, wait: Long = 300, block: (T) -> Unit) {
    debounceJob?.cancel()
    debounceJob = MainScope().launch {
        delay(wait)
        block(value)
    }
}