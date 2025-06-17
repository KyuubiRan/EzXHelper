package io.github.kyuubiran.ezxhelper.android.logging

object Logger {

    var tag: String = "EZXHelper"
    var logLevel: Int = LogLevel.VERBOSE

    var currentLogger: ILogger = object : AndroidLogger() {
        override val tag: String get() = Logger.tag
    }

    object LogLevel {

        const val VERBOSE = 0
        const val DEBUG = 1
        const val INFO = 2
        const val WARN = 3
        const val ERROR = 4
        const val NONE = 5
    }

    fun v(msg: String, thr: Throwable? = null) {
        if (logLevel > LogLevel.VERBOSE) return
        currentLogger.v(msg, thr)
    }

    fun v(thr: Throwable, msg: String = "") {
        if (logLevel > LogLevel.VERBOSE) return
        currentLogger.v(msg, thr)
    }

    fun i(msg: String, thr: Throwable? = null) {
        if (logLevel > LogLevel.INFO) return
        currentLogger.i(msg, thr)
    }

    fun i(thr: Throwable, msg: String = "") {
        if (logLevel > LogLevel.INFO) return
        currentLogger.i(msg, thr)
    }

    fun d(msg: String, thr: Throwable? = null) {
        if (logLevel > LogLevel.DEBUG) return
        currentLogger.d(msg, thr)
    }

    fun d(thr: Throwable, msg: String = "") {
        if (logLevel > LogLevel.DEBUG) return
        currentLogger.d(msg, thr)
    }

    fun w(msg: String, thr: Throwable? = null) {
        if (logLevel > LogLevel.WARN) return
        currentLogger.w(msg, thr)
    }

    fun w(thr: Throwable, msg: String = "") {
        if (logLevel > LogLevel.WARN) return
        currentLogger.w(msg, thr)
    }

    fun e(msg: String, thr: Throwable? = null) {
        if (logLevel > LogLevel.ERROR) return
        currentLogger.e(msg, thr)
    }

    fun e(thr: Throwable, msg: String = "") {
        if (logLevel > LogLevel.ERROR) return
        currentLogger.e(msg, thr)
    }
}