@file:Suppress("unused")

package io.github.kyuubiran.ezxhelper.core.util

import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.misc.ParamTypes
import io.github.kyuubiran.ezxhelper.core.misc.Params
import io.github.kyuubiran.ezxhelper.core.misc.paramTypes
import io.github.kyuubiran.ezxhelper.core.misc.params

object ObjectUtil {

    /**
     * Get the field object by the name in the object
     *
     * 获取对象中指定名称的字段对象
     *
     * @param obj this object | 对象
     * @param fieldName field name | 字段名称
     * @return field object | 字段对象
     * @throws NoSuchFieldException if the field is not found | 如果未找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getObject(obj: Any, fieldName: String): Any? = obj::class.java.getDeclaredField(fieldName).also { it.isAccessible = true }.get(obj)

    /**
     * Get the field object by the name in the object
     *
     * 获取对象中指定名称的字段对象
     *
     * @param obj this object | 对象
     * @param fieldName field name | 字段名称
     * @param clazz class where the field is declared (optional, defaults to the class of 'obj') | 字段声明所在的类（可选，默认为 'obj' 的类）
     * @return field object | 字段对象
     * @throws NoSuchFieldException if the field is not found | 如果未找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getObject(obj: Any, fieldName: String, clazz: Class<*>? = null): Any? =
        (clazz ?: obj::class.java).getDeclaredField(fieldName).also { it.isAccessible = true }.get(obj)

    /**
     * Get the field object by the name until the specified superclass
     *
     * 获取对象中指定名称的字段对象，直到指定的父类
     *
     * @param obj this object | 对象
     * @param fieldName field name | 字段名称
     * @param untilSuperClass until super class(true = break, false = continue), null = find in all superclasses | 直到父类（true = 中断，false = 继续），null = 在所有父类中查找
     * @return field object | 字段对象
     * @throws NoSuchFieldException if the field is not found | 如果未找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getObjectUntilSuperclass(obj: Any, fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): Any? {
        var clazz: Class<*>? = obj::class.java
        while (clazz != Any::class.java) {
            if (clazz == null) break
            if (untilSuperClass?.invoke(clazz) == true) break

            try {
                return getObject(obj, fieldName, clazz)
            } catch (e: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        throw NoSuchFieldException("No such field $fieldName in ${obj::class.java.name} and its superclasses.")
    }

    /**
     * Set the field object by the name in the object
     *
     * 设置对象中指定名称的字段对象
     *
     * @param obj this object | 对象
     * @param fieldName field name | 字段名称
     * @param value new value | 新值
     * @throws NoSuchFieldException if the field is not found | 如果未找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setObject(obj: Any, fieldName: String, value: Any?) =
        obj::class.java.getDeclaredField(fieldName).also { it.isAccessible = true }.set(obj, value)

    /**
     * Set the field object by the name in the object
     *
     * 设置对象中指定名称的字段对象
     *
     * @param obj this object | 对象
     * @param fieldName field name | 字段名称
     * @param clazz class where the field is declared (optional, defaults to the class of 'obj') | 字段声明所在的类（可选，默认为 'obj' 的类）
     * @throws NoSuchFieldException if the field is not found | 如果未找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setObject(obj: Any, fieldName: String, value: Any?, clazz: Class<*>? = null) =
        (clazz ?: obj::class.java).getDeclaredField(fieldName).also { it.isAccessible = true }.set(obj, value)

    /**
     * Set the field object by the name until the specified superclass
     *
     * 设置对象中指定名称的字段对象，直到指定的父类
     *
     * @param obj this object | 对象
     * @param fieldName field name
     * @param untilSuperClass until super class(true = break, false = continue), null = find in all superclasses. | 直到父类（true = 中断，false = 继续），null = 在所有父类中查找
     * @throws NoSuchFieldException if the field is not found | 如果未找到字段则抛出 [NoSuchFieldException]
     */
    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun setObjectUntilSuperclass(obj: Any, fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) {
        var clazz: Class<*>? = obj::class.java
        while (clazz != Any::class.java) {
            if (clazz == null) break
            if (untilSuperClass?.invoke(clazz) == true) break

            try {
                return setObject(obj, fieldName, value, clazz)
            } catch (e: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        throw NoSuchFieldException("No such field $fieldName in ${obj::class.java.name} and its superclasses.")
    }

    /**
     * Invoke the object method(best match params)
     *
     * 调用对象方法（最佳匹配参数）
     *
     * @param obj this object | 对象
     * @param methodName method name | 方法名称
     * @param returnType return type (or null if ignore) | 返回类型（或 null 如果忽略）
     * @param params method params | 方法参数
     * @return method result | 方法结果
     * @throws NoSuchMethodException if the method is not found | 如果未找到方法则抛出 [NoSuchMethodException]
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class)
    fun invokeMethodBestMatch(obj: Any, methodName: String, returnType: Class<*>? = null, vararg params: Any?): Any? {
        val paramTypes = params.map { it?.let { it::class.java } }.toTypedArray()
        val mf = obj::class.java.methodFinder()
            .filterNonStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByAssignableReturnType(returnType) }
            .filterByAssignableParamTypes(*paramTypes)

        val m = mf.firstOrNull() ?: mf.findSuper()
            .filterNonStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByAssignableReturnType(returnType) }
            .filterByAssignableParamTypes(*paramTypes)
            .firstOrNull()
        ?: throw NoSuchMethodException("No best match method $methodName in ${obj::class.java.name} and its superclasses.")

        return m.invoke(obj, *params)
    }

    /**
     * Invoke the object method
     *
     * 调用对象方法
     *
     * @param obj this object | 对象
     * @param methodName method name | 方法名称
     * @param returnType return type (or null if ignore) | 返回类型（或 null 如果忽略）
     * @param paramTypes method param types | 方法参数类型
     * @param params method params | 方法参数
     * @return method result | 方法结果
     * @throws NoSuchMethodException if the method is not found | 如果未找到方法则抛出 [NoSuchMethodException]
     * @throws IllegalArgumentException if the paramTypes size != params size | 如果 paramTypes 的大小与 params 的大小不匹配则抛出 [IllegalArgumentException]
     */
    @JvmStatic
    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeMethod(
        obj: Any,
        methodName: String,
        returnType: Class<*>? = null,
        paramTypes: ParamTypes = paramTypes(),
        params: Params = params()
    ): Any? {
        if (paramTypes.types.size != params.params.size) throw IllegalArgumentException("paramTypes size != params size")

        val mf = obj::class.java.methodFinder()
            .filterNonStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByReturnType(returnType) }
            .filterByParamTypes(*paramTypes.types)

        val m = mf.firstOrNull() ?: mf.findSuper()
            .filterNonStatic()
            .filterByName(methodName)
            .apply { if (returnType != null) filterByReturnType(returnType) }
            .filterByParamTypes(*paramTypes.types)
            .first()

        return m.invoke(obj, *params.params)
    }
}