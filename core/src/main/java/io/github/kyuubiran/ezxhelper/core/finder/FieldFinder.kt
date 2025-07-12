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
     * 通过名字过滤字段 [Field]
     *
     * @param name The name of the field | 字段名称
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByName(name: String, ignoreCase: Boolean = false) = filter { this.name.equals(name, ignoreCase = ignoreCase) }

    /**
     * Filter by field name contains a specific string
     *
     * 通过名字包含指定字符串过滤字段 [Field]
     *
     * @param name The string to check if the field name contains | 要检查字段名称是否包含的字符串
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByNameContains(name: String, ignoreCase: Boolean = false): FieldFinder =
        filter { this.name.contains(name, ignoreCase = ignoreCase) }

    /**
     * Filter by field name starts with a specific string
     *
     * 通过名字以指定字符串开头过滤字段 [Field]
     *
     * @param name The string to check if the field name starts with | 要检查字段名称是否以指定字符串开头
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByNameStartsWith(name: String, ignoreCase: Boolean = false): FieldFinder =
        filter { this.name.startsWith(name, ignoreCase = ignoreCase) }

    /**
     * Filter by field name ends with a specific string
     *
     * 通过名字以指定字符串结尾过滤字段 [Field]
     *
     * @param name The string to check if the field name ends with | 要检查字段名称是否以指定字符串结尾
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByNameEndsWith(name: String, ignoreCase: Boolean = false): FieldFinder =
        filter { this.name.endsWith(name, ignoreCase = ignoreCase) }

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

    /**
     * Filter by field type extends from a specific class
     *
     * 过滤出返回类型继承自指定类的 [Field]
     *
     * e.g.
     * ```kotlin
     * val i: Int = 42
     * val f : Float = 42.0f
     * ```
     * will be filtered by | 可以通过以下方式过滤
     * ```kotlin
     * val fields = finder.filterByTypeExtendsFrom(Number::class.java).toList()
     * ```
     *
     * @param superClass Class to check if the field type extends from | 要检查返回类型是否继承自的类
     * @return [FieldFinder] new finder | 过滤后的 [FieldFinder]
     */
    fun filterByTypeExtendsFrom(superClass: Class<*>) = filter { superClass.isAssignableFrom(this.type) }

    @JvmSynthetic
    fun filterByTypeExtendsFrom(superClass: KClass<*>) = filterByTypeExtendsFrom(superClass.java)

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

    // region first by

    /**
     * Find the first field by name
     *
     * 通过名字查找第一个匹配的 [Field]
     *
     * @param name The name of the field | 字段名称
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field] the first matching field | 第一个匹配的 [Field]
     * @throws NoSuchElementException if no field is found | 如果没有找到匹配的 [Field]，则抛出异常
     */
    @Throws(NoSuchElementException::class)
    fun firstByName(name: String, ignoreCase: Boolean = false): Field = first { this.name.equals(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name or return null if not found
     *
     * 通过名字查找第一个匹配的 [Field]，如果没有找到则返回 null
     *
     * @param name The name of the field | 字段名称
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field]? the first matching field or null if not found | 第一个匹配的 [Field] 或 null
     */
    fun firstOrNullByName(name: String, ignoreCase: Boolean = false): Field? = firstOrNull { this.name.equals(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name contains a specific string
     *
     * 通过名字包含指定字符串查找第一个匹配的 [Field]
     *
     * @param name The string to check if the field name contains | 要检查字段名称是否包含的字符串
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field] the first matching field | 第一个匹配的 [Field]
     * @throws NoSuchElementException if no field is found | 如果没有找到匹配的 [Field]，则抛出异常
     */
    @Throws(NoSuchElementException::class)
    fun firstByNameContains(name: String, ignoreCase: Boolean = false): Field = first { this.name.contains(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name contains a specific string or return null if not found
     *
     * 通过名字包含指定字符串查找第一个匹配的 [Field]，如果没有找到则返回 null
     *
     * @param name The string to check if the field name contains | 要检查字段名称是否包含的字符串
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field]? the first matching field or null if not found | 第一个匹配的 [Field] 或 null
     */
    fun firstOrNullByNameContains(name: String, ignoreCase: Boolean = false): Field? =
        firstOrNull { this.name.contains(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name starts with a specific string
     *
     * 通过名字以指定字符串开头查找第一个匹配的 [Field]
     *
     * @param name The string to check if the field name starts with | 要检查字段名称是否以指定字符串开头
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field] the first matching field | 第一个匹配的 [Field]
     * @throws NoSuchElementException if no field is found | 如果没有找到匹配的 [Field]，则抛出异常
     */
    @Throws(NoSuchElementException::class)
    fun firstByNameStartsWith(name: String, ignoreCase: Boolean = false): Field =
        first { this.name.startsWith(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name starts with a specific string or return null if not found
     *
     * 通过名字以指定字符串开头查找第一个匹配的 [Field]，如果没有找到则返回 null
     *
     * @param name The string to check if the field name starts with | 要检查字段名称是否以指定字符串开头
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field]? the first matching field or null if not found | 第一个匹配的 [Field] 或 null
     */
    fun firstOrNullByNameStartsWith(name: String, ignoreCase: Boolean = false): Field? =
        firstOrNull { this.name.startsWith(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name ends with a specific string
     *
     * 通过名字以指定字符串结尾查找第一个匹配的 [Field]
     *
     * @param name The string to check if the field name ends with | 要检查字段名称是否以指定字符串结尾
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field] the first matching field | 第一个匹配的 [Field]
     * @throws NoSuchElementException if no field is found | 如果没有找到匹配的 [Field]，则抛出异常
     */
    @Throws(NoSuchElementException::class)
    fun firstByNameEndsWith(name: String, ignoreCase: Boolean = false): Field =
        first { this.name.endsWith(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by name ends with a specific string or return null if not found
     *
     * 通过名字以指定字符串结尾查找第一个匹配的 [Field]，如果没有找到则返回 null
     *
     * @param name The string to check if the field name ends with | 要检查字段名称是否以指定字符串结尾
     * @param ignoreCase Whether to ignore case when checking | 是否忽略大小写
     * @return [Field]? the first matching field or null if not found | 第一个匹配的 [Field] 或 null
     */
    fun firstOrNullByNameEndsWith(name: String, ignoreCase: Boolean = false): Field? =
        firstOrNull { this.name.endsWith(name, ignoreCase = ignoreCase) }

    /**
     * Find the first field by type
     *
     * 通过类型查找第一个匹配的 [Field]
     *
     * @param type The type of the field | 字段类型
     * @return [Field] the first matching field | 第一个匹配的 [Field]
     * @throws NoSuchElementException if no field is found | 如果没有找到匹配的 [Field]，则抛出异常
     */
    @Throws(NoSuchElementException::class)
    fun firstByType(type: Class<*>): Field = first { this.type == type }

    @JvmSynthetic
    fun firstByType(type: KClass<*>): Field = first { this.type == type.java }

    /**
     * Find the first field by type or return null if not found
     *
     * 通过类型查找第一个匹配的 [Field]，如果没有找到则返回 null
     *
     * @param type The type of the field | 字段类型
     * @return [Field]? the first matching field or null if not found | 第一个匹配的 [Field] 或 null
     */
    fun firstOrNullByType(type: Class<*>): Field? = firstOrNull { this.type == type }

    @JvmSynthetic
    fun firstOrNullByType(type: KClass<*>): Field? = firstOrNull { this.type == type.java }

    /**
     * Find the first field by type extends from a specific class
     *
     * 通过类型继承自指定类查找第一个匹配的 [Field]
     *
     * @param superClass Class to check if
     * the field type extends from | 要检查返回类型是否继承自的类
     * @return [Field] the first matching field | 第一个匹配的 [Field]
     * @throws NoSuchElementException if no field is found | 如果没有找到匹配的 [Field]，则抛出异常
     */
    @Throws(NoSuchElementException::class)
    fun firstByTypeExtendsFrom(superClass: Class<*>): Field =
        first { superClass == this.type || superClass.isAssignableFrom(this.type) }

    @JvmSynthetic
    fun firstByTypeExtendsFrom(superClass: KClass<*>): Field =
        first { superClass.java == this.type || superClass.java.isAssignableFrom(this.type) }

    /**
     * Find the first field by type extends from a specific class or return null if not found
     *
     * 通过类型继承自指定类查找第一个匹配的 [Field]，如果没有找到则返回 null
     *
     * @param superClass Class to check if
     * the field type extends from | 要检查返回类型是否继承自的类
     * @return [Field]? the first matching field or null if not found | 第一个匹配的 [Field] 或 null
     */
    fun firstOrNullByTypeExtendsFrom(superClass: Class<*>): Field? =
        firstOrNull { superClass == this.type || superClass.isAssignableFrom(this.type) }

    @JvmSynthetic
    fun firstOrNullByTypeExtendsFrom(superClass: KClass<*>): Field? =
        firstOrNull { superClass.java == this.type || superClass.java.isAssignableFrom(this.type) }

    // endregion

    // region overrides

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = makeNewFinder {
        var seq = sequence

        if (clazz == null || clazz == Any::class.java) return@makeNewFinder seq

        var c: Class<*>? = clazz?.superclass
        if (c == null || c == Any::class.java) return@makeNewFinder seq

        while (c != null && c != Any::class.java) {
            seq += c.declaredFields.asSequence()
            c = c.superclass

            if (untilPredicate != null) {
                if (untilPredicate(c)) break
            }
        }

        seq
    }

    override fun newFinder(sequence: Sequence<Field>): FieldFinder = FieldFinder(sequence)

    // endregion
}