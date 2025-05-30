package io.github.kyuubiran.ezxhelper.core

object EzXReflection {

    var classLoader by ClassLoaderProvider::classLoader
    val safeClassLoader by ClassLoaderProvider::safeClassLoader

    fun init(classLoader: ClassLoader = ClassLoader.getSystemClassLoader()) {
        this.classLoader = classLoader
    }
}