package com.github.kyuubiran.ezxhelper.utils

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit

val mainHandler: Handler by lazy {
    Handler(Looper.getMainLooper())
}

val runtimeProcess: Runtime by lazy {
    Runtime.getRuntime()
}

/**
 * 将函数放到主线程执行 如UI更新、显示Toast等
 * @param r 需要执行的内容
 */
fun runOnMainThread(r: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        r.run()
    } else {
        mainHandler.post(r)
    }
}

/**
 * 扩展函数 显示一个Toast
 * @param msg Toast显示的消息
 * @param length Toast显示的时长
 */
fun Context.showToast(msg: String?, length: Int = Toast.LENGTH_SHORT) {
    runOnMainThread {
        Toast.makeText(this, msg, length).show()
    }
}

/**
 * 扩展函数 将模块的资源路径添加到Context.resources内 允许直接以R.xx.xxx获取资源
 * @see EzXHelperInit.addModuleAssetPath
 */
fun Context.addModuleAssetPath() {
    EzXHelperInit.addModuleAssetPath(this)
}

/**
 * 扩展函数 将模块的资源路径添加到resources内 允许直接以R.xx.xxx获取资源
 * @see EzXHelperInit.addModuleAssetPath
 */
fun Resources.addModuleAssetPath() {
    EzXHelperInit.addModuleAssetPath(this)
}