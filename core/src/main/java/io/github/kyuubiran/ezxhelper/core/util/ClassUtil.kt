package io.github.kyuubiran.ezxhelper.core.util

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.extension.MemberExtension.isStatic
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.misc.ParamTypes
import io.github.kyuubiran.ezxhelper.core.misc.Params
import io.github.kyuubiran.ezxhelper.core.misc.paramTypes
import io.github.kyuubiran.ezxhelper.core.misc.params

object ClassUtil {

    /**
     * Load the class or null if not found
     *
     * 尝试加载类，没有找到则返回 null
     *
     * @param className class name | 类名
     * @param cl class loader | 类加载器
     * @return class or null | 失败时返回 null
     */
    @JvmStatic
    fun loadClassOrNull(className: String, cl: ClassLoader? = null): Class<*>? = try {
        Class.forName(className, false, cl ?: ClassLoaderProvider.safeClassLoader)
    } catch (e: ClassNotFoundException) {
        null
    }

    /**
     * Load the class or throw exception if not found
     *
     * 尝试加载类，没有找到则抛出 [ClassNotFoundException]
     *
     * @param className class name | 类名
     * @param cl class loader | 类加载器
     * @return class or throw [ClassNotFoundException] | 失败时抛出 [ClassNotFoundException]
     */
    @JvmStatic
    @Throws(ClassNotFoundException::class)
    fun loadClass(className: String, cl: ClassLoader? = null): Class<*> =
        Class.forName(className, false, cl ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Load the first exists class or throw exception all not found
     *
     * 尝试加载第一个存在的类，如果都找不到则抛出 [ClassNotFoundException]
     *
     * @param className class name | 类名
     * @param cl class loader | 类加载器
     * @return class or throw [ClassNotFoundException] | 失败时抛出 [ClassNotFoundException]
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
     *
     * 尝试加载第一个存在的类，如果都找不到则抛出 [ClassNotFoundException]
     *
     * @param className class name | 类名
     * @return class or throw [ClassNotFoundException] | 失败时抛出 [ClassNotFoundException]
     */
    @JvmStatic
    @Throws(ClassNotFoundException::class)
    fun loadFirstClass(vararg className: String): Class<*> = loadFirstClass(ClassLoaderProvider.safeClassLoader, *className)

    /**
     * Load the first exists class or null
     *
     * 尝试加载第一个存在的类，如果都找不到则返回 null
     *
     * @param className class name | 类名
     * @param cl class loader | 类加载器
     * @return class or null | 失败时返回 null
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
     *
     * 尝试加载第一个存在的类，如果都找不到则返回 null
     *
     * @param className class name | 类名
     * @return class or null | 失败时返回 null
     */
    @JvmStatic
    fun loadFirstClassOrNull(vararg className: String): Class<*>? = loadFirstClassOrNull(ClassLoaderProvider.safeClassLoader, *className)

    /**
     * Get the static object
     *
     * 获取静态字段对象
     *
     * @param clazz class | 类
     * @param fieldName field name | 字段名
     * @return field object | 字段对象
     * @throws NoSuchFieldException if the field is not found | 没有找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getStaticObject(clazz: Class<*>, fieldName: String): Any? =
        clazz.declaredFields.firstOrNull { it.isStatic && fieldName == it.name }
            .let {
                it?.also { f -> f.isAccessible = true } ?: throw NoSuchFieldException("No such static field $fieldName in class ${clazz.name}.")
            }
            .get(null)

    /**
     * Get the static object
     *
     * 获取静态字段对象
     *
     * @param clazz class | 类
     * @param fieldName field name | 字段名
     * @return field object or null | 没有找到字段则返回 null
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getStaticObjectOrNull(clazz: Class<*>, fieldName: String): Any? = try {
        getStaticObject(clazz, fieldName)
    } catch (e: Exception) {
        null
    }

    /**
     * Get the static object
     *
     * 获取静态字段对象
     *
     * @param clazz class | 类
     * @param fieldName field name | 字段名
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses | 直到父类(true = 中断, false = 继续), 或者为 null 如果在所有父类中查找
     * @return field object | 字段对象
     * @throws NoSuchFieldException if the field is not found | 没有找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getStaticObjectUntilSuperclass(clazz: Class<*>, fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): Any? {
        var clz: Class<*>? = clazz
        while (clz != Any::class.java) {
            if (clz == null) break
            if (untilSuperClass?.invoke(clz) == true) break

            try {
                return getStaticObject(clz, fieldName)
            } catch (e: NoSuchFieldException) {
                clz = clazz.superclass
            }
        }
        throw NoSuchFieldException("No such static field $fieldName in ${clazz.name} and its superclasses.")
    }

    /**
     * Get the static object until the specified superclass, or null if [NoSuchFieldException] caused
     *
     * 获取静态字段对象，直到指定的父类，如果未找到字段则返回 null
     *
     * @param clazz class | 类
     * @param fieldName field name | 字段名
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses | 直到父类(true = 中断, false = 继续), 或者为 null 如果在所有父类中查找
     * @return field object | 没有找到字段则返回 null
     */
    @JvmStatic
    fun getStaticObjectOrNullUntilSuperclass(
        clazz: Class<*>,
        fieldName: String,
        untilSuperClass: (Class<*>.() -> Boolean)? = null
    ): Any? = try {
        getStaticObjectUntilSuperclass(clazz, fieldName, untilSuperClass)
    } catch (e: Exception) {
        null
    }

    /**
     * Set the static object
     *
     * 设置静态字段对象
     *
     * @param clazz class | 类
     * @param fieldName field name | 字段名
     * @param value field value | 字段值
     * @throws NoSuchFieldException if the field is not found | 没有找到字段则抛出 [NoSuchFieldException]
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
     *
     * 设置静态字段对象
     *
     * @param clazz class | 类
     * @param fieldName field name | 字段名
     * @param value field value | 字段值
     * @param untilSuperClass until super class(true = break, false = continue), or null if find in all superclasses | 直到父类(true = 中断, false = 继续), 或者为 null 如果在所有父类中查找
     * @throws NoSuchFieldException if the field is not found | 没有找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setStaticObjectUntilSuperclass(clazz: Class<*>, fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) {
        var clz: Class<*>? = clazz
        while (clz != Any::class.java) {
            if (clz == null) break
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
     *
     * 调用类中的静态方法(最佳匹配参数)
     *
     * @param clz class | 类
     * @param methodName method name | 方法名
     * @param returnType return type (or null if ignore) | 返回类型(或 null 如果忽略)
     * @param params method params | 方法参数
     * @return method result | 方法返回值
     * @throws NoSuchMethodException if the method is not found | 没有找到方法则抛出 [NoSuchMethodException]
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class)
    fun invokeStaticMethodBestMatch(clz: Class<*>, methodName: String, returnType: Class<*>? = null, vararg params: Any?): Any? {
        val paramTypes = params.map { it?.let { it::class.java } }.toTypedArray()
        val mf = clz.methodFinder()
            .filterStatic()
            .filterByName(methodName)
            .let { if (returnType != null) it.filterByAssignableReturnType(returnType) else it }
            .filterByAssignableParamTypes(*paramTypes)

        val m = mf.firstOrNull() ?: mf.findSuper()
            .filterStatic()
            .filterByName(methodName)
            .let { if (returnType != null) it.filterByAssignableReturnType(returnType) else it }
            .filterByAssignableParamTypes(*paramTypes)
            .firstOrNull()
        ?: throw NoSuchMethodException("No such best match method $methodName in ${clz.name} and its superclasses.")

        return m.invoke(null, *params)
    }

    /**
     * Invoke the static method in the class
     *
     * 调用类中的静态方法
     *
     * @param clz class | 类
     * @param methodName method name | 方法名
     * @param returnType return type (or null if ignore) | 返回类型(或 null 如果忽略)
     * @param paramTypes method param types | 方法参数类型
     * @param params method params | 方法参数
     * @return method result | 方法返回值
     * @throws NoSuchMethodException if the method is not found | 没有找到方法则抛出 [NoSuchMethodException]
     * @throws IllegalArgumentException if the paramTypes size != params size | 如果 paramTypes 的大小与 params 的大小不匹配则抛出 [IllegalArgumentException]
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeStaticMethod(clz: Class<*>, methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes, params: Params): Any? {
        val mf = clz.methodFinder()
            .filterStatic()
            .filterByName(methodName)
            .let { if (returnType != null) it.filterByReturnType(returnType) else it }
            .filterByParamTypes(*paramTypes.types)

        val m = mf.firstOrNull() ?: mf.findSuper()
            .filterStatic()
            .filterByName(methodName)
            .let { if (returnType != null) it.filterByReturnType(returnType) else it }
            .filterByParamTypes(*paramTypes.types)
            .first()

        return m.invoke(null, *params.params)
    }


    /**
     * Check if two classes are equal or match the same primitive type
     *
     * 检查两个类是否相等或匹配相同的原始类型
     *
     * @param clz1 class1 | 类1
     * @param clz2 class2 | 类2
     * @return `true` if two classes are equal or match the same primitive type, else `false` | 如果两个类相等或匹配相同的原始类型则返回 `true`，否则返回 `false`
     */
    @JvmStatic
    fun isPrimitiveTypeMatch(clz1: Class<*>, clz2: Class<*>): Boolean {
        return toPrimitiveType(clz1) == toPrimitiveType(clz2)
    }

    /**
     * Cast class to primitive type if possible
     *
     * 如果可能，则将类转换为原始类型
     *
     * @param clz class | 类
     * @return primitive type class if possible or itself | 成功则返回原始类型类，否则返回自身
     */
    @JvmStatic
    @Suppress("RemoveRedundantQualifierName")
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
     *
     * 创建类的新实例
     *
     * @param clz class | 类
     * @param paramTypes constructor param types | 构造器参数类型
     * @param params constructor params | 构造器参数
     * @return new instance | 新实例
     * @throws NoSuchMethodException if the constructor is not found | 没有找到构造器则抛出 [NoSuchMethodException]
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class)
    fun newInstance(clz: Class<*>, paramTypes: ParamTypes = paramTypes(), params: Params = params()): Any {
        val cf = clz.constructorFinder().filterByParamTypes(*paramTypes.types)

        val c = cf.firstOrNull() ?: throw NoSuchMethodException("No such constructor found in ${clz.name}.")
        return c.newInstance(*params.params)
    }

    /**
     * Create a new instance(best match params) of the class
     *
     * 创建类的新实例，使用最佳匹配的构造器
     *
     * @param clz class | 类
     * @param params constructor params | 构造器参数
     * @return new instance | 新实例
     * @throws NoSuchMethodException if the constructor is not found | 没有找到构造器则抛出 [NoSuchMethodException]
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class)
    fun newInstanceBestMatch(clz: Class<*>, vararg params: Any?): Any {
        val paramTypes = params.map { it?.let { it::class.java } }.toTypedArray()
        val cf = clz.constructorFinder().filterByAssignableParamTypes(*paramTypes)

        val c = cf.firstOrNull() ?: throw NoSuchMethodException("No such best match constructor found in ${clz.name}.")
        return c.newInstance(*params)
    }
}