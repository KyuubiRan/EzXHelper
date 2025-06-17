package io.github.kyuubiran.ezxhelper.core

object EzXReflection {

    /**
     * 初始化类加载器
     */
    @JvmStatic
    fun init(classLoader: ClassLoader = ClassLoader.getSystemClassLoader()) {
        ClassLoaderProvider.classLoader = classLoader
    }
}