@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finders

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.finders.base.ExecutableFinder
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

/**
 * Helper for finding constructor(s) in the class or collection.
 */
class ConstructorFinder private constructor(seq: Sequence<Constructor<*>>) : ExecutableFinder<Constructor<*>, ConstructorFinder>(seq) {

    override val name: String
        get() = "ConstructorFinder"

    @Suppress("ClassName")
    companion object `-Static` {

        @JvmStatic
        fun fromClass(clazz: Class<*>): ConstructorFinder {
            return ConstructorFinder(clazz.declaredConstructors.asSequence())
        }

        @JvmStatic
        @JvmSynthetic
        fun fromClass(kclazz: KClass<*>): ConstructorFinder = fromClass(kclazz.java)

        @JvmStatic
        fun fromSequence(seq: Sequence<Constructor<*>>): ConstructorFinder {
            return ConstructorFinder(seq)
        }

        @JvmStatic
        fun fromClass(clazzName: String, classLoader: ClassLoader = ClassLoaderProvider.safeClassLoader) =
            fromClass(Class.forName(clazzName, false, classLoader))


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
        fun Class<*>.constructorFinder() = fromClass(this)

        @JvmSynthetic
        fun Array<Constructor<*>>.constructorFinder() = fromArray(this)

        @JvmSynthetic
        fun Iterable<Constructor<*>>.constructorFinder() = fromIterable(this)

        @JvmSynthetic
        fun Sequence<Constructor<*>>.constructorFinder() = fromSequence(this)
    }

    // region overrides

    override fun getParameterTypes(member: Constructor<*>): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Constructor<*>): Array<Class<*>> = member.exceptionTypes
    override fun newFinder(sequence: Sequence<Constructor<*>>): ConstructorFinder = ConstructorFinder(sequence)

    // endregion
}