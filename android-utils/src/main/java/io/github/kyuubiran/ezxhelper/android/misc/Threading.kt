package io.github.kyuubiran.ezxhelper.android.misc

import android.os.Handler
import android.os.Looper

object Threading {

    val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    /**
     * Post [Runnable] to the main thread(UI thread)
     * 将一个 [Runnable] 放在主线程(UI 线程)上执行
     */
    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            mainHandler.post(runnable)
        }
    }
}