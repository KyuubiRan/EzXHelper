@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finder

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.finder.base.BaseMemberFinder
import io.github.kyuubiran.ezxhelper.core.`interface`.IFindSuper
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

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
            return FieldFinder(clazz.declaredFields.asSequence()).also { it.clazz = clazz }
        }

        @JvmStatic
        @JvmSynthetic
        fun fromClass(kclazz: KClass<*>): FieldFinder = fromClass(kclazz.java)

        @JvmStatic
        fun fromClass(clazzName: String, classLoader: ClassLoader = ClassLoaderProvider.safeClassLoader) =
            fromClass(Class.forName(clazzName, false, classLoader))

        @JvmStatic
        fun fromSequence(seq: Sequence<Field>): FieldFinder {
            return FieldFinder(seq)
        }

        @JvmStatic
        fun fromArray(array: Array<Field>): FieldFinder {
            return FieldFinder(array.asSequence())
        }

        @JvmStatic
        fun fromVararg(vararg array: Field): FieldFinder {
            return FieldFinder(array.asSequence())
        }

        @JvmStatic
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

    /**
     * Filter by field name
     *
     * 过滤出名字相同的 [Field]
     *
     * @param name The name of the field | 字段名称
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByName(name: String) = filter { this.name == name }

    /**
     * Filter by field type
     *
     * 过滤出具有指定类型的 [Field]
     *
     * @param type The type of the field | 字段类型
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByType(type: Class<*>) = filter { this.type == type }

    @JvmSynthetic
    fun filterByType(type: KClass<*>) = filter { this.type == type.java }

    // endregion

    // region filter modifiers

    /**
     * Filter if they are static
     *
     * 过滤出 static 的 [Field]
     *
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterStatic() = filter { Modifier.isStatic(this.modifiers) }

    /**
     * Filter if they are non-static
     *
     * 过滤出非 static 的 [Field]
     *
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterNonStatic() = filter { !Modifier.isStatic(this.modifiers) }

    /**
     * Filter if they are final
     *
     * 过滤出 final 的 [Field]
     *
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterFinal() = filter { Modifier.isFinal(this.modifiers) }

    /**
     * Filter if they are non-final
     *
     * 过滤出非 final 的 [Field]
     *
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterNonFinal() = filter { !Modifier.isFinal(this.modifiers) }


    // endregion

    // region overrides

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = makeNewFinder {
        var seq = sequence

        if (clazz == null || clazz == Any::class.java) return@makeNewFinder seq

        var c: Class<*>? = clazz?.superclass
        if (c == null || c == Any::class.java) return@makeNewFinder seq

        while (c != null && c != Any::class.java) {
            if (untilPredicate != null) {
                if (untilPredicate(c)) break
            }
            seq += c.declaredFields.asSequence()
            c = c.superclass
        }

        seq
    }

    override fun newFinder(sequence: Sequence<Field>): FieldFinder = FieldFinder(sequence)

    // endregion
}