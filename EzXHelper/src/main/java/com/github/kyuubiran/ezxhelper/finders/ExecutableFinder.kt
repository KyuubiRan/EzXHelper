@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.MemberExtensions
import java.lang.reflect.Member
import java.lang.reflect.Modifier

abstract class ExecutableFinder<E : Member, Self>(seq: Sequence<E>) : BaseMemberFinder<E, Self>(seq) {
    // region filter by
    /**
     * Filter by parameter types, or if null to skip check some parameters
     * @param paramTypes parameter types
     * @return [Self] this finder
     */
    fun filterByParamTypes(vararg paramTypes: Class<*>?) = applyThis {
        sequence = sequence.filter f@{
            val pt = getParameterTypes(it)
            if (pt.size != paramTypes.size) return@f false

            for (i in pt.indices) {
                if (paramTypes[i] == null) continue
                if (pt[i] != paramTypes[i]) return@f false
            }

            true
        }
    }

    /**
     * Use condition to filter parameter types
     * @param predicate condition
     * @return [Self] this finder
     */
    fun filterByParamTypes(predicate: (Array<Class<*>>) -> Boolean) = applyThis {
        sequence = sequence.filter { predicate(getParameterTypes(it)) }
    }

    /**
     * Filter by parameter count
     * @param count parameter count
     * @return [Self] this finder
     */
    fun filterByParamCount(count: Int) = applyThis {
        sequence = sequence.filter { getParameterTypes(it).size == count }
    }

    /**
     * Use condition to filter parameter count
     * @param predicate condition
     * @return [Self] this finder
     */
    fun filterByParamCount(predicate: (Int) -> Boolean) = applyThis {
        sequence = sequence.filter { predicate(getParameterTypes(it).size) }
    }

    /**
     * Filter by parameter count in range
     * @param range parameter count range
     * @return [Self] this finder
     */
    fun filterByParamCount(range: IntRange) = applyThis {
        sequence = sequence.filter { getParameterTypes(it).size in range }
    }

    /**
     * Filter by exception types
     * @param exceptionTypes exception types
     * @return [Self] this finder
     */
    fun filterByExceptionTypes(vararg exceptionTypes: Class<*>) = applyThis {
        val set = exceptionTypes.toSet()
        sequence = sequence.filter { getExceptionTypes(it).run { size == set.size && toSet() == set } }
    }
    // endregion

    // region filter modifiers
    /**
     * Filter if they are native.
     * @return [Self] this finder
     */
    fun filterNative() = filterIncludeModifiers(Modifier.NATIVE)

    /**
     * Filter if they are non-native.
     * @return [Self] this finder
     */
    fun filterNonNative() = filterExcludeModifiers(Modifier.NATIVE)

    /**
     * Filter if they are varargs.
     * @return [Self] this finder
     */
    fun filterVarargs() = filterIncludeModifiers(MemberExtensions.VARARGS)

    /**
     * Filter if they are non-varargs.
     * @return [Self] this finder
     */
    fun filterNonVarargs() = filterExcludeModifiers(MemberExtensions.VARARGS)
    // endregion

    // region overrides
    // endregion

    // region abstracts
    protected abstract fun getParameterTypes(member: E): Array<Class<*>>
    protected abstract fun getExceptionTypes(member: E): Array<Class<*>>
    // endregion
}