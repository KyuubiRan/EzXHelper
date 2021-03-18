package me.kyuubiran.ezxhelper.init

import android.content.Context

object InitFields {
    /**
     * 宿主全局AppContext
     */
    lateinit var appContext: Context
        internal set

    /**
     * 类加载器
     */
    lateinit var mClassLoader: ClassLoader
        internal set
}