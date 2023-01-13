package com.github.kyuubiran.ezxhelper

object ClassUtils {
    @JvmStatic
    fun loadClassOrNull(className: String, cl: ClassLoader? = null): Class<*>? = try {
        Class.forName(className, false, cl ?: EzXHelper.safeClassLoader)
    } catch (e: ClassNotFoundException) {
        null
    }

    @JvmStatic
    @Throws(ClassNotFoundException::class)
    fun loadClass(className: String, cl: ClassLoader? = null): Class<*> =
        Class.forName(className, false, cl ?: EzXHelper.safeClassLoader)

    @JvmStatic
    fun loadClassAny(cl: ClassLoader, vararg className: String): Class<*> {
        val sb = StringBuilder()
        for (name in className) {
            loadClassOrNull(name, cl)?.let { return it }
            sb.append(name).append(", ")
        }
        if (sb.endsWith(", ")) sb.delete(sb.length - 2, sb.length)
        throw ClassNotFoundException("No such class found in [$sb]")
    }

    @JvmStatic
    fun loadClassAny(vararg className: String): Class<*> = loadClassAny(EzXHelper.safeClassLoader, *className)

    @JvmStatic
    fun loadClassAnyOrNull(cl: ClassLoader, vararg className: String): Class<*>? {
        for (name in className) {
            loadClassOrNull(name, cl)?.let { return it }
        }
        return null
    }

    @JvmStatic
    fun loadClassAnyOrNull(vararg className: String): Class<*>? = loadClassAnyOrNull(EzXHelper.safeClassLoader, *className)
}