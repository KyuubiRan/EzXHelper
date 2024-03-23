@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.finders.base

import com.github.kyuubiran.ezxhelper.LogExtensions.logeIfThrow
import com.github.kyuubiran.ezxhelper.MemberExtensions.isNotPackagePrivate
import com.github.kyuubiran.ezxhelper.MemberExtensions.isPackagePrivate
import com.github.kyuubiran.ezxhelper.misc.Utils
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
    fun filterByModifiers(modifiers: Int): Self = applyThis {
        sequence = sequence.filter { it.modifiers == modifiers }
        exceptMessageScope {
            condition(
                "filterByModifiers(modifiers == ${String.format("0x%08X", modifiers)} == [${Utils.getMemberModifiersString(modifiers)}])"
            )
        }
    }

    /**
     * Use condition to filter by the modifiers.
     * @param predicate the condition
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): Self = applyThis {
        sequence = sequence.filter { predicate(it.modifiers) }
        exceptMessageScope { condition("filterByModifiers(CustomCondition)") }
    }

    /**
     * Filter include the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterIncludeModifiers(modifiers: Int): Self = applyThis {
        sequence = sequence.filter { (it.modifiers and modifiers) != 0 }
        exceptMessageScope {
            condition(
                "filterIncludeModifiers(${String.format("0x%08X", modifiers)} == [${Utils.getMemberModifiersString(modifiers)}])"
            )
        }
    }

    /**
     * Filter exclude the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterExcludeModifiers(modifiers: Int): Self = applyThis {
        sequence = sequence.filter { (it.modifiers and modifiers) == 0 }
        exceptMessageScope { condition("filterExcludeModifiers(${String.format("0x%08X", modifiers)} == [${Utils.getMemberModifiersString(modifiers)}])") }
    }

    /**
     * Filter if they are public.
     * @return [Self] the filtered finder
     */
    fun filterPublic() = applyThis {
        sequence = sequence.filter { Modifier.isPublic(it.modifiers) }
        exceptMessageScope { condition("filterPublic") }
    }

    /**
     * Filter if they are non-public.
     * @return [Self] the filtered finder
     */
    fun filterNonPublic() = applyThis {
        sequence = sequence.filter { !Modifier.isPublic(it.modifiers) }
        exceptMessageScope { condition("filterNonPublic") }
    }

    /**
     * Filter if they are protected.
     * @return [Self] the filtered finder
     */
    fun filterProtected() = applyThis {
        sequence = sequence.filter { Modifier.isProtected(it.modifiers) }
        exceptMessageScope { condition("filterProtected") }
    }

    /**
     * Filter if they are non-protected.
     * @return [Self] the filtered finder
     */
    fun filterNonProtected() = applyThis {
        sequence = sequence.filter { !Modifier.isProtected(it.modifiers) }
        exceptMessageScope { condition("filterNonProtected") }
    }

    /**
     * Filter if they are private.
     * @return [Self] the filtered finder
     */
    fun filterPrivate() = applyThis {
        sequence = sequence.filter { Modifier.isPrivate(it.modifiers) }
        exceptMessageScope { condition("filterPrivate") }
    }

    /**
     * Filter if they are non-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPrivate() = applyThis {
        sequence = sequence.filter { !Modifier.isPrivate(it.modifiers) }
        exceptMessageScope { condition("filterNonPrivate") }
    }

    /**
     * Filter if they are package-private.
     * @return [Self] the filtered finder
     */
    fun filterPackagePrivate() = applyThis {
        sequence = sequence.filter { it.isPackagePrivate }
        exceptMessageScope { condition("filterPackagePrivate") }
    }

    /**
     * Filter if they are non-package-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPackagePrivate() = applyThis {
        sequence = sequence.filter { it.isNotPackagePrivate }
        exceptMessageScope { condition("filterNonPackagePrivate") }
    }
    // endregion

    protected fun allowAccess(member: Member) {
        if (member !is AccessibleObject) return
        member.runCatching { isAccessible = true }
            .logeIfThrow("Cannot set accessible to ${member.name}")
    }
}