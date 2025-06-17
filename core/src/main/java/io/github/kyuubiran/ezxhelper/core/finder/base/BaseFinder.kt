@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.kyuubiran.ezxhelper.core.finder.base

import io.github.kyuubiran.ezxhelper.core.`interface`.INamed


abstract class BaseFinder<T, Finder>(protected var sequence: Sequence<T>) : INamed {

    abstract fun newFinder(sequence: Sequence<T>): Finder

    @Suppress("UNCHECKED_CAST")
    protected inline fun makeNewFinder(block: BaseFinder<T, Finder>.() -> Sequence<T>) =
        (newFinder(block()) as BaseFinder<T, Finder>) as Finder

    // region get elem

    /**
     * Get the first element or null if not found.
     *
     * 获取第一个元素，没有找到则返回 null
     *
     * @return [T] the first element or null | 没有找到则返回 null
     */
    open fun firstOrNull(): T? = sequence.firstOrNull()

    /**
     * Get the first element or throw an exception if there is no such element.
     *
     * 获取第一个元素，没有找到则抛出异常
     *
     * @return [T] the first element | 没有找到则抛出异常
     * @throws NoSuchElementException if sequence is empty. | 如果序列为空则抛出 [NoSuchElementException] 异常
     */
    @Throws(NoSuchElementException::class)
    open fun first(): T = try {
        sequence.first()
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("No such element in $name")
    }

    /**
     * Get the last element or null if not found.
     *
     * 获取最后一个元素，没有找到则返回 null
     *
     * @return [T] the last element or null | 没有找到则返回 null
     */
    open fun lastOrNull(): T? = sequence.lastOrNull()

    /**
     * Get the last element or throw an exception if there is no such element.
     *
     * 获取最后一个元素，没有找到则抛出异常
     *
     * @return [T] the last element | 没有找到则抛出异常
     * @throws NoSuchElementException if sequence is empty. | 如果序列为空则抛出 [NoSuchElementException] 异常
     */
    @Throws(NoSuchElementException::class)
    open fun last(): T = try {
        sequence.last()
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("No such element in $name")
    }

    /**
     * Get the first element by condition or throw an exception if there is no such element.
     *
     * 获取第一个元素，没有找到则抛出异常
     *
     * @param condition filter condition | 过滤条件
     * @return [T] the first element | 第一个元素
     * @throws NoSuchElementException if sequence is empty. | 如果序列为空则抛出 [NoSuchElementException] 异常
     */
    @Throws(NoSuchElementException::class)
    open fun first(condition: T.() -> Boolean) = try {
        sequence.first(condition)
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("No such element in $name")
    }

    /**
     * Get the last element by condition or throw an exception if there is no such element.
     *
     * 获取最后一个元素，没有找到则抛出异常
     *
     * @param condition filter condition | 过滤条件
     * @return [T] the last element | 最后一个元素
     * @throws NoSuchElementException if sequence is empty. | 如果序列为空则抛出 [NoSuchElementException] 异常
     */
    @Throws(NoSuchElementException::class)
    open fun last(condition: T.() -> Boolean) = try {
        sequence.last(condition)
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("No such element in $name")
    }

    /**
     * Get the first element by condition or null if not found
     *
     * 获取第一个元素，没有找到则返回 null
     *
     * @param condition filter condition | 过滤条件
     * @return [T] the first element or null | 没有找到则返回 null
     */
    open fun firstOrNull(condition: T.() -> Boolean) = sequence.firstOrNull(condition)

    /**
     * Get the last element by condition or null if not found
     *
     * 获取最后一个元素，没有找到则返回 null
     *
     * @param condition filter condition | 过滤条件
     * @return [T] the last element or null | 没有找到则返回 null
     */
    open fun lastOrNull(condition: T.() -> Boolean) = sequence.lastOrNull(condition)

    /**
     * Get the single element or throw an exception if there is no such element or more than one element.
     *
     * 获取单个元素，如果未找到或有多个元素则抛出异常
     *
     * @return [T] the single element | 单个元素
     * @throws IllegalArgumentException if there is more than one element. | 如果有多个元素则抛出 [IllegalArgumentException] 异常
     * @throws NoSuchElementException if sequence is empty. | 如果序列为空则抛出 [NoSuchElementException] 异常
     */
    @Throws(IllegalArgumentException::class, NoSuchElementException::class)
    open fun single() = try {
        sequence.single()
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("No such element in $name")
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("More than one element in $name")
    }

    /**
     * Get the single element or throw an exception if there is no such element or more than one element.
     *
     * 获取单个元素，如果未找到或有多个元素则抛出异常
     *
     * @param condition filter condition | 过滤条件
     * @return [T] the single element | 单个元素
     * @throws IllegalArgumentException if there is more than one element. | 如果有多个元素则抛出 [IllegalArgumentException] 异常
     * @throws NoSuchElementException if sequence is empty. | 如果序列为空则抛出 [NoSuchElementException] 异常
     */
    @Throws(IllegalArgumentException::class, NoSuchElementException::class)
    open fun single(condition: T.() -> Boolean) = try {
        sequence.single(condition)
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException("No such element in $name")
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("More than one element in $name")
    }

    /**
     * Get the single element or null if not found.
     *
     * 获取单个元素，没有找到则返回 null
     *
     * @return [T] the single element or null | 没有找到则返回 null
     */
    open fun singleOrNull() = sequence.singleOrNull()

    /**
     * Get the single element by condition or null if not found.
     *
     * 获取单个元素，没有找到则返回 null
     *
     * @param condition filter condition | 过滤条件
     * @return [T] the single element or null | 没有找到则返回 null
     */
    open fun singleOrNull(condition: T.() -> Boolean) = sequence.singleOrNull(condition)

    // endregion

    /**
     * Filter with a predicate.
     *
     * 过滤序列中的元素
     *
     * @param filter the predicate | 过滤条件
     * @return [Finder] the filtered finder | 返回过滤后的新 [Finder]
     */
    fun filter(filter: T.() -> Boolean): Finder = makeNewFinder {
        sequence.filter(filter)
    }

    // region for-each

    /**
     * On-each loop for.
     *
     * 遍历序列中的每个元素并执行操作
     *
     * @param action the action | 操作
     * @return [Finder] new finder | 返回新的 [Finder]
     */
    fun onEach(action: (T) -> Unit): Finder = makeNewFinder { sequence.onEach(action) }

    /**
     * On-each loop with index for.
     *
     * 遍历序列中的每个元素并执行操作，带有索引
     *
     * @param action the action | 操作
     * @return [Finder] new finder | 返回新的 [Finder]
     */
    fun onEachIndexed(action: (index: Int, T) -> Unit): Finder =
        makeNewFinder { sequence.onEachIndexed(action) }

    /**
     * For-each loop for.
     *
     * 遍历序列中的每个元素并执行操作
     *
     * @param action the action | 操作
     * @return [Finder] new finder | 返回新的 [Finder]
     */
    fun forEach(action: (T) -> Unit) = sequence.forEach(action)

    /**
     * For-each loop with index for.
     *
     * 遍历序列中的每个元素并执行操作，带有索引
     *
     * @param action the action | 操作
     * @return [Finder] new finder | 返回新的 [Finder]
     */
    fun forEachIndexed(action: (index: Int, T) -> Unit) = sequence.forEachIndexed(action)

    // endregion

    // region map

    /**
     * Map to the list.
     *
     * 映射序列中的每个元素到一个新的 [List]
     *
     * @param transform the transform action | 转换操作
     * @return [List] the list | 返回新的 [List]
     */
    fun <R> mapToList(transform: (T) -> R): List<R> = sequence.map(transform).toList()

    /**
     * Map to the mutable list.
     *
     * 映射序列中的每个元素到一个新的 [MutableList]
     *
     * @param transform the transform action | 转换操作
     * @return [MutableList] the mutable list | 返回新的 [MutableList]
     */
    fun <R> mapToMutableList(transform: (T) -> R): List<R> = sequence.map(transform).toMutableList()

    /**
     * Map to the set.
     *
     * 映射序列中的每个元素到一个新的 [Set]
     *
     * @param transform the transform action | 转换操作
     * @return [Set] the set | 返回新的 [Set]
     */
    fun <R> mapToSet(transform: (T) -> R): Set<R> = sequence.map(transform).toSet()

    /**
     * Map to the mutable set.
     *
     * 映射序列中的每个元素到一个新的 [MutableSet]
     *
     * @param transform the transform action | 转换操作
     * @return [MutableSet] the mutable set | 返回新的 [MutableSet]
     */
    fun <R> mapToMutableSet(transform: (T) -> R): MutableSet<R> =
        sequence.map(transform).toMutableSet()

    /**
     * Map to the hashset.
     *
     * 映射序列中的每个元素到一个新的 [HashSet]
     *
     * @param transform the transform action | 转换操作
     * @return [HashSet] the hashset | 返回新的 [HashSet]
     */
    fun <R> mapToHashSet(transform: (T) -> R): HashSet<R> = sequence.map(transform).toHashSet()

    /**
     * Map to the collection.
     *
     * 映射序列中的每个元素到一个新的 [Collection]
     *
     * @param destination the destination collection | 目标集合
     * @param transform the transform action | 转换操作
     * @return [MutableCollection] the collection | 返回新的 [MutableCollection]
     */
    fun <R, C> mapToCollection(
        destination: C,
        transform: (T) -> R
    ): C where C : MutableCollection<in R> =
        sequence.map(transform).toCollection(destination)
    // endregion

    // region collection
    /**
     * Make sequence to the list.
     *
     * 将序列转换为列表
     *
     * @return [List] the list | 返回新的 [List]
     */
    fun toList(): List<T> = sequence.toList()

    /**
     * Make sequence to the mutable list.
     *
     * 将序列转换为 [MutableList]
     *
     * @return [MutableList] the mutable list | 返回新的 [MutableList]
     */
    fun toMutableList(): MutableList<T> = sequence.toMutableList()

    /**
     * Make sequence to the set.
     *
     * 将序列转换为 [Set]
     *
     * @return [Set] the set | 返回新的 [Set]
     */
    fun toSet(): Set<T> = sequence.toSet()

    /**
     * Make sequence to the mutable set.
     *
     * 将序列转换为 [MutableSet]
     *
     * @return [MutableSet] the mutable set | 返回新的 [MutableSet]
     */
    fun toMutableSet(): MutableSet<T> = sequence.toMutableSet()

    /**
     * Make sequence to the hashset.
     *
     * 将序列转换为 [HashSet]
     *
     * @return [HashSet] the hashset | 返回新的 [HashSet]
     */
    fun toHashSet(): HashSet<T> = sequence.toHashSet()

    /**
     * Make sequence to the collection.
     *
     * 将序列转换为 [MutableCollection]
     *
     * @return [MutableCollection] the collection | 返回新的 [MutableCollection]
     */
    fun <C> toCollection(collection: C): C where C : MutableCollection<T> =
        sequence.toCollection(collection)

    // endregion

    // region contact

    /**
     * Concatenate with another finder.
     *
     * 将当前 [Finder] 与另一个 [Finder] 关联
     */
    fun contact(other: BaseFinder<T, Finder>): Finder = makeNewFinder {
        sequenceOf(sequence, other.sequence).flatten()
    }

    /**
     * Concatenate with another sequence.
     *
     * 将当前 [Finder] 与另一个 [Sequence] 关联
     */
    fun contact(other: Sequence<T>): Finder = makeNewFinder {
        sequenceOf(sequence, other).flatten()
    }

    /**
     * Concatenate with another array.
     *
     * 将当前 [Finder] 与另一个 [Array] 关联
     */
    fun contact(other: Array<T>): Finder = makeNewFinder {
        sequence.plus(other)
    }

    /**
     * Concatenate with another iterable.
     *
     *  将当前 [Finder] 与另一个 [Iterable] 关联
     */
    fun contact(other: Iterable<T>): Finder = makeNewFinder {
        sequence.plus(other)
    }

    operator fun plus(other: BaseFinder<T, Finder>) = contact(other)
    operator fun plus(other: Sequence<T>) = contact(other)
    operator fun plus(other: Array<T>) = contact(other)
    operator fun plus(other: Iterable<T>) = contact(other)

    operator fun plusAssign(other: BaseFinder<T, Finder>) {
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