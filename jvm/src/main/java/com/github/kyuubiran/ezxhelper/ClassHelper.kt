@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly

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
        inline fun Class<*>.objectHelper(block: ClassHelper.() -> Unit) {
            classHelper().apply(block)
        }

        @JvmSynthetic
        @JvmStatic
        inline fun <T> Class<*>.objectHelper(block: ClassHelper.() -> T) = classHelper().run(block)
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
}