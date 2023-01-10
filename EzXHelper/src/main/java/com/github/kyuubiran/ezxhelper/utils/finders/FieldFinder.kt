@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FieldFinder private constructor(seq: Sequence<Field>) : BaseMemberFinder<Field, FieldFinder>(seq) {

    companion object {
        fun fromClass(clazz: Class<*>): FieldFinder = FieldFinder(clazz.declaredFields.asSequence())
        fun fromSequence(seq: Sequence<Field>): FieldFinder = FieldFinder(seq)
        fun fromArray(array: Array<Field>): FieldFinder = FieldFinder(array.asSequence())
        fun fromVararg(vararg array: Field): FieldFinder = FieldFinder(array.asSequence())
        fun fromIterable(iterable: Iterable<Field>): FieldFinder = FieldFinder(iterable.asSequence())
    }

    // #region filter by
    fun filterByName(name: String) = applyThis { memberSequence.filter { it.name == name } }
    fun filterByType(type: Class<*>) = applyThis { memberSequence.filter { it.type == type } }
    // #endregion

    // #region filter modifiers
    fun filterStatic() = filterIncludeModifiers(Modifier.STATIC)
    fun filterNonStatic() = filterExcludeModifiers(Modifier.STATIC)
    // #endregion
}