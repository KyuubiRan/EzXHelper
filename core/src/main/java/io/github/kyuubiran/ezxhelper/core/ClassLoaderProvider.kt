package io.github.kyuubiran.ezxhelper.core

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
        get() = if (::classLoader.isInitialized) classLoader else ClassLoader.getSystemClassLoader()
}