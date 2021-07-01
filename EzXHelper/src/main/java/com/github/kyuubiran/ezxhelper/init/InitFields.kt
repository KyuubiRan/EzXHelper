package com.github.kyuubiran.ezxhelper.init

import android.content.Context
import android.content.res.Resources
import android.content.res.XModuleResources

object InitFields {
    /**
     * 宿主全局AppContext
     */
    lateinit var appContext: Context
        internal set

    /**
     * 指示宿主全局AppContext是否已经初始化
     */
    val isAppContextInitialized: Boolean = this::appContext.isInitialized

    /**
     * 调用本库加载类函数时使用的类加载器
     */
    lateinit var ezXClassLoader: ClassLoader
        internal set

    /**
     * 模块路径
     */
    lateinit var modulePath: String
        internal set

    /**
     * 模块资源
     */
    lateinit var moduleRes: Resources
        internal set

    /**
     * 指示模块资源是否已经初始化
     */
    val isModuleResInitialized: Boolean = this::moduleRes.isInitialized

    /**
     * 宿主包名
     */
    lateinit var hostPackageName: String
        internal set

    /**
     * 日志TAG
     */
    var LOG_TAG: String = "EzXHelper"
        internal set

    /**
     * Toast TAG
     */
    var TOAST_TAG: String? = null
        internal set
}