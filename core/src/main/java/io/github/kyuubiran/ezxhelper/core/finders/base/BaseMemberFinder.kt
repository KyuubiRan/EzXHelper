@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.kyuubiran.ezxhelper.core.finders.base

import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isNotPackagePrivate
import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isPackagePrivate
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Member
import java.lang.reflect.Modifier

abstract class BaseMemberFinder<T, Self>(memberSequence: Sequence<T>) : BaseFinder<T, Self>(memberSequence) where T : Member {

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
     * Filter by the same modifiers.
     * @param modifiers the modifiers
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(modifiers: Int): Self = filter { this.modifiers == modifiers }

    /**
     * Use condition to filter by the modifiers.
     * @param predicate the condition
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): Self = filter { predicate(modifiers) }

    /**
     * Filter include the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterIncludeModifiers(modifiers: Int): Self = filter { (this.modifiers and modifiers) != 0 }

    /**
     * Filter exclude the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterExcludeModifiers(modifiers: Int): Self = filter { (this.modifiers and modifiers) == 0 }

    /**
     * Filter if they are public.
     * @return [Self] the filtered finder
     */
    fun filterPublic() = sequence.filter { Modifier.isPublic(it.modifiers) }

    /**
     * Filter if they are non-public.
     * @return [Self] the filtered finder
     */
    fun filterNonPublic() = filter { !Modifier.isPublic(modifiers) }

    /**
     * Filter if they are protected.
     * @return [Self] the filtered finder
     */
    fun filterProtected() = filter { Modifier.isProtected(modifiers) }

    /**
     * Filter if they are non-protected.
     * @return [Self] the filtered finder
     */
    fun filterNonProtected() = filter { !Modifier.isProtected(modifiers) }

    /**
     * Filter if they are private.
     * @return [Self] the filtered finder
     */
    fun filterPrivate() = filter { Modifier.isPrivate(modifiers) }

    /**
     * Filter if they are non-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPrivate() = filter { !Modifier.isPrivate(modifiers) }

    /**
     * Filter if they are package-private.
     * @return [Self] the filtered finder
     */
    fun filterPackagePrivate() = filter { isPackagePrivate }

    /**
     * Filter if they are non-package-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPackagePrivate() = filter { isNotPackagePrivate }

    // endregion

    protected fun allowAccess(member: Member) {
        if (member !is AccessibleObject) return
        member.runCatching { isAccessible = true }
    }
}