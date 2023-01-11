@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.utils.interfaces.IFindSuper
import java.lang.reflect.Method
import java.lang.reflect.Modifier

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
        fun Class<*>.methodFinder() = fromClass(this)

        @JvmSynthetic
        fun Array<Method>.methodFinder() = fromArray(this)

        @JvmSynthetic
        fun Iterable<Method>.methodFinder() = fromIterable(this)

        @JvmSynthetic
        fun Sequence<Method>.methodFinder() = fromSequence(this)
    }

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = applyThis {
        if (clazz == null || clazz == Any::class.java) return@applyThis

        var c = clazz?.superclass ?: return@applyThis

        while (c != Any::class.java) {
            untilPredicate?.invoke(c)?.let {
                if (it) return@applyThis
            }

            memberSequence += c.declaredMethods.asSequence()
            memberSequence += c.interfaces.flatMap { i -> i.declaredMethods.asSequence() }

            c = c.superclass ?: return@applyThis
        }
    }

    // #region filter by
    fun filterByName(name: String) = applyThis { memberSequence.filter { it.name == name } }
    fun filterByReturnType(returnType: Class<*>) = applyThis { memberSequence.filter { it.returnType == returnType } }
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