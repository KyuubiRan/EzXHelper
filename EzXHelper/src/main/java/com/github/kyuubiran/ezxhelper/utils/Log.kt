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
     * @param msg 消息
     */
    fun e(msg: String) {
        Log.e(LOG_TAG, msg)
    }

    /**
     * 打印日志 等级:Info
     * @param thr 异常
     * @param msg 消息
     */
    fun i(thr: Throwable, msg: String? = null) {
        Log.i(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Debug
     * @param thr 异常
     * @param msg 消息
     */
    fun d(thr: Throwable, msg: String? = null) {
        Log.d(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Warn
     * @param thr 异常
     * @param msg 消息
     */
    fun w(thr: Throwable, msg: String? = null) {
        Log.w(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Error
     * @param thr 异常
     * @param msg 消息
     */
    fun e(thr: Throwable, msg: String? = null) {
        Log.e(LOG_TAG, msg, thr)
    }

    /**
     * 打印日志 等级:Error
     * @param thr Throwable
     * @param msg 消息
     */
    @Deprecated(
        "Replace with Log.e(thr, msg)",
        ReplaceWith("Log.e(msg, thr)")
    )
    fun t(thr: Throwable, msg: String? = null) {
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
        try {
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
        } catch (e: Exception) {
            Log.e(LOG_TAG, null, e)
        }
    }
}