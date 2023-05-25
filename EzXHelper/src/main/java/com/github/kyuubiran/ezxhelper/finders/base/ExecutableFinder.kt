@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders.base

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
                val clz1 = pt[i]
                val clz2 = paramTypes[i] ?: continue
                if (clz1 != clz2) return@f false
            }

            true
        }
        exceptMessageScope { condition("filterByParamTypes(${paramTypes.map { it?.name ?: "<ignored>" }})") }
    }

    /**
     * Filter by parameter types or subclass of types, or if null to skip check some parameters
     * @param paramTypes parameter types
     * @return [Self] this finder
     */
    fun filterByAssignableParamTypes(vararg paramTypes: Class<*>?) = applyThis {
        sequence = sequence.filter f@{
            val pt = getParameterTypes(it)
            if (pt.size != paramTypes.size) return@f false

            for (i in pt.indices) {
                val clz1 = pt[i]
                val clz2 = paramTypes[i] ?: continue
                if (clz2.isAssignableFrom(clz1) || clz1.isAssignableFrom(clz2)) continue
                if (clz1 != clz2) return@f false
            }

            true
        }
        exceptMessageScope { condition("filterByAssignableParamTypes(${paramTypes.map { it?.name ?: "<ignored>" }})") }
    }

    /**
     * Filter the executable if the parameter is empty
     * @return [Self] this finder
     */
    fun filterEmptyParam() = applyThis {
        sequence = sequence.filter { getParameterTypes(it).isEmpty() }
        exceptMessageScope { condition("filterEmptyParam") }
    }

    /**
     * Filter the executable if the parameter is not empty
     * @return [Self] this finder
     */
    fun filterNotEmptyParam() = applyThis {
        sequence = sequence.filter { getParameterTypes(it).isNotEmpty() }
        exceptMessageScope { condition("filterNotEmptyParam") }
    }

    /**
     * Use condition to filter parameter types
     * @param predicate condition
     * @return [Self] this finder
     */
    fun filterByParamTypes(predicate: (Array<Class<*>>) -> Boolean) = applyThis {
        sequence = sequence.filter { predicate(getParameterTypes(it)) }
        exceptMessageScope { condition("filterByParamTypes(CustomCondition)") }
    }

    /**
     * Filter by parameter count
     * @param count parameter count
     * @return [Self] this finder
     */
    fun filterByParamCount(count: Int) = applyThis {
        sequence = sequence.filter { getParameterTypes(it).size == count }
        exceptMessageScope { condition("filterByParamCount(count == $count)") }
    }

    /**
     * Use condition to filter parameter count
     * @param predicate condition
     * @return [Self] this finder
     */
    fun filterByParamCount(predicate: (Int) -> Boolean) = applyThis {
        sequence = sequence.filter { predicate(getParameterTypes(it).size) }
        exceptMessageScope { condition("filterByParamCount(CustomCondition)") }
    }

    /**
     * Filter by parameter count in range
     * @param range parameter count range
     * @return [Self] this finder
     */
    fun filterByParamCount(range: IntRange) = applyThis {
        sequence = sequence.filter { getParameterTypes(it).size in range }
        exceptMessageScope { condition("filterByParamCount(${range.first} <= count <= ${range.last})") }
    }

    /**
     * Filter by exception types
     * @param exceptionTypes exception types
     * @return [Self] this finder
     */
    fun filterByExceptionTypes(vararg exceptionTypes: Class<*>) = applyThis {
        val set = exceptionTypes.toSet()
        sequence = sequence.filter { getExceptionTypes(it).run { size == set.size && toSet() == set } }
        exceptMessageScope { condition("filterByExceptionTypes(${set.map { it.name }})") }
    }

    // endregion

    // region filter modifiers

    /**
     * Filter if they are native.
     * @return [Self] this finder
     */
    fun filterNative() = applyThis {
        sequence = sequence.filter { Modifier.isNative(it.modifiers) }
        exceptMessageScope { condition("filterNative") }
    }

    /**
     * Filter if they are non-native.
     * @return [Self] this finder
     */
    fun filterNonNative() = applyThis {
        sequence = sequence.filter { !Modifier.isNative(it.modifiers) }
        exceptMessageScope { condition("filterNonNative") }
    }

    /**
     * Filter if they are varargs.
     * @return [Self] this finder
     */
    fun filterVarargs() = applyThis {
        sequence = sequence.filter { it.modifiers and MemberExtensions.VARARGS != 0 }
        exceptMessageScope { condition("filterVarargs") }
    }

    /**
     * Filter if they are non-varargs.
     * @return [Self] this finder
     */
    fun filterNonVarargs() = applyThis {
        sequence = sequence.filter { it.modifiers and MemberExtensions.VARARGS == 0 }
        exceptMessageScope { condition("filterNonVarargs") }
    }

    // endregion

    // region abstracts

    protected abstract fun getParameterTypes(member: E): Array<Class<*>>
    protected abstract fun getExceptionTypes(member: E): Array<Class<*>>

    // endregion
}