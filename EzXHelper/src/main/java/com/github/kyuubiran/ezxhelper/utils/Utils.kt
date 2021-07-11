package com.github.kyuubiran.ezxhelper.utils

inline fun tryOrFalse(crossinline block: () -> Unit): Boolean {
    return try {
        block()
        true
    } catch (thr: Throwable) {
        false
    }
}