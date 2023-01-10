@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import java.lang.reflect.Method
import java.lang.reflect.Modifier

class MethodFinder private constructor(seq: Sequence<Method>) : ExecutableFinder<Method, MethodFinder>(seq) {
    private var clazz: Class<*>? = null

    companion object {
        fun fromClass(clazz: Class<*>): MethodFinder {
            var seq = emptySequence<Method>()
            seq += clazz.declaredMethods.asSequence()
            seq += clazz.interfaces.flatMap { c -> c.declaredMethods.asSequence() }
            return MethodFinder(seq).also { it.clazz = clazz }
        }

        fun fromSequence(seq: Sequence<Method>): MethodFinder {
            return MethodFinder(seq)
        }

        fun fromArray(array: Array<Method>): MethodFinder {
            return MethodFinder(array.asSequence())
        }

        fun fromVararg(vararg array: Method): MethodFinder {
            return MethodFinder(array.asSequence())
        }

        fun fromIterable(iterable: Iterable<Method>): MethodFinder {
            return MethodFinder(iterable.asSequence())
        }
    }

    fun findSuper(findSuper: (Class<*>.() -> Boolean)? = null) = applyThis {
        if (clazz == null || clazz == Any::class.java) return@applyThis

        var c = clazz?.superclass ?: return@applyThis
        var seq = emptySequence<Method>()

        while (c != Any::class.java) {
            findSuper?.invoke(c)?.let {
                if (it) return@applyThis
            }

            seq += c.declaredMethods.asSequence()
            seq += c.interfaces.flatMap { i -> i.declaredMethods.asSequence() }

            c = c.superclass ?: return@applyThis
        }

        this += seq
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