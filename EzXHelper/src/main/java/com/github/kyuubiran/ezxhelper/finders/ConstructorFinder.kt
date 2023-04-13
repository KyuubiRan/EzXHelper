@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import com.github.kyuubiran.ezxhelper.finders.base.ExecutableFinder
import java.lang.reflect.Constructor

/**
 * Helper for finding constructor(s) in the class or collection.
 */
class ConstructorFinder private constructor(seq: Sequence<Constructor<*>>) : ExecutableFinder<Constructor<*>, ConstructorFinder>(seq) {
    @Suppress("ClassName")
    companion object `-Static` {
        @JvmStatic
        fun fromClass(clazz: Class<*>): ConstructorFinder {
            return ConstructorFinder(clazz.declaredConstructors.asSequence()).apply {
                exceptMessageScope { ctor<ConstructorFinder>("No such constructor found in class: ${clazz.name}") }
            }
        }

        @JvmStatic
        fun fromSequence(seq: Sequence<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(seq).apply {
                exceptMessageScope { ctor<ConstructorFinder>("No such constructor found in sequence(size=${seq.count()})") }
            }
        }

        @JvmStatic
        fun fromClass(clazzName: String, classLoader: ClassLoader = EzXHelper.classLoader) =
            fromClass(Class.forName(clazzName, false, classLoader))


        @JvmStatic
        fun fromArray(array: Array<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(array.asSequence()).apply {
                exceptMessageScope { ctor<ConstructorFinder>("No such constructor found in array(size=${array.count()})") }
            }
        }

        @JvmStatic
        fun fromVararg(vararg array: Constructor<*>): ConstructorFinder {
            return ConstructorFinder(array.asSequence()).apply {
                exceptMessageScope { ctor<ConstructorFinder>("No such constructor found in array(size=${array.count()})") }
            }
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(iterable.asSequence()).apply {
                exceptMessageScope { ctor<ConstructorFinder>("No such constructor found in iterable)") }
            }
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

    // region overrides
    override fun getParameterTypes(member: Constructor<*>): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Constructor<*>): Array<Class<*>> = member.exceptionTypes
    // endregion
}