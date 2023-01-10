@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope
import java.lang.reflect.Method
import java.lang.reflect.Modifier

context (IXposedScope)
class MethodFinder private constructor(seq: Sequence<Method>) : BaseExecutableFinder<Method, MethodFinder>(seq) {

    // #region filter by
    fun filterByName(name: String): MethodFinder = applyThis { memberSequence.filter { it.name == name } }
    // #endregion

    // #region filter modifiers
    fun filterStatic() = filterIncludeModifiers(Modifier.STATIC)
    fun filterNonStatic() = filterExcludeModifiers(Modifier.STATIC)
    // #endregion

    // #region overrides
    override fun getParameterTypes(member: Method): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Method): Array<Class<*>> = member.exceptionTypes
    // #endregion
}