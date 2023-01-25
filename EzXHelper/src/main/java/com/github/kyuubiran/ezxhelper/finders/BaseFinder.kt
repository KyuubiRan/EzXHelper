@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.finders

abstract class BaseFinder<T, Self> constructor(protected var sequence: Sequence<T>) {
    @Suppress("UNCHECKED_CAST")
    protected inline fun applyThis(block: BaseFinder<T, Self>.() -> Unit) = this.apply(block) as Self

    // region get elem

    /**
     * Get the first element or null if not found.
     * @return [T] the first element or null
     */
    open fun firstOrNull(): T? = sequence.firstOrNull()

    /**
     * Get the first element or throw an exception if there is no such element.
     * @return [T] the first element
     * @throws NoSuchElementException if sequence is empty.
     */
    @Throws(NoSuchElementException::class)
    open fun first(): T = sequence.first()

    /**
     * Get the last element or null if not found.
     * @return [T] the last element or null
     */
    open fun lastOrNull(): T? = sequence.lastOrNull()

    /**
     * Get the last element or throw an exception if there is no such element.
     * @return [T] the last element
     * @throws NoSuchElementException if sequence is empty.
     */
    @Throws(NoSuchElementException::class)
    open fun last(): T = sequence.last()

    /**
     * Get the first element by condition or throw an exception if there is no such element.
     * @param condition filter condition
     * @return [T] the first element
     * @throws NoSuchElementException if sequence is empty.
     */
    @Throws(NoSuchElementException::class)
    open fun first(condition: T.() -> Boolean) = sequence.first(condition)

    /**
     * Get the last element by condition or throw an exception if there is no such element.
     * @param condition filter condition
     * @return [T] the last element
     * @throws NoSuchElementException if sequence is empty.
     */
    @Throws(NoSuchElementException::class)
    open fun last(condition: T.() -> Boolean) = sequence.last(condition)

    /**
     * Get the first element by condition or null if not found
     * @param condition filter condition
     * @return [T] the first element or null
     */
    open fun firstOrNull(condition: T.() -> Boolean) = sequence.firstOrNull(condition)

    /**
     * Get the last element by condition or null if not found
     * @param condition filter condition
     * @return [T] the last element or null
     */
    open fun lastOrNull(condition: T.() -> Boolean) = sequence.lastOrNull(condition)

    // endregion

    /**
     * Filter with a predicate.
     * @param filter the predicate
     * @return [Self] the filtered finder
     */
    fun filter(filter: T.() -> Boolean): Self = applyThis {
        sequence = sequence.filter(filter)
    }

    // region for-each
    /**
     * On-each loop for.
     * @param action the action
     * @return [Self] the finder
     */
    fun onEach(action: (T) -> Unit): Self = applyThis { sequence.onEach(action) }

    /**
     * On-each loop with index for.
     * @param action the action
     * @return [Self] the finder
     */
    fun onEachIndexed(action: (index: Int, T) -> Unit): Self = applyThis { sequence.onEachIndexed(action) }

    /**
     * For-each loop for.
     * @param action the action
     * @return [Self] the finder
     */
    fun forEach(action: (T) -> Unit) = sequence.forEach(action)

    /**
     * For-each loop with index for.
     * @param action the action
     * @return [Self] the finder
     */
    fun forEachIndexed(action: (index: Int, T) -> Unit) = sequence.forEachIndexed(action)
    // endregion

    // region mapTo
    /**
     * Map to the list.
     * @param transform the transform action
     * @return [List] the list
     */
    fun <R> mapToList(transform: (T) -> R): List<R> = sequence.map(transform).toList()

    /**
     * Map to the mutable list.
     * @param transform the transform action
     * @return [MutableList] the mutable list
     */
    fun <R> mapToMutableList(transform: (T) -> R): List<R> = sequence.map(transform).toMutableList()

    /**
     * Map to the set.
     * @param transform the transform action
     * @return [Set] the set
     */
    fun <R> mapToSet(transform: (T) -> R): Set<R> = sequence.map(transform).toSet()

    /**
     * Map to the mutable set.
     * @param transform the transform action
     * @return [MutableSet] the mutable set
     */
    fun <R> mapToMutableSet(transform: (T) -> R): MutableSet<R> = sequence.map(transform).toMutableSet()

    /**
     * Map to the hashset.
     * @param transform the transform action
     * @return [HashSet] the hashset
     */
    fun <R> mapToHashSet(transform: (T) -> R): HashSet<R> = sequence.map(transform).toHashSet()

    /**
     * Map to the collection.
     * @param transform the transform action
     * @return [MutableCollection] the collection
     */
    fun <R, C> mapToCollection(destination: C, transform: (T) -> R): C where C : MutableCollection<in R> =
        sequence.map(transform).toCollection(destination)
    // endregion

    // region toCollection
    /**
     * Make sequence to the list.
     * @return [List] the list
     */
    fun toList(): List<T> = sequence.toList()

    /**
     * Make sequence to the mutable list.
     * @return [MutableList] the mutable list
     */
    fun toMutableList(): MutableList<T> = sequence.toMutableList()

    /**
     * Make sequence to the set.
     * @return [Set] the set
     */
    fun toSet(): Set<T> = sequence.toSet()

    /**
     * Make sequence to the mutable set.
     * @return [MutableSet] the mutable set
     */
    fun toMutableSet(): MutableSet<T> = sequence.toMutableSet()

    /**
     * Make sequence to the hashset.
     * @return [HashSet] the hashset
     */
    fun toHashSet(): HashSet<T> = sequence.toHashSet()

    /**
     * Make sequence to the collection.
     * @return [MutableCollection] the collection
     */
    fun <C> toCollection(collection: C): C where C : MutableCollection<T> = sequence.toCollection(collection)
    // endregion

    // region contact
    /**
     * Concatenate with another finder.
     */
    fun contact(other: BaseFinder<T, Self>): Self = applyThis {
        sequence += other.sequence
    }

    /**
     * Concatenate with another sequence.
     */
    fun contact(other: Sequence<T>): Self = applyThis {
        sequence += other
    }

    /**
     * Concatenate with another array.
     */
    fun contact(other: Array<T>): Self = applyThis {
        sequence += other.asSequence()
    }

    /**
     * Concatenate with another iterable.
     */
    fun contact(other: Iterable<T>): Self = applyThis {
        sequence += other.asSequence()
    }

    operator fun plus(other: BaseFinder<T, Self>) = contact(other)
    operator fun plus(other: Sequence<T>) = contact(other)
    operator fun plus(other: Array<T>) = contact(other)
    operator fun plus(other: Iterable<T>) = contact(other)

    operator fun plusAssign(other: BaseFinder<T, Self>) {
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
}