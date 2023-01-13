@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper

object ClassUtils {
    /**
     * Load the class or null if not found
     * @param className class name
     * @param cl class loader
     * @return class or null
     */
    @JvmStatic
    fun loadClassOrNull(className: String, cl: ClassLoader? = null): Class<*>? = try {
        Class.forName(className, false, cl ?: EzXHelper.safeClassLoader)
    } catch (e: ClassNotFoundException) {
        null
    }

    /**
     * Load the class or throw exception if not found
     * @param className class name
     * @param cl class loader
     * @return class or throw [ClassNotFoundException]
     */
    @JvmStatic
    @Throws(ClassNotFoundException::class)
    fun loadClass(className: String, cl: ClassLoader? = null): Class<*> =
        Class.forName(className, false, cl ?: EzXHelper.safeClassLoader)

    /**
     * Load the first exists class or throw exception all not found
     * @param className class name
     * @param cl class loader
     * @return class or throw [ClassNotFoundException]
     */
    @JvmStatic
    @Throws(ClassNotFoundException::class)
    fun loadFirstClass(cl: ClassLoader, vararg className: String): Class<*> {
        val sb = StringBuilder()
        for (name in className) {
            loadClassOrNull(name, cl)?.let { return it }
            sb.append(name).append(", ")
        }
        if (sb.endsWith(", ")) sb.delete(sb.length - 2, sb.length)
        throw ClassNotFoundException("No such class found in [$sb]")
    }

    /**
     * Load the first exists class or throw exception all not found
     * @param className class name
     * @return class or throw [ClassNotFoundException]
     */
    @Throws(ClassNotFoundException::class)
    @JvmStatic
    fun loadFirstClass(vararg className: String): Class<*> = loadFirstClass(EzXHelper.safeClassLoader, *className)

    /**
     * Load the first exists class or null
     * @param className class name
     * @param cl class loader
     * @return class or null
     */
    @JvmStatic
    fun loadFirstClassOrNull(cl: ClassLoader, vararg className: String): Class<*>? {
        for (name in className) {
            loadClassOrNull(name, cl)?.let { return it }
        }
        return null
    }

    /**
     * Load the first exists class or null
     * @param className class name
     * @return class or null
     */
    @JvmStatic
    fun loadFirstClassOrNull(vararg className: String): Class<*>? = loadFirstClassOrNull(EzXHelper.safeClassLoader, *className)
}