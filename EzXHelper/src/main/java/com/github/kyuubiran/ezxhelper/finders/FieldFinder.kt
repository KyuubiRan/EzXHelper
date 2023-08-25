@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import com.github.kyuubiran.ezxhelper.finders.base.BaseMemberFinder
import com.github.kyuubiran.ezxhelper.interfaces.IFindSuper
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Helper for finding field(s) in the class or collection.
 */
class FieldFinder private constructor(seq: Sequence<Field>) : BaseMemberFinder<Field, FieldFinder>(seq), IFindSuper<FieldFinder> {
    private var clazz: Class<*>? = null
    override val name: String
        get() = "FieldFinder"

    @Suppress("ClassName")
    companion object `-Static` {
        @JvmStatic
        fun fromClass(clazz: Class<*>): FieldFinder {
            return FieldFinder(clazz.declaredFields.asSequence()).also { it.clazz = clazz }.apply {
                exceptMessageScope { ctor(this@apply, "No such field found in class: ${clazz.name}") }
            }
        }

        @JvmStatic
        fun fromClass(clazzName: String, classLoader: ClassLoader = EzXHelper.classLoader) =
            fromClass(Class.forName(clazzName, false, classLoader))

        @JvmStatic
        fun fromSequence(seq: Sequence<Field>): FieldFinder {
            return FieldFinder(seq).apply {
                exceptMessageScope { ctor(this@apply, "No such field found in sequence(size=${seq.count()})") }
            }
        }

        @JvmStatic
        fun fromArray(array: Array<Field>): FieldFinder {
            return FieldFinder(array.asSequence()).apply {
                exceptMessageScope { ctor(this@apply, "No such field found in array(size=${array.count()})") }
            }
        }

        @JvmStatic
        fun fromVararg(vararg array: Field): FieldFinder {
            return FieldFinder(array.asSequence()).apply {
                exceptMessageScope { ctor(this@apply, "No such field found in vararg(size=${array.count()})") }
            }
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Field>): FieldFinder {
            return FieldFinder(iterable.asSequence()).apply {
                exceptMessageScope { ctor(this@apply, "No such field found in iterable(size=${iterable.count()})") }
            }
        }

        @JvmSynthetic
        @KotlinOnly
        fun Class<*>.fieldFinder() = fromClass(this)

        @JvmSynthetic
        @KotlinOnly
        fun Array<Field>.fieldFinder() = fromArray(this)

        @JvmSynthetic
        @KotlinOnly
        fun Iterable<Field>.fieldFinder() = fromIterable(this)

        @JvmSynthetic
        @KotlinOnly
        fun Sequence<Field>.fieldFinder() = fromSequence(this)
    }

    // region filter by

    /**
     * Filter by field name.
     * @param name The name of the field.
     * @return [FieldFinder] this finder.
     */
    fun filterByName(name: String) = applyThis {
        filter { this.name == name }
        exceptMessageScope { condition("filterByName($name)") }
    }

    /**
     * Filter by field type.
     * @param type The type of the field.
     * @return [FieldFinder] this finder.
     */
    fun filterByType(type: Class<*>) = applyThis {
        filter { this.type == type }
        exceptMessageScope { condition("filterByType(${type.name})") }
    }

    // endregion

    // region filter modifiers

    /**
     * Filter if they are static.
     * @return [FieldFinder] this finder.
     */
    fun filterStatic() = applyThis {
        filter { Modifier.isStatic(this.modifiers) }
        exceptMessageScope { condition("filterStatic") }
    }

    /**
     * Filter if they are non-static.
     * @return [FieldFinder] this finder.
     */
    fun filterNonStatic() = applyThis {
        filter { !Modifier.isStatic(this.modifiers) }
        exceptMessageScope { condition("filterNonStatic") }
    }

    /**
     * Filter if they are final.
     * @return [FieldFinder] this finder.
     */
    fun filterFinal() = applyThis {
        filter { Modifier.isFinal(this.modifiers) }
        exceptMessageScope { condition("filterFinal") }
    }

    /**
     * Filter if they are non-final.
     * @return [FieldFinder] this finder.
     */
    fun filterNonFinal() = applyThis {
        filter { !Modifier.isFinal(this.modifiers) }
        exceptMessageScope { condition("filterNonFinal") }
    }

    // endregion

    // region overrides

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = applyThis {
        if (clazz == null || clazz == Any::class.java) return@applyThis

        var c = clazz?.superclass ?: return@applyThis

        val ml = if (exceptionMessageEnabled) mutableListOf<String>() else null

        while (c != Any::class.java) {
            if (untilPredicate?.invoke(c) == true) break

            ml?.add(c.name)

            sequence += c.declaredFields.asSequence()
            c = c.superclass
        }

        if (ml != null) {
            exceptMessageScope { condition("findSuper(CustomCondition)") }
        }
    }

    // endregion
}