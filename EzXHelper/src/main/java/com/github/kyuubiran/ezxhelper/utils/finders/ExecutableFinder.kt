@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope
import java.lang.reflect.Member
import java.lang.reflect.Modifier

context (IXposedScope)
abstract class ExecutableFinder<E : Member, Self>(seq: Sequence<E>) : BaseMemberFinder<E, Self>(seq) {
    // #region filter by
    fun filterByParamTypes(vararg paramTypes: Class<*>?) = applyThis {
        memberSequence.filter f@{
            val pt = getParameterTypes(it)
            if (pt.size != paramTypes.size) return@f false

            for (i in pt.indices) {
                if (paramTypes[i] == null) continue
                if (pt[i] != paramTypes[i]) return@f false
            }

            true
        }
    }

    fun filterByParamTypes(predicate: (Array<Class<*>>) -> Boolean) = applyThis {
        memberSequence.filter { predicate(getParameterTypes(it)) }
    }

    fun filterByParamCount(count: Int) = applyThis {
        memberSequence.filter { getParameterTypes(it).size == count }
    }

    fun filterByParamCount(predicate: (Int) -> Boolean) = applyThis {
        memberSequence.filter { predicate(getParameterTypes(it).size) }
    }

    fun filterByParamCount(range: IntRange) = applyThis {
        memberSequence.filter { getParameterTypes(it).size in range }
    }

    fun filterByExceptionTypes(vararg exceptionTypes: Class<*>) = applyThis {
        val set = exceptionTypes.toSet()
        memberSequence.filter { getExceptionTypes(it).toSet() == set }
    }
    // #endregion

    // #region filter modifiers
    fun filterNative() = filterIncludeModifiers(Modifier.NATIVE)
    fun filterNonNative() = filterExcludeModifiers(Modifier.NATIVE)

    fun filterVarargs() = filterIncludeModifiers(0x0080)
    fun filterNonVarargs() = filterExcludeModifiers(0x0080)
    // #endregion

    // #region abstract
    protected abstract fun getParameterTypes(member: E): Array<Class<*>>
    protected abstract fun getExceptionTypes(member: E): Array<Class<*>>
    // #endregion
}