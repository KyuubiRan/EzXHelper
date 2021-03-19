package com.github.kyuubiran.ezxhelper.utils

import android.util.Log
import com.github.kyuubiran.ezxhelper.init.InitFields.LOG_TAG


object Log {
    /**
     * 打印日志 等级:Info
     * @param msg 消息
     */
    fun i(msg: String) {
        Log.i(LOG_TAG, msg)
    }

    /**
     * 打印日志 等级:Debug
     * @param msg 消息
     */
    fun d(msg: String) {
        Log.d(LOG_TAG, msg)
    }

    /**
     * 打印日志 等级:Warn
     * @param msg 消息
     */
    fun w(msg: String) {
        Log.w(LOG_TAG, msg)
    }

    /**
     * 打印日志 等级:Error
     * @param e 异常
     * @param msg 消息
     */
    fun e(e: Exception, msg: String = "") {
        if (msg.isEmpty())
            Log.e(LOG_TAG, e.stackTraceToString())
        else
            Log.e(LOG_TAG, "$msg\n${e.stackTraceToString()}")
    }

    /**
     * 打印日志 等级:Error
     * @param e 错误
     * @param msg 消息
     */
    fun e(e: Error, msg: String = "") {
        if (msg.isEmpty())
            Log.e(LOG_TAG, e.stackTraceToString())
        else
            Log.e(LOG_TAG, "$msg\n${e.stackTraceToString()}")
    }

    /**
     * 打印日志 等级:Error
     * @param thr Throwable
     * @param msg 消息
     */
    fun t(thr: Throwable, msg: String = "") {
        if (msg.isEmpty())
            Log.e(LOG_TAG, thr.stackTraceToString())
        else
            Log.e(LOG_TAG, "$msg\n${thr.stackTraceToString()}")
    }
}