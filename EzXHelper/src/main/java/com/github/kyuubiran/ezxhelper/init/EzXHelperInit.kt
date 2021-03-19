package com.github.kyuubiran.ezxhelper.init

import android.content.Context
import com.github.kyuubiran.ezxhelper.init.InitFields.LOG_TAG
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.mClassLoader
import de.robv.android.xposed.callbacks.XC_LoadPackage

object EzXHelperInit {
    /**
     * 使用本库必须执行的初始化
     * 应在handleLoadPackage方法内第一个调用
     */
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        mClassLoader = lpparam.classLoader
    }

    /**
     * 初始化全局ApplicationContext
     */
    fun initAppContext(context: Context) {
        appContext = context
    }

    /**
     * 设置打印日志的标签
     */
    fun setLogTag(tag: String) {
        LOG_TAG = tag
    }
}