@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.misc

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object AndroidUtils {
    @JvmStatic
    val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    @JvmStatic
    val runtimeProcess: Runtime by lazy {
        Runtime.getRuntime()
    }

    /**
     * Post runnable on main thread.
     */
    @JvmStatic
    fun runOnMainThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            mainHandler.post(runnable)
        }
    }

    /**
     * Show toast
     * @param msg message
     * @param length duration [Toast.LENGTH_SHORT] / [Toast.LENGTH_LONG]
     */
    @JvmStatic
    fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) = runOnMainThread {
        Toast.makeText(this, msg, length).show()
    }

    /**
     * Show toast
     * @param msg message
     * @param args format args
     * @param length duration [Toast.LENGTH_SHORT] / [Toast.LENGTH_LONG]
     */
    @JvmStatic
    fun Context.showToast(msg: String, vararg args: Any?, length: Int = Toast.LENGTH_SHORT) = runOnMainThread {
        Toast.makeText(this, msg.format(args), length).show()
    }
}