@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.MemberExtensions.isStatic
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.lang.IllegalArgumentException

object ClassUtils {
    /**
     * Load the class or null if not found
     * @param className class name
     * @param cl class loader
     * @return class or null
     */
    @JvmStatic
    fun loadClassOrNull(className: String, cl: ClassLoader? = null): Class<*>? = try {
        Class.forName(className, false, cl ?: ClassLoaderProvider.safeClassLoader)
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
        Class.forName(className, false, cl ?: ClassLoaderProvider.safeClassLoader)

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
    fun loadFirstClass(vararg className: String): Class<*> = loadFirstClass(ClassLoaderProvider.safeClassLoader, *className)

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
    fun loadFirstClassOrNull(vararg className: String): Class<*>? = loadFirstClassOrNull(
        ClassLoaderProvider.safeClassLoader, *className
    )

    /**
     * Get the static object
     * @param clazz class
     * @param fieldName field name
     * @return field object or null
     * @throws NoSuchFieldException if the field is not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getStaticObjectOrNull(clazz: Class<*>, fieldName: String): Any? =
        clazz.declaredFields.firstOrNull { it.isStatic && fieldName == it.name }
            .let {
                it?.also { f -> f.isAccessible = true } ?: throw NoSuchFieldException("No such static field $fieldName in class ${clazz.name}.")
            }
            .get(null)


    /**
     * Get the static object
     * @param clazz class
     * @param fieldName field name
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses
     * @return field object or null
     * @throws NoSuchFieldException if the field is not found
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
     * @throws NoSuchFieldException if the field is not found
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
     * @throws NoSuchFieldException if the field is not found
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
     * @throws NoSuchFieldException if the field is not found
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setStaticObject(clazz: Class<*>, fieldName: String, value: Any?) =
        clazz.declaredFields.firstOrNull { it.isStatic && fieldName == it.name }
            .let {
                it?.also { f -> f.isAccessible = true } ?: throw NoSuchFieldException("No such static field $fieldName in class ${clazz.name}.")
            }
            .set(null, value)

    /**
     * Set the static object
     * @param clazz class
     * @param fieldName field name
     * @param value field value
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses
     * @throws NoSuchFieldException if the field is not found
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

    /**
     * Invoke the static method(best match params) in the class
     * @param clz class
     * @param methodName method name
     * @param returnType return type (or null if ignore)
     * @param params method params
     * @return method result
     * @throws NoSuchMethodException if the method is not found
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class)
    fun invokeStaticMethodBestMatch(clz: Class<*>, methodName: String, returnType: Class<*>? = null, vararg params: Any?): Any? {
        val paramTypes = params.map { it?.let { it::class.java } }.toTypedArray()
        val mf = clz.methodFinder()
            .filterStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByAssignableReturnType(returnType) }
            .filterByAssignableParamTypes(*paramTypes)

        val m = mf.firstOrNull() ?: mf.findSuper()
            .filterStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByAssignableReturnType(returnType) }
            .filterByAssignableParamTypes(*paramTypes)
            .firstOrNull()
        ?: throw NoSuchMethodException("No such best match method $methodName in ${clz.name} and its superclasses.")

        return m.invoke(null, *params)
    }

    /**
     * Invoke the static method in the class
     * @param clz class
     * @param methodName method name
     * @param returnType return type (or null if ignore)
     * @param paramTypes method param types
     * @param params method params
     * @return method result
     * @throws NoSuchMethodException if the method is not found
     * @throws IllegalArgumentException if the paramTypes size != params size
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeStaticMethod(clz: Class<*>, methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes, params: Params): Any? {
        val mf = clz.methodFinder()
            .filterStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByReturnType(returnType) }
            .filterByParamTypes(*paramTypes.types)

        val m = mf.firstOrNull() ?: mf.findSuper()
            .filterStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByReturnType(returnType) }
            .filterByParamTypes(*paramTypes.types)
            .first()

        return m.invoke(null, *params.params)
    }

    /**
     * Check if two classes are equal or match the same primitive type
     * @param clz1 class1
     * @param clz2 class2
     * @return `true` if two classes are equal or match the same primitive type, else `false`
     */
    @JvmStatic
    fun isPrimitiveTypeMatch(clz1: Class<*>, clz2: Class<*>): Boolean {
        return toPrimitiveType(clz1) == toPrimitiveType(clz2)
    }

    /**
     * Cast class to primitive type if possible
     * @param clz class
     * @return primitive type class if possible or itself
     */
    @JvmStatic
    fun toPrimitiveType(clz: Class<*>): Class<out Any> {
        if (clz.isPrimitive) return clz
        return when (clz) {
            java.lang.Boolean::class.java -> Boolean::class.javaPrimitiveType!!
            java.lang.Byte::class.java -> Byte::class.javaPrimitiveType!!
            java.lang.Short::class.java -> Short::class.javaPrimitiveType!!
            java.lang.Integer::class.java -> Int::class.javaPrimitiveType!!
            java.lang.Long::class.java -> Long::class.javaPrimitiveType!!
            java.lang.Float::class.java -> Float::class.javaPrimitiveType!!
            java.lang.Double::class.java -> Double::class.javaPrimitiveType!!
            java.lang.Character::class.java -> Char::class.javaPrimitiveType!!
            else -> clz
        }
    }

    /**
     * Create a new instance of the class
     * @param clz class
     * @param paramTypes constructor param types
     * @param params constructor params
     * @return new instance
     * @throws NoSuchMethodException if the constructor is not found
     */
    @Throws(NoSuchMethodException::class)
    fun newInstance(clz: Class<*>, paramTypes: ParamTypes = paramTypes(), params: Params = params()): Any {
        val cf = clz.constructorFinder().filterByParamTypes(*paramTypes.types)

        val c = cf.firstOrNull() ?: throw NoSuchMethodException("No such constructor found in ${clz.name}.")
        return c.newInstance(*params.params)
    }

    /**
     * Create a new instance of the class
     * @param clz class
     * @param params constructor params
     * @return new instance
     * @throws NoSuchMethodException if the constructor is not found
     */
    @Throws(NoSuchMethodException::class)
    fun newInstanceBestMatch(clz: Class<*>, vararg params: Any?): Any {
        val paramTypes = params.map { it?.let { it::class.java } }.toTypedArray()
        val cf = clz.constructorFinder().filterByAssignableParamTypes(*paramTypes)

        val c = cf.firstOrNull() ?: throw NoSuchMethodException("No such best match constructor found in ${clz.name}.")
        return c.newInstance(*params)
    }
}