package com.github.kyuubiran.ezxhelper

object ClassLoaderProvider {
    /**
     * Class loader for doing reflection.
     */
    @JvmStatic
    lateinit var classLoader: ClassLoader

    /**
     * Safe class loader for doing reflection, will use system class loader instead if [classLoader] is not initialized.
     */
    @JvmStatic
    val safeClassLoader: ClassLoader
        get() = if (isClassLoaderInited) classLoader else ClassLoader.getSystemClassLoader()

    @JvmStatic
    val isClassLoaderInited: Boolean
        get() = this::classLoader.isInitialized
}