package com.github.kyuubiran.ezxhelper.utils

import android.util.Log
import android.widget.Toast
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit.setToastTag
import com.github.kyuubiran.ezxhelper.init.InitFields.LOG_TAG
import com.github.kyuubiran.ezxhelper.init.InitFields.TOAST_TAG
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext

object Log {
    /**
     * 打印日志 等级:Info
     * @param msg 消息
     * @param thr 异常
     */
    fun i(msg: String, thr: Throwable? = null) {
        Log.i(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Debug
     * @param msg 消息
     * @param thr 异常
     */
    fun d(msg: String, thr: Throwable? = null) {
        Log.d(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Warn
     * @param msg 消息
     * @param thr 异常
     */
    fun w(msg: String, thr: Throwable? = null) {
        Log.w(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Error
     * @param msg 消息
     * @param thr 异常
     */
    fun e(msg: String, thr: Throwable? = null) {
        Log.e(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Info
     * @param thr 异常
     * @param msg 消息
     */
    fun i(thr: Throwable, msg: String = "") {
        Log.i(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Debug
     * @param thr 异常
     * @param msg 消息
     */
    fun d(thr: Throwable, msg: String = "") {
        Log.d(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Warn
     * @param thr 异常
     * @param msg 消息
     */
    fun w(thr: Throwable, msg: String = "") {
        Log.w(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Error
     * @param thr 异常
     * @param msg 消息
     */
    fun e(thr: Throwable, msg: String = "") {
        Log.e(LOG_TAG, msg, thr)
    }

    /**
     * 显示一个Toast
     *
     * 需要先初始化appContext才能使用
     *
     * 如果不设置TOAST_TAG
     * 则不显示前缀
     * @see setToastTag
     */
    fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        runOnMainThread {
            Toast.makeText(appContext, null, duration).run {
                if (TOAST_TAG != null) {
                    setText("${TOAST_TAG}: $msg")
                } else {
                    setText(msg)
                }
                show()
            }
        }
    }

    /**
     * 扩展函数 配合runCatching使用
     * 如果抛出异常 则调用 Log.i 记录
     * @param msg 消息
     * @param then 发生异常时执行的函数
     * @see runCatching
     * @see i
     */
    inline fun <R> Result<R>.logiIfThrow(msg: String = "", then: ((Throwable) -> Unit) = {}) {
        this.exceptionOrNull()?.let {
            i(it, msg)
            then(it)
        }
    }

    /**
     * 扩展函数 配合 runCatching 使用
     * 如果抛出异常 则调用 Log.d 记录
     * @param msg 消息
     * @param then 发生异常时执行的函数
     * @see runCatching
     * @see d
     */
    inline fun <R> Result<R>.logdIfThrow(msg: String = "", then: (Throwable) -> Unit = {}) {
        this.exceptionOrNull()?.let {
            d(it, msg)
            then(it)
        }
    }

    /**
     * 扩展函数 配合 runCatching 使用
     * 如果抛出异常 则调用 Log.w 记录
     * @param msg 消息
     * @param then 发生异常时执行的函数
     * @see runCatching
     * @see w
     */
    inline fun <R> Result<R>.logwIfThrow(msg: String = "", then: (Throwable) -> Unit = {}) {
        this.exceptionOrNull()?.let {
            w(it, msg)
            then(it)
        }
    }

    /**
     * 扩展函数 配合 runCatching 使用
     * 如果抛出异常 则调用 Log.e 记录
     * @param msg 消息
     * @param then 发生异常时执行的函数
     * @see runCatching
     * @see e
     */
    inline fun <R> Result<R>.logeIfThrow(msg: String = "", then: (Throwable) -> Unit = {}) {
        this.exceptionOrNull()?.let {
            e(it, msg)
            then(it)
        }
    }
}