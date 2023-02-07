@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import java.lang.IllegalArgumentException

/**
 * The scoped helper for [Any] to quick do reflect things
 * @see ObjectUtils
 */
@KotlinOnly
class ObjectHelper private constructor(private val obj: Any) {
    companion object {
        @JvmSynthetic
        @JvmStatic
        fun Any.objectHelper() = ObjectHelper(this)

        @JvmSynthetic
        @JvmStatic
        inline fun Any.objectHelper(block: ObjectHelper.() -> Unit) {
            objectHelper().apply(block)
        }

        @JvmSynthetic
        @JvmStatic
        inline fun <T> Any.objectHelper(block: ObjectHelper.() -> T) = objectHelper().run(block)
    }

    @Throws(NoSuchFieldException::class)
    @JvmSynthetic
    fun getObjectOrNull(fieldName: String): Any? = ObjectUtils.getObjectOrNull(obj, fieldName)

    @Throws(NoSuchFieldException::class)
    @JvmSynthetic
    fun getObjectOrNullUntilSuperclass(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): Any? =
        ObjectUtils.getObjectOrNullUntilSuperclass(obj, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    @JvmSynthetic
    fun <T> getObjectOrNullAs(fieldName: String): T? = ObjectUtils.getObjectOrNullAs(obj, fieldName)


    @Throws(NoSuchFieldException::class)
    @JvmSynthetic
    fun <T> getObjectOrNullUntilSuperclassAs(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): T? =
        ObjectUtils.getObjectOrNullUntilSuperclassAs(obj, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    @JvmSynthetic
    fun setObject(fieldName: String, value: Any?) = ObjectUtils.setObject(obj, fieldName, value)

    @Throws(NoSuchFieldException::class)
    @JvmSynthetic
    fun setObjectUntilSuperclass(fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ObjectUtils.setObjectUntilSuperclass(obj, fieldName, value, untilSuperClass)

    @Throws(NoSuchMethodException::class)
    fun invokeMethodBestMatch(methodName: String, returnType: Class<*>? = null, vararg params: Any?): Any? =
        ObjectUtils.invokeMethodBestMatch(obj, methodName, returnType, *params)

    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeMethod(methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes, params: Params): Any? =
        ObjectUtils.invokeMethod(obj, methodName, returnType, paramTypes, params)
}