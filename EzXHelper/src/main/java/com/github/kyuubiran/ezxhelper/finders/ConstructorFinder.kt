@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import java.lang.reflect.Constructor

/**
 * Helper for finding constructor(s) in the class or collection.
 */
class ConstructorFinder private constructor(seq: Sequence<Constructor<*>>) : ExecutableFinder<Constructor<*>, ConstructorFinder>(seq) {
    @Suppress("ClassName")
    companion object `-Static` {
        @JvmStatic
        fun fromClass(clazz: Class<*>): ConstructorFinder {
            return ConstructorFinder(clazz.declaredConstructors.asSequence())
        }

        @JvmStatic
        fun fromSequence(seq: Sequence<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(seq)
        }

        @JvmStatic
        fun fromArray(array: Array<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(array.asSequence())
        }

        @JvmStatic
        fun fromVararg(vararg array: Constructor<*>): ConstructorFinder {
            return ConstructorFinder(array.asSequence())
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(iterable.asSequence())
        }

        @JvmSynthetic
        @KotlinOnly
        fun Class<*>.constructorFinder() = fromClass(this)

        @JvmSynthetic
        @KotlinOnly
        fun Array<Constructor<*>>.constructorFinder() = fromArray(this)

        @JvmSynthetic
        @KotlinOnly
        fun Iterable<Constructor<*>>.constructorFinder() = fromIterable(this)

        @JvmSynthetic
        @KotlinOnly
        fun Sequence<Constructor<*>>.constructorFinder() = fromSequence(this)
    }

    // region filter by
    // endregion

    // region filter modifiers
    // endregion

    // region overrides
    override fun getParameterTypes(member: Constructor<*>): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Constructor<*>): Array<Class<*>> = member.exceptionTypes
    // endregion
}