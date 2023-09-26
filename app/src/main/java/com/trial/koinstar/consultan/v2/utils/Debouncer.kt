package com.trial.koinstar.consultan.v2.utils

import android.os.Handler
import android.os.Looper

class Debouncer(private val delayMillis: Long) {
    private var callback: (() -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())

    fun debounce(callback: () -> Unit) {
        this.callback = callback
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            this.callback?.invoke()
            this.callback = null
        }, delayMillis)
    }

    fun cancel() {
        handler.removeCallbacksAndMessages(null)
        this.callback = null
    }
}