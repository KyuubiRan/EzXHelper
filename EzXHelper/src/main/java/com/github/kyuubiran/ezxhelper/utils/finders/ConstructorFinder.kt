@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope
import java.lang.reflect.Constructor

context (IXposedScope)
class ConstructorFinder<C> internal constructor(seq: Sequence<Constructor<C>>) : ExecutableFinder<Constructor<C>, ConstructorFinder<C>>(seq) {
    // #region filter by
    // #endregion

    // #region filter modifiers
    // #endregion

    // #region overrides
    override fun getParameterTypes(member: Constructor<C>): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Constructor<C>): Array<Class<*>> = member.exceptionTypes
    // #endregion
}