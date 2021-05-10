package com.github.kyuubiran.ezxhelper.init

import android.content.Context

object InitFields {
    /**
     * 宿主全局AppContext
     */
    lateinit var appContext: Context
        internal set

    /**
     * 模块类加载器
     */
    lateinit var mClassLoader: ClassLoader
        internal set

    /**
     * 日志TAG
     */
    var LOG_TAG: String = "EzXHelper"
        internal set
}