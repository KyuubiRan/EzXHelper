@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import java.lang.IllegalArgumentException

/**
 * The scoped helper for [Class] to quick do reflect things
 * @see ClassUtils
 */
@KotlinOnly
class ClassHelper private constructor(private val clz: Class<*>) {
    companion object {
        @JvmSynthetic
        @JvmStatic
        fun Class<*>.classHelper() = ClassHelper(this)

        @JvmSynthetic
        @JvmStatic
        inline fun Class<*>.classHelper(block: ClassHelper.() -> Unit) {
            classHelper().apply(block)
        }

        @JvmSynthetic
        @JvmStatic
        inline fun <T> Class<*>.classHelper(block: ClassHelper.() -> T) = classHelper().run(block)
    }

    @Throws(NoSuchFieldException::class)
    fun getStaticObjectOrNull(fieldName: String) = ClassUtils.getStaticObjectOrNull(clz, fieldName)

    @Throws(NoSuchFieldException::class)
    fun <T> getStaticObjectOrNullAs(fieldName: String) =
        ClassUtils.getStaticObjectOrNullAs<T>(clz, fieldName)

    @Throws(NoSuchFieldException::class)
    fun getStaticObjectOrNullUntilSuperclass(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtils.getStaticObjectOrNullUntilSuperclass(clz, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    fun <T> getStaticObjectOrNullUntilSuperclassAs(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtils.getStaticObjectOrNullUntilSuperclassAs<T>(clz, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    fun setStaticObject(fieldName: String, value: Any?) =
        ClassUtils.setStaticObject(clz, fieldName, value)

    @Throws(NoSuchFieldException::class)
    fun setStaticObjectUntilSuperclass(fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtils.setStaticObjectUntilSuperclass(clz, fieldName, value, untilSuperClass)

    @Throws(NoSuchMethodException::class)
    fun invokeStaticMethodBestMatch(methodName: String, returnType: Class<*>? = null, vararg params: Any?) =
        ClassUtils.invokeStaticMethodBestMatch(clz, methodName, returnType, *params)

    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeStaticMethod(methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes = paramTypes(), params: Params = params()) =
        ClassUtils.invokeStaticMethod(clz, methodName, returnType, paramTypes, params)

    /**
     * Check if two classes are equal or match the same primitive type
     * @param clazz class to compare with
     * @return `true` if two classes are equal or match the same primitive type, else `false`
     */
    fun isPrimitiveTypeMatch(clazz: Class<*>) =
        ClassUtils.isPrimitiveTypeMatch(clz, clazz)

    /**
     * Cast class to primitive type if possible
     * @return primitive type class if possible or itself
     */
    fun toPrimitiveType() =
        ClassUtils.toPrimitiveType(clz)

    @Throws(NoSuchMethodException::class)
    fun newInstanceBestMatch(vararg params: Any?) =
        ClassUtils.newInstanceBestMatch(clz, *params)

    @Throws(NoSuchMethodException::class)
    fun newInstance(paramTypes: ParamTypes = paramTypes(), params: Params = params()) =
        ClassUtils.newInstance(clz, paramTypes, params)
}