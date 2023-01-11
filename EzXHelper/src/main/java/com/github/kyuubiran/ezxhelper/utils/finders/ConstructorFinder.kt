@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import java.lang.reflect.Constructor

class ConstructorFinder private constructor(seq: Sequence<Constructor<*>>) : ExecutableFinder<Constructor<*>, ConstructorFinder>(seq) {

    @Suppress("ClassName")
    companion object `-Static` {
        @JvmStatic
        fun fromClass(clazz: Class<*>): ConstructorFinder = ConstructorFinder(clazz.declaredConstructors.asSequence())

        @JvmStatic
        fun fromSequence(seq: Sequence<Constructor<*>>): ConstructorFinder = ConstructorFinder(seq)

        @JvmStatic
        fun fromArray(array: Array<Constructor<*>>): ConstructorFinder = ConstructorFinder(array.asSequence())

        @JvmStatic
        fun fromVararg(vararg array: Constructor<*>): ConstructorFinder = ConstructorFinder(array.asSequence())

        @JvmStatic
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