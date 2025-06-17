package io.github.kyuubiran.ezxhelper.android.logging

import android.util.Log

abstract class AndroidLogger : ILogger {

    abstract val tag: String

    override fun v(msg: String, thr: Throwable?) {
        Log.v(tag, msg, thr)
    }

    override fun i(msg: String, thr: Throwable?) {
        Log.i(tag, msg, thr)
    }

    override fun d(msg: String, thr: Throwable?) {
        Log.d(tag, msg, thr)
    }

    override fun w(msg: String, thr: Throwable?) {
        Log.w(tag, msg, thr)
    }

    override fun e(msg: String, thr: Throwable?) {
        Log.e(tag, msg, thr)
    }
}