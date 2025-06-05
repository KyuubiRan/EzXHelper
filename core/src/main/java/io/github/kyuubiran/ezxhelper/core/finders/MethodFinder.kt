@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finders

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.finders.base.ExecutableFinder
import io.github.kyuubiran.ezxhelper.core.interfaces.IFindSuper
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Helper for finding method(s) in the class or collection.
 */
class MethodFinder private constructor(seq: Sequence<Method>) : ExecutableFinder<Method, MethodFinder>(seq), IFindSuper<MethodFinder> {

    private var clazz: Class<*>? = null

    override val name: String
        get() = "MethodFinder"

    @Suppress("ClassName")
    companion object `-Static` {

        @JvmStatic
        fun fromClass(clazz: Class<*>): MethodFinder {
            var seq = emptySequence<Method>()
            seq += clazz.declaredMethods.asSequence()
            seq += clazz.interfaces.flatMap { c -> c.declaredMethods.asSequence() }
            return MethodFinder(seq).apply {
                this.clazz = clazz
            }
        }

        @JvmStatic
        @JvmSynthetic
        fun fromClass(kclazz: KClass<*>): MethodFinder = fromClass(kclazz.java)

        @JvmStatic
        fun fromClass(clazzName: String, classLoader: ClassLoader = ClassLoaderProvider.safeClassLoader) =
            fromClass(Class.forName(clazzName, false, classLoader))

        @JvmStatic
        fun fromSequence(seq: Sequence<Method>): MethodFinder {
            return MethodFinder(seq)
        }

        @JvmStatic
        fun fromArray(array: Array<Method>): MethodFinder {
            return MethodFinder(array.asSequence())
        }

        @JvmStatic
        fun fromVararg(vararg array: Method): MethodFinder {
            return MethodFinder(array.asSequence())
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Method>): MethodFinder {
            return MethodFinder(iterable.asSequence())
        }

        @JvmSynthetic
        fun Class<*>.methodFinder() = fromClass(this)

        @JvmSynthetic
        fun Array<Method>.methodFinder() = fromArray(this)

        @JvmSynthetic
        fun Iterable<Method>.methodFinder() = fromIterable(this)

        @JvmSynthetic
        fun Sequence<Method>.methodFinder() = fromSequence(this)
    }

    // region filter by

    /**
     * Filter by method name.
     * @param name method name
     * @return [MethodFinder] this finder
     */
    fun filterByName(name: String) = filter { this.name == name }


    /**
     * Filter by method return type.
     * @param returnType method return type
     * @return [MethodFinder] this finder
     */
    fun filterByReturnType(returnType: Class<*>) = filter { this.returnType == returnType }

    fun filterByReturnType(returnType: KClass<*>) = filterByReturnType(returnType.java)

    /**
     * Filter by method returns void type.
     * @return [MethodFinder] this finder
     */
    fun filterVoidReturnType() = filterByReturnType(Void.TYPE)

    /**
     * Filter by method assignable return type.
     * @param returnType method return type
     * @return [MethodFinder] this finder
     */
    fun filterByAssignableReturnType(returnType: Class<*>) = filter { this.returnType.isAssignableFrom(returnType) || returnType.isAssignableFrom(this.returnType) }

// endregion

// region filter modifiers
    /**
     * Filter if they are abstract.
     * @return [MethodFinder] this finder.
     */
    fun filterAbstract() = filter { Modifier.isAbstract(this.modifiers) }


    /**
     * Filter if they are non-abstract.
     * @return [MethodFinder] this finder.
     */
    fun filterNonAbstract() = filter { !Modifier.isAbstract(this.modifiers) }

    /**
     * Filter if they are static.
     * @return [MethodFinder] this finder.
     */
    fun filterStatic() = filter { Modifier.isStatic(this.modifiers) }

    /**
     * Filter if they are non-static.
     * @return [MethodFinder] this finder.
     */
    fun filterNonStatic() = filter { !Modifier.isStatic(this.modifiers) }

    /**
     * Filter if they are final.
     * @return [MethodFinder] this finder.
     */
    fun filterFinal() = filter { Modifier.isFinal(this.modifiers) }

    /**
     * Filter if they are non-final.
     * @return [MethodFinder] this finder.
     */
    fun filterNonFinal() = filter { !Modifier.isFinal(this.modifiers) }

// endregion

// region overrides

    override fun getParameterTypes(member: Method): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Method): Array<Class<*>> = member.exceptionTypes

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = makeNewFinder {
        var seq = sequence

        if (clazz == null) return@makeNewFinder seq

        var c: Class<*>? = clazz?.superclass ?: return@makeNewFinder seq

        while (c != null) {
            if (untilPredicate != null) {
                if (untilPredicate(c)) break
            }

            seq += c.declaredMethods.asSequence()
            seq += c.interfaces.flatMap { i -> i.declaredMethods.asSequence() }

            c = c.superclass
        }

        seq
    }

    override fun newFinder(sequence: Sequence<Method>): MethodFinder = MethodFinder(sequence)

// endregion
}