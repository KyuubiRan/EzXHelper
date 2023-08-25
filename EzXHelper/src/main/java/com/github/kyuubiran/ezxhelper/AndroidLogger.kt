@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper

import android.widget.Toast
import com.github.kyuubiran.ezxhelper.EzXHelper.setToastTag
import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import com.github.kyuubiran.ezxhelper.misc.AndroidUtils
import de.robv.android.xposed.XposedBridge

object AndroidLogger : Logger() {
    /**
     * Cancel the last toast if it is still showing.
     */
    var cancelLastToast: Boolean = false

    private var toast: Toast? = null


    override fun i(msg: String, thr: Throwable?) {
        if (logLevelFilter > INFO) return
        android.util.Log.i(logTag, msg, thr)
    }

    override fun d(msg: String, thr: Throwable?) {
        if (logLevelFilter > INFO) return
        android.util.Log.d(logTag, msg, thr)
    }

    override fun w(msg: String, thr: Throwable?) {
        if (logLevelFilter > WARN) return
        android.util.Log.w(logTag, msg, thr)
    }

    override fun e(msg: String, thr: Throwable?) {
        if (logLevelFilter > ERROR) return
        android.util.Log.e(logTag, msg, thr)
    }

    override fun px(levelFilter: Int, level: String, msg: String, thr: Throwable?) {
        if (logLevelFilter > levelFilter) return
        if (isLogToXposed) XposedBridge.log("[$level/$logTag] $msg: ${thr?.stackTraceToString()}")
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
    fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) = AndroidUtils.runOnMainThread {
        if (cancelLastToast) toast?.cancel()
        toast = null
        toast = Toast.makeText(EzXHelper.appContext, null, duration).apply {
            setText(if (Log.currentLogger.toastTag != null) "${Log.currentLogger.toastTag}: $msg" else msg)
            show()
        }
    }

    @JvmSynthetic
    @KotlinOnly
    fun toast(msg: String, vararg formats: String, duration: Int = Toast.LENGTH_SHORT) =
        toast(msg.format(*formats), duration)
}