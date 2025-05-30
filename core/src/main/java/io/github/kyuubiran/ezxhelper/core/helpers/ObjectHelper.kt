package io.github.kyuubiran.ezxhelper.core.helpers

import io.github.kyuubiran.ezxhelper.core.miscs.ParamTypes
import io.github.kyuubiran.ezxhelper.core.miscs.Params
import io.github.kyuubiran.ezxhelper.core.utils.ObjectUtils

/**
 * Wrapper for [ObjectUtils]
 * @see ObjectUtils
 */
class ObjectHelper(private val target: Any) {

    companion object {

        @JvmSynthetic
        fun Any.objectHelper() = ObjectHelper(this)

        @JvmSynthetic
        inline fun Any.objectHelper(block: ObjectHelper.() -> Unit) {
            objectHelper().apply(block)
        }

        @JvmSynthetic
        inline fun <T> Any.objectHelper(block: ObjectHelper.() -> T) = objectHelper().run(block)
    }

    @Throws(NoSuchFieldException::class)
    fun getObjectOrNull(fieldName: String): Any? = ObjectUtils.getObjectOrNull(target, fieldName)

    @Throws(NoSuchFieldException::class)
    fun getObjectOrNullUntilSuperclass(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): Any? =
        ObjectUtils.getObjectOrNullUntilSuperclass(target, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    fun <T> getObjectOrNullAs(fieldName: String): T? = ObjectUtils.getObjectOrNullAs(target, fieldName)

    @Throws(NoSuchFieldException::class)
    fun <T> getObjectOrNullUntilSuperclassAs(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null): T? =
        ObjectUtils.getObjectOrNullUntilSuperclassAs(target, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    fun setObject(fieldName: String, value: Any?) = ObjectUtils.setObject(target, fieldName, value)

    @Throws(NoSuchFieldException::class)
    fun setObjectUntilSuperclass(fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ObjectUtils.setObjectUntilSuperclass(target, fieldName, value, untilSuperClass)

    @Throws(NoSuchMethodException::class)
    fun invokeMethodBestMatch(methodName: String, returnType: Class<*>? = null, vararg params: Any?): Any? =
        ObjectUtils.invokeMethodBestMatch(target, methodName, returnType, *params)

    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeMethod(methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes, params: Params): Any? =
        ObjectUtils.invokeMethod(target, methodName, returnType, paramTypes, params)
}