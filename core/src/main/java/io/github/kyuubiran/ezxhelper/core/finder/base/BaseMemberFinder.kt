@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.kyuubiran.ezxhelper.core.finder.base

import io.github.kyuubiran.ezxhelper.core.extension.MemberExtension.isNotPackagePrivate
import io.github.kyuubiran.ezxhelper.core.extension.MemberExtension.isPackagePrivate
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Member
import java.lang.reflect.Modifier

abstract class BaseMemberFinder<T, Finder>(memberSequence: Sequence<T>) : BaseFinder<T, Finder>(memberSequence) where T : Member {

    // region get elem
    final override fun first(): T = super.first().also { allowAccess(it) }
    final override fun firstOrNull(): T? = super.firstOrNull()?.also { allowAccess(it) }
    final override fun last(): T = super.last().also { allowAccess(it) }
    final override fun lastOrNull(): T? = super.lastOrNull()?.also { allowAccess(it) }
    final override fun first(condition: T.() -> Boolean): T = super.first(condition).also { allowAccess(it) }
    final override fun firstOrNull(condition: T.() -> Boolean): T? = super.firstOrNull(condition)?.also { allowAccess(it) }
    final override fun last(condition: T.() -> Boolean): T = super.last(condition).also { allowAccess(it) }
    final override fun lastOrNull(condition: T.() -> Boolean): T? = super.lastOrNull(condition)?.also { allowAccess(it) }
    final override fun single(): T = super.single().also { allowAccess(it) }
    final override fun singleOrNull(): T? = super.singleOrNull()?.also { allowAccess(it) }
    final override fun single(condition: T.() -> Boolean): T = super.single(condition).also { allowAccess(it) }
    final override fun singleOrNull(condition: T.() -> Boolean): T? = super.singleOrNull(condition)?.also { allowAccess(it) }
    // endregion

    // region filter modifiers
    /**
     * Filter by the same modifiers
     *
     * 过滤出具有相同修饰符的 [Member]
     *
     * @param modifiers the modifiers | 修饰符
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterByModifiers(modifiers: Int): Finder = filter { this.modifiers == modifiers }

    /**
     * Use condition to filter by the modifiers
     *
     * 过滤出满足条件的修饰符的 [Member]
     *
     * @param predicate the condition | 条件
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): Finder = filter { predicate(modifiers) }

    /**
     * Filter include the modifiers
     *
     * 过滤出包含指定修饰符的 [Member]
     *
     * @param modifiers the modifiers. | 修饰符
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterIncludeModifiers(modifiers: Int): Finder = filter { (this.modifiers and modifiers) != 0 }

    /**
     * Filter exclude the modifiers
     *
     * 过滤出不包含指定修饰符的 [Member]
     *
     * @param modifiers the modifiers. | 修饰符
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterExcludeModifiers(modifiers: Int): Finder = filter { (this.modifiers and modifiers) == 0 }

    /**
     * Filter if they are public
     *
     * 过滤出带有 public 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterPublic() = sequence.filter { Modifier.isPublic(it.modifiers) }

    /**
     * Filter if they are non-public
     *
     * 过滤出不带有 public 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterNonPublic() = filter { !Modifier.isPublic(modifiers) }

    /**
     * Filter if they are protected
     *
     * 过滤出带有 protected 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterProtected() = filter { Modifier.isProtected(modifiers) }

    /**
     * Filter if they are non-protected
     *
     * 过滤出不带有 protected 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterNonProtected() = filter { !Modifier.isProtected(modifiers) }

    /**
     * Filter if they are private
     *
     * 过滤出带有 private 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterPrivate() = filter { Modifier.isPrivate(modifiers) }

    /**
     * Filter if they are non-private
     *
     * 过滤出不带有 private 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterNonPrivate() = filter { !Modifier.isPrivate(modifiers) }

    /**
     * Filter if they are package-private
     *
     * 过滤出带有 package-private 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterPackagePrivate() = filter { isPackagePrivate }

    /**
     * Filter if they are non-package-private
     *
     * 过滤出不带有 package-private 修饰符的 [Member]
     *
     * @return [Finder] the filtered finder | 过滤后的 [Finder]
     */
    fun filterNonPackagePrivate() = filter { isNotPackagePrivate }

    // endregion

    /**
     * Allow access to the member.
     *
     * 允许访问成员
     */
    protected fun allowAccess(member: Member) {
        if (member !is AccessibleObject) return
        member.runCatching { isAccessible = true }
    }
}