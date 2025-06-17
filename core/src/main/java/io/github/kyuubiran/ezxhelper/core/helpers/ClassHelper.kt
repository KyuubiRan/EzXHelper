package io.github.kyuubiran.ezxhelper.core.helpers

import io.github.kyuubiran.ezxhelper.core.miscs.ParamTypes
import io.github.kyuubiran.ezxhelper.core.miscs.Params
import io.github.kyuubiran.ezxhelper.core.miscs.paramTypes
import io.github.kyuubiran.ezxhelper.core.miscs.params
import io.github.kyuubiran.ezxhelper.core.utils.ClassUtils
import kotlin.reflect.KClass

/**
 * Wrapper for [ClassUtils]
 *
 * [ClassUtils] 的包装类
 *
 * @see ClassUtils
 */
class ClassHelper(private val clazz: Class<*>) {

    @Suppress("ClassName")
    companion object `-Static` {

        @JvmSynthetic
        fun Class<*>.classHelper() = ClassHelper(this)

        @JvmSynthetic
        fun KClass<*>.classHelper() = ClassHelper(this.java)

        @JvmSynthetic
        inline fun Class<*>.classHelper(block: ClassHelper.() -> Unit) {
            classHelper().apply(block)
        }

        @JvmSynthetic
        inline fun KClass<*>.classHelper(block: ClassHelper.() -> Unit) {
            classHelper().apply(block)
        }

        @JvmSynthetic
        inline fun <T> Class<*>.objectHelper(block: ClassHelper.() -> T) = classHelper().run(block)

        @JvmSynthetic
        inline fun <T> KClass<*>.objectHelper(block: ClassHelper.() -> T) = classHelper().run(block)
    }

    @Throws(NoSuchFieldException::class)
    fun getStaticObject(fieldName: String) = ClassUtils.getStaticObject(clazz, fieldName)

    fun getStaticObjectOrNull(fieldName: String) = ClassUtils.getStaticObjectOrNull(clazz, fieldName)

    @Throws(NoSuchFieldException::class)
    fun getStaticObjectUntilSuperclass(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtils.getStaticObjectUntilSuperclass(clazz, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    fun setStaticObject(fieldName: String, value: Any?) =
        ClassUtils.setStaticObject(clazz, fieldName, value)

    @Throws(NoSuchFieldException::class)
    fun setStaticObjectUntilSuperclass(fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtils.setStaticObjectUntilSuperclass(clazz, fieldName, value, untilSuperClass)

    @Throws(NoSuchMethodException::class)
    fun invokeStaticMethodBestMatch(methodName: String, returnType: Class<*>? = null, vararg params: Any?) =
        ClassUtils.invokeStaticMethodBestMatch(clazz, methodName, returnType, *params)

    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeStaticMethod(methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes = paramTypes(), params: Params = params()) =
        ClassUtils.invokeStaticMethod(clazz, methodName, returnType, paramTypes, params)

    fun isPrimitiveTypeMatch(clazz: Class<*>) = ClassUtils.isPrimitiveTypeMatch(clazz, clazz)

    fun toPrimitiveType() = ClassUtils.toPrimitiveType(clazz)

    @Throws(NoSuchMethodException::class)
    fun newInstanceBestMatch(vararg params: Any?) = ClassUtils.newInstanceBestMatch(clazz, *params)

    @Throws(NoSuchMethodException::class)
    fun newInstance(paramTypes: ParamTypes = paramTypes(), params: Params = params()) = ClassUtils.newInstance(clazz, paramTypes, params)
}