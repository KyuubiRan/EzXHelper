@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.utils.Log.logeIfThrow
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Member
import java.lang.reflect.Modifier

@Suppress("UNCHECKED_CAST")
abstract class BaseMemberFinder<T, Self> internal constructor(protected var memberSequence: Sequence<T>) where T : Member {
    protected inline fun applyThis(block: BaseMemberFinder<T, Self>.() -> Unit) = this.apply { block() } as Self

    fun firstOrNull(): T? = memberSequence.firstOrNull()?.also { allowAccess(it) }

    @Throws(NoSuchElementException::class)
    fun first(): T = memberSequence.first().also { allowAccess(it) }

    // region contact
    fun contact(other: BaseMemberFinder<T, Self>): Self = applyThis {
        memberSequence += other.memberSequence
    }

    fun contact(other: Sequence<T>): Self = applyThis {
        memberSequence += other
    }

    fun contact(other: Array<T>): Self = applyThis {
        memberSequence += other.asSequence()
    }

    fun contact(other: Iterable<T>): Self = applyThis {
        memberSequence += other.asSequence()
    }

    operator fun plus(other: BaseMemberFinder<T, Self>) = contact(other)
    operator fun plus(other: Sequence<T>) = contact(other)
    operator fun plus(other: Array<T>) = contact(other)
    operator fun plus(other: Iterable<T>) = contact(other)

    operator fun plusAssign(other: BaseMemberFinder<T, Self>) {
        contact(other)
    }

    operator fun plusAssign(other: Sequence<T>) {
        contact(other)
    }

    operator fun plusAssign(other: Array<T>) {
        contact(other)
    }

    operator fun plusAssign(other: Iterable<T>) {
        contact(other)
    }
    // endregion

    fun filter(filter: T.() -> Boolean): Self = applyThis {
        memberSequence.filter(filter)
    }

    // region filter by
    // endregion

    // region filter modifiers
    fun filterByModifiers(modifiers: Int): Self = applyThis {
        memberSequence.filter { it.modifiers == modifiers }
    }

    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): Self = applyThis {
        memberSequence.filter { predicate(it.modifiers) }
    }

    fun filterIncludeModifiers(modifiers: Int): Self = applyThis {
        memberSequence.filter { (it.modifiers and modifiers) != 0 }
    }

    fun filterExcludeModifiers(modifiers: Int): Self = applyThis {
        memberSequence.filter { (it.modifiers and modifiers) == 0 }
    }

    fun filterPublic() = filterIncludeModifiers(Modifier.PUBLIC)
    fun filterNonPublic() = filterExcludeModifiers(Modifier.PUBLIC)

    fun filterProtected() = filterIncludeModifiers(Modifier.PROTECTED)
    fun filterNonProtected() = filterExcludeModifiers(Modifier.PROTECTED)

    fun filterPrivate() = filterIncludeModifiers(Modifier.PRIVATE)
    fun filterNonPrivate() = filterExcludeModifiers(Modifier.PRIVATE)

    fun filterPackagePrivate() = filterExcludeModifiers(Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE)
    fun filterNonPackagePrivate() = filterExcludeModifiers(Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE)
    // endregion

    // region for-each
    fun onEach(action: (T) -> Unit): Self = applyThis { memberSequence.onEach(action) }
    fun onEachIndexed(action: (index: Int, T) -> Unit): Self = applyThis { memberSequence.onEachIndexed(action) }
    fun forEach(action: (T) -> Unit) = memberSequence.forEach(action)
    fun forEachIndexed(action: (index: Int, T) -> Unit) = memberSequence.forEachIndexed(action)
    // endregion

    // region mapTo
    fun <R> mapToList(transform: (T) -> R): List<R> = memberSequence.map(transform).toList()
    fun <R> mapToMutableList(transform: (T) -> R): List<R> = memberSequence.map(transform).toMutableList()
    fun <R> mapToSet(transform: (T) -> R): Set<R> = memberSequence.map(transform).toSet()
    fun <R> mapToMutableSet(transform: (T) -> R): MutableSet<R> = memberSequence.map(transform).toMutableSet()
    fun <R> mapToHashSet(transform: (T) -> R): HashSet<R> = memberSequence.map(transform).toHashSet()
    fun <R, C> mapToCollection(destination: C, transform: (T) -> R): C where C : MutableCollection<in R> =
        memberSequence.map(transform).toCollection(destination)
    // endregion

    // region toCollection
    fun toList(): List<T> = memberSequence.onEach { allowAccess(it) }.toList()
    fun toMutableList(): MutableList<T> = memberSequence.onEach { allowAccess(it) }.toMutableList()
    fun toSet(): Set<T> = memberSequence.onEach { allowAccess(it) }.toSet()
    fun toMutableSet(): MutableSet<T> = memberSequence.onEach { allowAccess(it) }.toMutableSet()
    fun toHashSet(): HashSet<T> = memberSequence.onEach { allowAccess(it) }.toHashSet()
    fun <C> toCollection(collection: C): C where C : MutableCollection<T> = memberSequence.onEach { allowAccess(it) }.toCollection(collection)
    // endregion

    protected fun allowAccess(member: Member) {
        (member as? AccessibleObject)?.runCatching { isAccessible = true }?.logeIfThrow("Cannot set accessible to ${member.name}")
    }
}