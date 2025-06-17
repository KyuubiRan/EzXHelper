package io.github.kyuubiran.ezxhelper.core

object EzXReflection {

    /**
     * 初始化类加载器
     */
    fun init(classLoader: ClassLoader = ClassLoader.getSystemClassLoader()) {
        ClassLoaderProvider.classLoader = classLoader
    }
}