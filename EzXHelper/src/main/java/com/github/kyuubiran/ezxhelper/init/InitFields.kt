package com.github.kyuubiran.ezxhelper.init

import android.content.Context
import android.content.res.Resources
import com.github.kyuubiran.ezxhelper.utils.interfaces.ILogs

object InitFields {
    /**
     * 宿主全局AppContext
     */
    lateinit var appContext: Context
        internal set

    /**
     * 宿主全局AppContext是否初始化
     */
    val isAppContextInited: Boolean
        get() = this::appContext.isInitialized

    /**
     * 调用本库加载类函数时使用的类加载器
     */
    lateinit var ezXClassLoader: ClassLoader
        internal set

    /**
     * 类加载器是否初始化
     */
    val isEzXClassLoaderInited: Boolean
        get() = this::ezXClassLoader.isInitialized

    /**
     * 模块路径
     */
    lateinit var modulePath: String
        internal set

    /**
     * 模块路径是否初始化
     */
    val isModulePathInited: Boolean
        get() = this::modulePath.isInitialized

    /**
     * 模块资源
     */
    lateinit var moduleRes: Resources
        internal set

    /**
     * 模块资源是否初始化
     */
    val isModuleResInited: Boolean
        get() = this::moduleRes.isInitialized

    /**
     * 宿主包名
     */
    lateinit var hostPackageName: String
        internal set

    /**
     * 宿主包名初始化
     */
    val isHostPackageNameInited: Boolean
        get() = this::hostPackageName.isInitialized

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

    /**
     * Log 接口
     * @see ILogs
     */
    var LOGS: ILogs = object : ILogs {}
        internal set

    /**
     * 输出日志到 Xposed
     */
    var LOG_XP: Boolean = true
        internal set

}