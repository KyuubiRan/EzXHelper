@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finders.base

import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions
import io.github.kyuubiran.ezxhelper.core.utils.ClassUtils
import java.lang.reflect.Member
import java.lang.reflect.Modifier

abstract class ExecutableFinder<E : Member, Self>(seq: Sequence<E>) : BaseMemberFinder<E, Self>(seq) {
    // region filter by

    /**
     * Filter by parameter types, or if null to skip check some parameters
     * @param paramTypes parameter types
     * @return [Self] this finder
     */
    fun filterByParamTypes(vararg paramTypes: Class<*>?) = filter f@{
        val pt = getParameterTypes(this@f)
        if (pt.size != paramTypes.size) return@f false

        for (i in pt.indices) {
            val clz1 = pt[i]
            val clz2 = paramTypes[i] ?: continue
            if (clz1 != clz2) return@f false
        }

        true
    }

    /**
     * Filter by parameter types or subclass of types, or if null to skip check some parameters
     * @param paramTypes parameter types
     * @return [Self] this finder
     */
    fun filterByAssignableParamTypes(vararg paramTypes: Class<*>?) = filter f@{
        val pt = getParameterTypes(this@f)
        if (pt.size != paramTypes.size) return@f false

        for (i in pt.indices) {
            val clz1 = pt[i]
            val clz2 = paramTypes[i] ?: continue
            if (clz2.isAssignableFrom(clz1) || clz1.isAssignableFrom(clz2)) continue
            if (ClassUtils.isPrimitiveTypeMatch(clz1, clz2)) continue
            if (clz1 != clz2) return@f false
        }

        true
    }

    /**
     * Filter the executable if the parameter is empty
     * @return [Self] this finder
     */
    fun filterEmptyParam() = filter { getParameterTypes(this).isEmpty() }

    /**
     * Filter the executable if the parameter is not empty
     * @return [Self] this finder
     */
    fun filterNotEmptyParam() = filter { getParameterTypes(this).isNotEmpty() }

    /**
     * Use condition to filter parameter types
     * @param predicate condition
     * @return [Self] this finder
     */
    fun filterByParamTypes(predicate: (Array<Class<*>>) -> Boolean) = filter { predicate(getParameterTypes(this)) }


    /**
     * Filter by parameter count
     * @param count parameter count
     * @return [Self] this finder
     */
    fun filterByParamCount(count: Int) = filter { getParameterTypes(this).size == count }

    /**
     * Use condition to filter parameter count
     * @param predicate condition
     * @return [Self] this finder
     */
    fun filterByParamCount(predicate: (Int) -> Boolean) = filter { predicate(getParameterTypes(this).size) }

    /**
     * Filter by parameter count in range
     * @param range parameter count range
     * @return [Self] this finder
     */
    fun filterByParamCount(range: IntRange) = filter { getParameterTypes(this).size in range }

    /**
     * Filter by exception types
     * @param exceptionTypes exception types
     * @return [Self] this finder
     */
    fun filterByExceptionTypes(vararg exceptionTypes: Class<*>) = exceptionTypes.toSet().let { set ->
        filter { getExceptionTypes(this).run { size == set.size && toSet() == set } }
    }

    // endregion

    // region filter modifiers

    /**
     * Filter if they are native.
     * @return [Self] this finder
     */
    fun filterNative() = filter { Modifier.isNative(modifiers) }

    /**
     * Filter if they are non-native.
     * @return [Self] this finder
     */
    fun filterNonNative() = filter { !Modifier.isNative(modifiers) }

    /**
     * Filter if they are varargs.
     * @return [Self] this finder
     */
    fun filterVarargs() = filter { modifiers and MemberExtensions.VARARGS != 0 }

    /**
     * Filter if they are non-varargs.
     * @return [Self] this finder
     */
    fun filterNonVarargs() = filter { modifiers and MemberExtensions.VARARGS == 0 }

    // endregion

    // region abstracts

    protected abstract fun getParameterTypes(member: E): Array<Class<*>>
    protected abstract fun getExceptionTypes(member: E): Array<Class<*>>

    // endregion
}