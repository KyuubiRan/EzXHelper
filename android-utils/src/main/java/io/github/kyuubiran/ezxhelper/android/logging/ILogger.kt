package io.github.kyuubiran.ezxhelper.android.logging

interface ILogger {

    fun v(msg: String, thr: Throwable? = null)

    fun i(msg: String, thr: Throwable? = null)

    fun d(msg: String, thr: Throwable? = null)

    fun w(msg: String, thr: Throwable? = null)

    fun e(msg: String, thr: Throwable? = null)
}