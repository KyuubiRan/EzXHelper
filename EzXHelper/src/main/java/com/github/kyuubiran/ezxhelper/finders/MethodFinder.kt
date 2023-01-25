@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import com.github.kyuubiran.ezxhelper.interfaces.IFindSuper
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Helper for finding method(s) in the class or collection.
 */
class MethodFinder private constructor(seq: Sequence<Method>) : ExecutableFinder<Method, MethodFinder>(seq), IFindSuper<MethodFinder> {
    private var clazz: Class<*>? = null

    @Suppress("ClassName")
    companion object `-Static` {
        @JvmStatic
        fun fromClass(clazz: Class<*>): MethodFinder {
            var seq = emptySequence<Method>()
            seq += clazz.declaredMethods.asSequence()
            seq += clazz.interfaces.flatMap { c -> c.declaredMethods.asSequence() }
            return MethodFinder(seq).also { it.clazz = clazz }
        }

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
        @KotlinOnly
        fun Class<*>.methodFinder() = fromClass(this)

        @JvmSynthetic
        @KotlinOnly
        fun Array<Method>.methodFinder() = fromArray(this)

        @JvmSynthetic
        @KotlinOnly
        fun Iterable<Method>.methodFinder() = fromIterable(this)

        @JvmSynthetic
        @KotlinOnly
        fun Sequence<Method>.methodFinder() = fromSequence(this)
    }

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = applyThis {
        if (clazz == null || clazz == Any::class.java) return@applyThis

        var c = clazz?.superclass ?: return@applyThis

        while (c != Any::class.java) {
            if (untilPredicate?.invoke(c) == true) break

            sequence += c.declaredMethods.asSequence()
            sequence += c.interfaces.flatMap { i -> i.declaredMethods.asSequence() }

            c = c.superclass ?: return@applyThis
        }
    }

    // #region filter by
    /**
     * Filter by method name.
     * @param name method name
     * @return [MethodFinder] this finder
     */
    fun filterByName(name: String) = applyThis { sequence = sequence.filter { it.name == name } }

    /**
     * Filter by method return type.
     * @param returnType method return type
     * @return [MethodFinder] this finder
     */
    fun filterByReturnType(returnType: Class<*>) = applyThis { sequence = sequence.filter { it.returnType == returnType } }
    // #endregion

    // #region filter modifiers
    /**
     * Filter if they are static.
     * @return [FieldFinder] this finder.
     */
    fun filterStatic() = filterIncludeModifiers(Modifier.STATIC)

    /**
     * Filter if they are non-static.
     * @return [FieldFinder] this finder.
     */
    fun filterNonStatic() = filterExcludeModifiers(Modifier.STATIC)

    /**
     * Filter if they are final.
     * @return [FieldFinder] this finder.
     */
    fun filterFinal() = filterIncludeModifiers(Modifier.FINAL)

    /**
     * Filter if they are non-final.
     * @return [FieldFinder] this finder.
     */
    fun filterNonFinal() = filterExcludeModifiers(Modifier.FINAL)
    // #endregion

    // #region overrides
    override fun getParameterTypes(member: Method): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Method): Array<Class<*>> = member.exceptionTypes
    // #endregion
}