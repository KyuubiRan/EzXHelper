@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper

import android.widget.Toast
import com.github.kyuubiran.ezxhelper.EzXHelper.setToastTag
import com.github.kyuubiran.ezxhelper.misc.AndroidUtils.runOnMainThread
import de.robv.android.xposed.XposedBridge

open class Logger {
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
    open fun i(msg: String, thr: Throwable? = null) {
        if (logLevelFilter > INFO) return
        android.util.Log.i(logTag, msg, thr)
    }

    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    open fun d(msg: String, thr: Throwable? = null) {
        if (logLevelFilter > DEBUG) return
        android.util.Log.d(logTag, msg, thr)
    }


    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    open fun w(msg: String, thr: Throwable? = null) {
        if (logLevelFilter > WARN) return
        android.util.Log.w(logTag, msg, thr)
    }


    /**
     * Print the log
     * @param thr throwable
     * @param msg message
     */
    open fun e(msg: String, thr: Throwable? = null) {
        if (logLevelFilter > ERROR) return
        android.util.Log.e(logTag, msg, thr)
    }

    /**
     * Print the log to xposed
     * @param levelFilter log level filter
     * @param level log level string
     * @param thr throwable
     * @param msg message
     */
    open fun px(levelFilter: Int, level: String, msg: String, thr: Throwable?) {
        if (logLevelFilter > levelFilter) return
        if (isLogToXposed) XposedBridge.log("[$level/$logTag] $msg: ${thr?.stackTraceToString()}")
    }

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

object Log {
    private val defaultLogger = Logger()
    private var logger: Logger? = null

    var currentLogger: Logger
        get() = logger ?: defaultLogger
        set(value) {
            logger = value
        }

    /**
     * Cancel the last toast if it is still showing.
     */
    var cancelLastToast: Boolean = false

    private var toast: Toast? = null

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

    /**
     * Show a toast message.
     *
     * Need set the [EzXHelper.appContext] before use.
     *
     * If [Logger.toastTag] is not set will not show the prefix.
     * @see setToastTag
     */
    @JvmStatic
    fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) = runOnMainThread {
        if (cancelLastToast) toast?.cancel()
        toast = null
        toast = Toast.makeText(EzXHelper.appContext, null, duration).apply {
            setText(if (currentLogger.toastTag != null) "${currentLogger.toastTag}: $msg" else msg)
            show()
        }
    }

    @JvmSynthetic
    fun toast(msg: String, vararg formats: String, duration: Int = Toast.LENGTH_SHORT) =
        toast(msg.format(*formats), duration)

    /**
     * Use this with [runCatching]
     * Use [i] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see i
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logiIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        exceptionOrNull()?.let {
            currentLogger.i(it, msg)
            action(it)
        }
    }


    /**
     * Use this with [runCatching]
     * Use [ix] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see ix
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logixIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        exceptionOrNull()?.let {
            currentLogger.ix(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [d] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see d
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logdIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            currentLogger.d(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [dx] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see dx
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logdxIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            currentLogger.dx(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [w] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see w
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logwIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            currentLogger.w(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [wx] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see wx
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logwxIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            currentLogger.wx(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [e] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see e
     */
    @JvmSynthetic
    inline fun <R> Result<R>.logeIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            currentLogger.e(it, msg)
            action(it)
        }
    }

    /**
     * Use this with [runCatching]
     * Use [ex] to log if throw [Throwable]
     * @param msg message
     * @param action do something with throwable
     * @see runCatching
     * @see ex
     */
    inline fun <R> Result<R>.logexIfThrow(msg: String = "", action: (Throwable) -> Unit = {}) = this.apply {
        this.exceptionOrNull()?.let {
            currentLogger.ex(it, msg)
            action(it)
        }
    }
}