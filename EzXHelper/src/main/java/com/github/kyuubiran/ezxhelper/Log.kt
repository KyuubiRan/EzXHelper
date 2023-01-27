@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper

object Log {
    private val defaultLogger = AndroidLogger
    private var logger: Logger? = null
    init {
        LogExtensions.logger = defaultLogger
    }

    var currentLogger: Logger
        get() = logger ?: defaultLogger
        set(value) {
            logger = value
            LogExtensions.logger = value
        }

    @JvmStatic
    fun i(msg: String, thr: Throwable? = null) {
        currentLogger.i(msg, thr)
    }

    @JvmStatic
    fun d(msg: String, thr: Throwable? = null) {
        currentLogger.d(msg, thr)
    }

    @JvmStatic
    fun w(msg: String, thr: Throwable? = null) {
        currentLogger.w(msg, thr)
    }

    @JvmStatic
    fun e(msg: String, thr: Throwable? = null) {
        currentLogger.e(msg, thr)
    }

    @JvmStatic
    fun ix(msg: String, thr: Throwable? = null) {
        currentLogger.ix(msg, thr)
    }

    @JvmStatic
    fun wx(msg: String, thr: Throwable? = null) {
        currentLogger.wx(msg, thr)
    }

    @JvmStatic
    fun dx(msg: String, thr: Throwable? = null) {
        currentLogger.dx(msg, thr)
    }

    @JvmStatic
    fun ex(msg: String, thr: Throwable? = null) {
        currentLogger.ex(msg, thr)
    }

    @JvmStatic
    fun i(thr: Throwable, msg: String = "") {
        currentLogger.i(thr, msg)
    }

    @JvmStatic
    fun d(thr: Throwable, msg: String = "") {
        currentLogger.d(thr, msg)
    }

    @JvmStatic
    fun w(thr: Throwable, msg: String = "") {
        currentLogger.w(thr, msg)
    }

    @JvmStatic
    fun e(thr: Throwable, msg: String = "") {
        currentLogger.e(thr, msg)
    }

    @JvmStatic
    fun ix(thr: Throwable, msg: String = "") {
        currentLogger.ix(thr, msg)
    }

    @JvmStatic
    fun wx(thr: Throwable, msg: String = "") {
        currentLogger.wx(thr, msg)
    }

    @JvmStatic
    fun dx(thr: Throwable, msg: String = "") {
        currentLogger.dx(thr, msg)
    }

    @JvmStatic
    fun ex(thr: Throwable, msg: String = "") {
        currentLogger.ex(thr, msg)
    }
}
