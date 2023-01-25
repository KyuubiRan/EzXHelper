@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.MemberExtensions.isStatic

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

    /**
     * Get the static object
     * @param clazz class
     * @param fieldName field name
     * @return field object or null
     * @throws NoSuchFieldException if not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getStaticObjectOrNull(clazz: Class<*>, fieldName: String): Any? =
        clazz.declaredFields.firstOrNull { it.isStatic && fieldName == it.name }
            .let { it ?: throw NoSuchFieldException("No such static field $fieldName in class ${clazz.name}.") }
            .get(null)


    /**
     * Get the static object
     * @param clazz class
     * @param fieldName field name
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses
     * @return field object or null
     * @throws NoSuchFieldException if not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getStaticObjectOrNullUntilSuperclass(clazz: Class<*>, fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): Any? {
        var clz: Class<*> = clazz
        while (clz != Any::class.java) {
            if (untilSuperClass?.invoke(clz) == true) break

            try {
                return getStaticObjectOrNull(clz, fieldName)
            } catch (e: NoSuchFieldException) {
                clz = clazz.superclass
            }
        }
        throw NoSuchFieldException("No such static field $fieldName in ${clazz.name} and its superclasses.")
    }

    /**
     * Get the static object, and trying to cast to the [T] type
     * @param clazz class
     * @param fieldName field name
     * @return [T] field object or null
     * @throws NoSuchFieldException if not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getStaticObjectOrNullAs(clazz: Class<*>, fieldName: String): T? =
        getStaticObjectOrNull(clazz, fieldName) as? T?

    /**
     * Get the static object, and trying to cast to the [T] type
     * @param clazz class
     * @param fieldName field name
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses
     * @return field object or null
     * @throws NoSuchFieldException if not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getStaticObjectOrNullUntilSuperclassAs(clazz: Class<*>, fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): T? =
        getStaticObjectOrNullUntilSuperclass(clazz, fieldName, untilSuperClass) as? T?

    /**
     * Set the static object
     * @param clazz class
     * @param fieldName field name
     * @param value field value
     * @throws NoSuchFieldException if not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setStaticObject(clazz: Class<*>, fieldName: String, value: Any?) =
        clazz.declaredFields.firstOrNull { it.isStatic && fieldName == it.name }
            .let { it ?: throw NoSuchFieldException("No such static field $fieldName in class ${clazz.name}.") }
            .set(null, value)

    /**
     * Set the static object
     * @param clazz class
     * @param fieldName field name
     * @param value field value
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses
     * @throws NoSuchFieldException if not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setStaticObjectUntilSuperclass(clazz: Class<*>, fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) {
        var clz: Class<*> = clazz
        while (clz != Any::class.java) {
            if (untilSuperClass?.invoke(clz) == true) break

            try {
                setStaticObject(clz, fieldName, value)
                return
            } catch (e: NoSuchFieldException) {
                clz = clazz.superclass
            }
        }
        throw NoSuchFieldException("No such static field $fieldName in ${clazz.name} and its superclasses.")
    }
}