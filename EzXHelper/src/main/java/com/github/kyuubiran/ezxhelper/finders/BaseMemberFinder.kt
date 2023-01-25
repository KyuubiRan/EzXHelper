@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.Log.logeIfThrow
import com.github.kyuubiran.ezxhelper.MemberExtensions
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Member
import java.lang.reflect.Modifier


abstract class BaseMemberFinder<T, Self> constructor(memberSequence: Sequence<T>) : BaseFinder<T, Self>(memberSequence) where T : Member {
    // region get elem

    final override fun first(): T = super.first().also { allowAccess(it) }
    final override fun firstOrNull(): T? = super.firstOrNull()?.also { allowAccess(it) }
    final override fun last(): T = super.last().also { allowAccess(it) }
    final override fun lastOrNull(): T? = super.lastOrNull()?.also { allowAccess(it) }
    final override fun first(condition: T.() -> Boolean): T = super.first(condition).also { allowAccess(it) }
    final override fun firstOrNull(condition: T.() -> Boolean): T? = super.firstOrNull(condition)?.also { allowAccess(it) }
    final override fun last(condition: T.() -> Boolean): T = super.last(condition).also { allowAccess(it) }
    final override fun lastOrNull(condition: T.() -> Boolean): T? = super.lastOrNull(condition)?.also { allowAccess(it) }

    // endregion

    // region filter modifiers
    /**
     * Filter by the same modifiers.
     * @param modifiers the modifiers
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(modifiers: Int): Self = applyThis {
        sequence = sequence.filter { it.modifiers == modifiers }
    }

    /**
     * Use condition to filter by the modifiers.
     * @param predicate the condition
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): Self = applyThis {
        sequence = sequence.filter { predicate(it.modifiers) }
    }

    /**
     * Filter include the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterIncludeModifiers(modifiers: Int): Self = applyThis {
        sequence = sequence.filter { (it.modifiers and modifiers) != 0 }
    }

    /**
     * Filter exclude the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterExcludeModifiers(modifiers: Int): Self = applyThis {
        sequence = sequence.filter { (it.modifiers and modifiers) == 0 }
    }

    /**
     * Filter if they are public.
     * @return [Self] the filtered finder
     */
    fun filterPublic() = filterIncludeModifiers(Modifier.PUBLIC)

    /**
     * Filter if they are non-public.
     * @return [Self] the filtered finder
     */
    fun filterNonPublic() = filterExcludeModifiers(Modifier.PUBLIC)

    /**
     * Filter if they are protected.
     * @return [Self] the filtered finder
     */
    fun filterProtected() = filterIncludeModifiers(Modifier.PROTECTED)

    /**
     * Filter if they are non-protected.
     * @return [Self] the filtered finder
     */
    fun filterNonProtected() = filterExcludeModifiers(Modifier.PROTECTED)

    /**
     * Filter if they are private.
     * @return [Self] the filtered finder
     */
    fun filterPrivate() = filterIncludeModifiers(Modifier.PRIVATE)

    /**
     * Filter if they are non-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPrivate() = filterExcludeModifiers(Modifier.PRIVATE)

    /**
     * Filter if they are package-private.
     * @return [Self] the filtered finder
     */
    fun filterPackagePrivate() = filterExcludeModifiers(MemberExtensions.PACKAGE_PRIVATE)

    /**
     * Filter if they are non-package-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPackagePrivate() = filterIncludeModifiers(MemberExtensions.PACKAGE_PRIVATE)
    // endregion

    protected fun allowAccess(member: Member) {
        (member as? AccessibleObject)?.runCatching { isAccessible = true }?.logeIfThrow("Cannot set accessible to ${member.name}")
    }
}