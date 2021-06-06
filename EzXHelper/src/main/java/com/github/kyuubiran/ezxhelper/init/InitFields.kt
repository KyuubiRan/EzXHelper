package com.github.kyuubiran.ezxhelper.init

import android.content.Context
import android.content.res.XModuleResources

object InitFields {
    /**
     * 宿主全局AppContext
     */
    lateinit var appContext: Context
        internal set

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
    lateinit var moduleRes: XModuleResources
        internal set

    /**
     * 日志TAG
     */
    var LOG_TAG: String = "EzXHelper"
        internal set
}