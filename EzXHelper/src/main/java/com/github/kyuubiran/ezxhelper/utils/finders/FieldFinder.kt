@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.utils.interfaces.IFindSuper
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FieldFinder private constructor(seq: Sequence<Field>) : BaseMemberFinder<Field, FieldFinder>(seq), IFindSuper<FieldFinder> {
    private var clazz: Class<*>? = null

    @Suppress("ClassName")
    companion object `-Static` {
        fun fromClass(clazz: Class<*>): FieldFinder {
            return FieldFinder(clazz.declaredFields.asSequence()).also { it.clazz = clazz }
        }

        fun fromSequence(seq: Sequence<Field>): FieldFinder {
            return FieldFinder(seq)
        }

        fun fromArray(array: Array<Field>): FieldFinder {
            return FieldFinder(array.asSequence())
        }

        fun fromVararg(vararg array: Field): FieldFinder {
            return FieldFinder(array.asSequence())
        }

        fun fromIterable(iterable: Iterable<Field>): FieldFinder {
            return FieldFinder(iterable.asSequence())
        }

        @JvmSynthetic
        fun Class<*>.fieldFinder() = fromClass(this)

        @JvmSynthetic
        fun Array<Field>.fieldFinder() = fromArray(this)

        @JvmSynthetic
        fun Iterable<Field>.fieldFinder() = fromIterable(this)

        @JvmSynthetic
        fun Sequence<Field>.fieldFinder() = fromSequence(this)
    }

    // region filter by
    fun filterByName(name: String) = applyThis { memberSequence.filter { it.name == name } }
    fun filterByType(type: Class<*>) = applyThis { memberSequence.filter { it.type == type } }
    // endregion

    // region filter modifiers
    fun filterStatic() = filterIncludeModifiers(Modifier.STATIC)
    fun filterNonStatic() = filterExcludeModifiers(Modifier.STATIC)
    // endregion

    // region overrides
    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = applyThis {
        if (clazz == null || clazz == Any::class.java) return@applyThis

        var c = clazz?.superclass ?: return@applyThis

        while (c != Any::class.java) {
            memberSequence += c.declaredFields.asSequence()
            c = c.superclass
        }
    }
    // endregion
}