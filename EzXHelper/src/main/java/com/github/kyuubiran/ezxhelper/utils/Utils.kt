package com.github.kyuubiran.ezxhelper.utils

/**
 * 尝试执行一块代码，如果成功返true，失败则返回false
 * @param block 执行的代码块
 * @return 成功为true，失败为false
 */
inline fun tryOrFalse(crossinline block: () -> Unit): Boolean {
    return try {
        block()
        true
    } catch (thr: Throwable) {
        false
    }
}

/**
 * 尝试执行一块代码，如果成功返回代码块执行的结果，失败则返回null
 * @param block 执行的代码块
 * @return 成功返回代码块执行的返回值，失败返回null
 */
inline fun <T> tryOrNull(crossinline block: () -> T?): T? {
    return try {
        block()
    } catch (thr: Throwable) {
        null
    }
}

/**
 * 扩展函数 移除可变列表中符合条件的元素
 * @param predicate 条件
 */
inline fun <E> MutableList<E>.dropIf(predicate: ((E) -> Boolean)) {
    val collection = arrayListOf<E>()
    this.forEach { item -> if (predicate(item)) collection.add(item) }
    this.removeAll(collection)
}

/**
 * 扩展函数 移除可变列表中符合条件的元素 并返回可变列表
 * @param predicate 条件
 * @return 移除符合条件的元素之后的可变列表
 */
inline fun <E> MutableList<E>.applyDropIf(predicate: (E) -> Boolean): MutableList<E> {
    this.dropIf(predicate)
    return this
}

/**
 * 扩展函数 保留可变列表中符合条件的元素
 * @param predicate 条件
 */
inline fun <E> MutableList<E>.keepIf(predicate: ((E) -> Boolean)) {
    val collection = arrayListOf<E>()
    this.forEach { item -> if (!predicate(item)) collection.add(item) }
    this.removeAll(collection)
}

/**
 * 扩展函数 保留可变列表中符合条件的元素 并返回可变列表
 * @param predicate 条件
 * @return 保留符合条件的元素之后的可变列表
 */
inline fun <E> MutableList<E>.applyKeepIf(predicate: (E) -> Boolean): MutableList<E> {
    this.keepIf(predicate)
    return this
}