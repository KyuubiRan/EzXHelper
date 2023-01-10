@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope
import java.lang.reflect.Field
import java.lang.reflect.Modifier

context (IXposedScope)
class FieldFinder private constructor(seq: Sequence<Field>) : BaseMemberFinder<Field, FieldFinder>(seq) {
    // #region filter by
    fun filterByName(name: String) = applyThis { memberSequence.filter { it.name == name } }
    // #endregion

    // #region filter modifiers
    fun filterStatic() = filterIncludeModifiers(Modifier.STATIC)
    fun filterNonStatic() = filterExcludeModifiers(Modifier.STATIC)
    // #endregion
}