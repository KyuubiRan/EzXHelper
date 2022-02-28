package com.github.kyuubiran.ezxhelper.utils.interfaces

import android.util.Log
import com.github.kyuubiran.ezxhelper.init.InitFields

interface ILogs {

    /**
     * 打印日志 等级:Info
     * @param msg 消息
     * @param thr 异常
     */
    fun i(msg: String, thr: Throwable?) {
        Log.i(InitFields.LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Debug
     * @param msg 消息
     * @param thr 异常
     */
    fun d(msg: String, thr: Throwable?) {
        Log.d(InitFields.LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Warn
     * @param msg 消息
     * @param thr 异常
     */
    fun w(msg: String, thr: Throwable?) {
        Log.w(InitFields.LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Error
     * @param msg 消息
     * @param thr 异常
     */
    fun e(msg: String, thr: Throwable?) {
        Log.e(InitFields.LOG_TAG, msg, thr)
    }

}