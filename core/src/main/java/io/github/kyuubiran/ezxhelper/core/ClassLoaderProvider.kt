package io.github.kyuubiran.ezxhelper.core

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider.classLoader


object ClassLoaderProvider {

    /**
     * Class loader for doing reflection.
     *
     * 默认使用的类加载器，用于反射操作。
     *
     */
    @JvmStatic
    lateinit var classLoader: ClassLoader

    /**
     * Safe class loader for doing reflection, will use system class loader instead if [classLoader] is not initialized.
     *
     * 安全的类加载器，用于反射操作，如果 [classLoader] 未初始化，则使用系统类加载器。
     *
     */
    @JvmStatic
    val safeClassLoader: ClassLoader
        get() = if (::classLoader.isInitialized) classLoader else ClassLoader.getSystemClassLoader()
}