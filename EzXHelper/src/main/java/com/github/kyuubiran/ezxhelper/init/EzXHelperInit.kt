package com.github.kyuubiran.ezxhelper.init

import android.content.Context
import com.github.kyuubiran.ezxhelper.init.InitFields.LOG_TAG
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.ezXClassLoader
import de.robv.android.xposed.callbacks.XC_LoadPackage

object EzXHelperInit {
    /**
     * 使用本库必须执行的初始化
     * 应在handleLoadPackage方法内第一个调用
     */
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        setEzClassLoader(lpparam.classLoader)
    }

    /**
     * 设置本库使用的类加载器
     * 注意：通常情况下 建议使用框架提供的类加载器进行操作
     * 但某些app会修改自身的类加载器 遇到这种情况请自行设置运行时的类加载器
     * @param classLoader 类加载器
     */
    fun setEzClassLoader(classLoader: ClassLoader) {
        ezXClassLoader = classLoader
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