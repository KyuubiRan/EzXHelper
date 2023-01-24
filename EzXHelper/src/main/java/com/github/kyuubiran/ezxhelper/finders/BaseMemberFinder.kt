@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.Log.logeIfThrow
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Member
import java.lang.reflect.Modifier

@Suppress("UNCHECKED_CAST")
abstract class BaseMemberFinder<T, Self> internal constructor(protected var memberSequence: Sequence<T>) where T : Member {
    protected inline fun applyThis(block: BaseMemberFinder<T, Self>.() -> Unit) = this.apply(block) as Self

    /**
     * Get the first element or null if not found.
     * @return [T : Member] the first element or null
     */
    fun firstOrNull(): T? = memberSequence.firstOrNull()?.also { allowAccess(it) }

    /**
     * Get the first element or throw an exception if there is no such element.
     * @return [T : Member] the first element
     * @throws NoSuchElementException if is empty.
     */
    @Throws(NoSuchElementException::class)
    fun first(): T = memberSequence.first().also { allowAccess(it) }

    /**
     * Get the last element or null if not found.
     * @return [T : Member] the last element or null
     */
    fun lastOrNull(): T? = memberSequence.lastOrNull()?.also { allowAccess(it) }

    /**
     * Get the last element or throw an exception if there is no such element.
     * @return [T : Member] the last element
     * @throws NoSuchElementException if is empty.
     */
    fun last(): T = memberSequence.last().also { allowAccess(it) }

    // region contact
    /**
     * Concatenate with another finder.
     */
    fun contact(other: BaseMemberFinder<T, Self>): Self = applyThis {
        memberSequence += other.memberSequence
    }

    /**
     * Concatenate with another sequence.
     */
    fun contact(other: Sequence<T>): Self = applyThis {
        memberSequence += other
    }

    /**
     * Concatenate with another array.
     */
    fun contact(other: Array<T>): Self = applyThis {
        memberSequence += other.asSequence()
    }

    /**
     * Concatenate with another iterable.
     */
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

    /**
     * Filter with a predicate.
     * @param filter the predicate
     * @return [Self] the filtered finder
     */
    fun filter(filter: T.() -> Boolean): Self = applyThis {
        memberSequence = memberSequence.filter(filter)
    }

    // region filter by
    // endregion

    // region filter modifiers
    /**
     * Filter by the same modifiers.
     * @param modifiers the modifiers
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(modifiers: Int): Self = applyThis {
        memberSequence = memberSequence.filter { it.modifiers == modifiers }
    }

    /**
     * Use condition to filter by the modifiers.
     * @param predicate the condition
     * @return [Self] the filtered finder
     */
    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): Self = applyThis {
        memberSequence = memberSequence.filter { predicate(it.modifiers) }
    }

    /**
     * Filter include the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterIncludeModifiers(modifiers: Int): Self = applyThis {
        memberSequence = memberSequence.filter { (it.modifiers and modifiers) != 0 }
    }

    /**
     * Filter exclude the modifiers.
     * @param modifiers the modifiers.
     * @return [Self] the filtered finder
     */
    fun filterExcludeModifiers(modifiers: Int): Self = applyThis {
        memberSequence = memberSequence.filter { (it.modifiers and modifiers) == 0 }
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
    fun filterPackagePrivate() = filterExcludeModifiers(Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE)

    /**
     * Filter if they are non-package-private.
     * @return [Self] the filtered finder
     */
    fun filterNonPackagePrivate() = filterExcludeModifiers(Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE)
    // endregion

    // region for-each
    /**
     * On-each loop for.
     * @param action the action
     * @return [Self] the finder
     */
    fun onEach(action: (T) -> Unit): Self = applyThis { memberSequence.onEach(action) }

    /**
     * On-each loop with index for.
     * @param action the action
     * @return [Self] the finder
     */
    fun onEachIndexed(action: (index: Int, T) -> Unit): Self = applyThis { memberSequence.onEachIndexed(action) }

    /**
     * For-each loop for.
     * @param action the action
     * @return [Self] the finder
     */
    fun forEach(action: (T) -> Unit) = memberSequence.forEach(action)

    /**
     * For-each loop with index for.
     * @param action the action
     * @return [Self] the finder
     */
    fun forEachIndexed(action: (index: Int, T) -> Unit) = memberSequence.forEachIndexed(action)
    // endregion

    // region mapTo
    /**
     * Map to the list.
     * @param transform the transform action
     * @return [List] the list
     */
    fun <R> mapToList(transform: (T) -> R): List<R> = memberSequence.map(transform).toList()

    /**
     * Map to the mutable list.
     * @param transform the transform action
     * @return [MutableList] the mutable list
     */
    fun <R> mapToMutableList(transform: (T) -> R): List<R> = memberSequence.map(transform).toMutableList()

    /**
     * Map to the set.
     * @param transform the transform action
     * @return [Set] the set
     */
    fun <R> mapToSet(transform: (T) -> R): Set<R> = memberSequence.map(transform).toSet()

    /**
     * Map to the mutable set.
     * @param transform the transform action
     * @return [MutableSet] the mutable set
     */
    fun <R> mapToMutableSet(transform: (T) -> R): MutableSet<R> = memberSequence.map(transform).toMutableSet()

    /**
     * Map to the hashset.
     * @param transform the transform action
     * @return [HashSet] the hashset
     */
    fun <R> mapToHashSet(transform: (T) -> R): HashSet<R> = memberSequence.map(transform).toHashSet()

    /**
     * Map to the collection.
     * @param transform the transform action
     * @return [MutableCollection] the collection
     */
    fun <R, C> mapToCollection(destination: C, transform: (T) -> R): C where C : MutableCollection<in R> =
        memberSequence.map(transform).toCollection(destination)
    // endregion

    // region toCollection
    /**
     * Make sequence to the list.
     * @return [List] the list
     */
    fun toList(): List<T> = memberSequence.toList()

    /**
     * Make sequence to the mutable list.
     * @return [MutableList] the mutable list
     */
    fun toMutableList(): MutableList<T> = memberSequence.toMutableList()

    /**
     * Make sequence to the set.
     * @return [Set] the set
     */
    fun toSet(): Set<T> = memberSequence.toSet()

    /**
     * Make sequence to the mutable set.
     * @return [MutableSet] the mutable set
     */
    fun toMutableSet(): MutableSet<T> = memberSequence.toMutableSet()

    /**
     * Make sequence to the hashset.
     * @return [HashSet] the hashset
     */
    fun toHashSet(): HashSet<T> = memberSequence.toHashSet()

    /**
     * Make sequence to the collection.
     * @return [MutableCollection] the collection
     */
    fun <C> toCollection(collection: C): C where C : MutableCollection<T> = memberSequence.toCollection(collection)
    // endregion

    protected fun allowAccess(member: Member) {
        (member as? AccessibleObject)?.runCatching { isAccessible = true }?.logeIfThrow("Cannot set accessible to ${member.name}")
    }
}