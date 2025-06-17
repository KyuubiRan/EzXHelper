package io.github.kyuubiran.ezxhelper.core.helper

import io.github.kyuubiran.ezxhelper.core.misc.ParamTypes
import io.github.kyuubiran.ezxhelper.core.misc.Params
import io.github.kyuubiran.ezxhelper.core.misc.paramTypes
import io.github.kyuubiran.ezxhelper.core.misc.params
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil
import kotlin.reflect.KClass

/**
 * Wrapper for [ClassUtil]
 *
 * [ClassUtil] 的包装类
 *
 * @see ClassUtil
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
    fun getStaticObject(fieldName: String) = ClassUtil.getStaticObject(clazz, fieldName)

    fun getStaticObjectOrNull(fieldName: String) = ClassUtil.getStaticObjectOrNull(clazz, fieldName)

    @Throws(NoSuchFieldException::class)
    fun getStaticObjectUntilSuperclass(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtil.getStaticObjectUntilSuperclass(clazz, fieldName, untilSuperClass)

    fun getStaticObjectOrNullUntilSuperclass(fieldName: String, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtil.getStaticObjectOrNullUntilSuperclass(clazz, fieldName, untilSuperClass)

    @Throws(NoSuchFieldException::class)
    fun setStaticObject(fieldName: String, value: Any?) =
        ClassUtil.setStaticObject(clazz, fieldName, value)

    @Throws(NoSuchFieldException::class)
    fun setStaticObjectUntilSuperclass(fieldName: String, value: Any?, untilSuperClass: (Class<*>.() -> Boolean)? = null) =
        ClassUtil.setStaticObjectUntilSuperclass(clazz, fieldName, value, untilSuperClass)

    @Throws(NoSuchMethodException::class)
    fun invokeStaticMethodBestMatch(methodName: String, returnType: Class<*>? = null, vararg params: Any?) =
        ClassUtil.invokeStaticMethodBestMatch(clazz, methodName, returnType, *params)

    @Throws(NoSuchMethodException::class, IllegalArgumentException::class)
    fun invokeStaticMethod(methodName: String, returnType: Class<*>? = null, paramTypes: ParamTypes = paramTypes(), params: Params = params()) =
        ClassUtil.invokeStaticMethod(clazz, methodName, returnType, paramTypes, params)

    fun isPrimitiveTypeMatch(clazz: Class<*>) = ClassUtil.isPrimitiveTypeMatch(clazz, clazz)

    fun toPrimitiveType() = ClassUtil.toPrimitiveType(clazz)

    @Throws(NoSuchMethodException::class)
    fun newInstanceBestMatch(vararg params: Any?) = ClassUtil.newInstanceBestMatch(clazz, *params)

    @Throws(NoSuchMethodException::class)
    fun newInstance(paramTypes: ParamTypes = paramTypes(), params: Params = params()) = ClassUtil.newInstance(clazz, paramTypes, params)
}