package com.github.kyuubiran.ezxhelper.init

import android.content.Context

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
     * 日志TAG
     */
    var LOG_TAG: String = "EzXHelper"
        internal set
}