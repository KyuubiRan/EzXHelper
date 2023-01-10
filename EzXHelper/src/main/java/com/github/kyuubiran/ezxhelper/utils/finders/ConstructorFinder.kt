@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import java.lang.reflect.Constructor

class ConstructorFinder internal constructor(seq: Sequence<Constructor<*>>) : ExecutableFinder<Constructor<*>, ConstructorFinder>(seq) {
    companion object {
        fun fromClass(clazz: Class<*>): ConstructorFinder = ConstructorFinder(clazz.declaredConstructors.asSequence())
        fun fromSequence(seq: Sequence<Constructor<*>>): ConstructorFinder = ConstructorFinder(seq)
        fun fromArray(array: Array<Constructor<*>>): ConstructorFinder = ConstructorFinder(array.asSequence())
        fun fromVararg(vararg array: Constructor<*>): ConstructorFinder = ConstructorFinder(array.asSequence())
        fun fromIterable(iterable: Iterable<Constructor<*>>): ConstructorFinder = ConstructorFinder(iterable.asSequence())
    }

    // #region filter by
    // #endregion

    // #region filter modifiers
    // #endregion

    // #region overrides
    override fun getParameterTypes(member: Constructor<*>): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Constructor<*>): Array<Class<*>> = member.exceptionTypes
    // #endregion
}