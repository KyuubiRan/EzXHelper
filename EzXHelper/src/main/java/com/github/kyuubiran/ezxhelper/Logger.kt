@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly

abstract class Logger {
    /**
     * Log level filter.
     * Will ignore all logs with level lower than this.
     */
    var logLevelFilter: Int = VERBOSE

    var logTag: String = "EZXHelper"


    /**
     * Is print the log to xposed
     */
    var isLogToXposed: Boolean = true
        internal set

    var toastTag: String? = null

    /**
     * Log level filter definitions
     */
    companion object LogLevel {
        const val VERBOSE = 0
        const val DEBUG = 1
        const val INFO = 2
        const val WARN = 3
        const val ERROR = 4
    }

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    abstract fun i(msg: String, thr: Throwable? = null)

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    abstract fun d(msg: String, thr: Throwable? = null)


    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    abstract fun w(msg: String, thr: Throwable? = null)


    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    abstract fun e(msg: String, thr: Throwable? = null)

    /**
     * Print the log to xposed
     * @param levelFilter log level filter
     * @param level log level string
     * @param thr throwable
     * @param msg message
     */
    abstract fun px(levelFilter: Int, level: String, msg: String, thr: Throwable?)

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    fun i(thr: Throwable, msg: String = "") {
        i(msg, thr)
    }

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    fun d(thr: Throwable, msg: String = "") {
        d(msg, thr)
    }

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    fun w(thr: Throwable, msg: String = "") {
        w(msg, thr)
    }

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    fun e(thr: Throwable, msg: String = "") {
        e(msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun ix(msg: String, thr: Throwable? = null) {
        i(msg, thr)
        px(INFO, "I", msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun ix(thr: Throwable, msg: String = "") {
        ix(msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun wx(msg: String, thr: Throwable? = null) {
        w(msg, thr)
        px(WARN, "W", msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun wx(thr: Throwable, msg: String = "") {
        wx(msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun dx(msg: String, thr: Throwable? = null) {
        d(msg, thr)
        px(DEBUG, "D", msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun dx(thr: Throwable, msg: String = "") {
        dx(msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun ex(msg: String, thr: Throwable? = null) {
        e(msg, thr)
        px(ERROR, "E", msg, thr)
    }


    /**
     * Print the log to xposed log
     * @param thr throwable
     * @param msg message
     */
    fun ex(thr: Throwable, msg: String = "") {
        ex(msg, thr)
    }
}

object LogExtensions {
    var logger: Logger? = null

    /**
     * Use this with [runCatching]
     * Use [Logger.i] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.i
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logiIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        exceptionOrNull()?.let {
            logger?.i(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.ix] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.ix
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logixIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        exceptionOrNull()?.let {
            require(logger != null) { "Logger is null" }
            logger?.ix(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.d] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.d
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logdIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            logger?.d(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.dx] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.dx
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logdxIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            logger?.dx(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.w] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.w
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logwIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            logger?.w(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.wx] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.wx
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logwxIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            logger?.wx(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.e] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.e
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logeIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            logger?.e(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [Logger.ex] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see Logger.ex
     */
    @JvmSynthetic
    @KotlinOnly
    inline fun <R> Result<R>.logexIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            logger?.ex(it, msg)
            action(it)
        }
    }
}
