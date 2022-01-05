package com.github.kyuubiran.ezxhelper.utils

inline fun tryOrFalse(crossinline block: () -> Unit): Boolean {
    return try {
        block()
        true
    } catch (thr: Throwable) {
        false
    }
}

inline fun <T> tryOrNull(crossinline block: () -> T?): T? {
    return try {
        block()
    } catch (thr: Throwable) {
        null
    }
}

inline fun <E> MutableList<E>.removeElementsIf(predicate: ((E) -> Boolean)) {
    val rm = arrayListOf<Int>()
    this.forEachIndexed { idx, item -> if (predicate(item)) rm.add(idx) }
    rm.forEach { this.removeAt(it) }
}

inline fun <E> MutableList<E>.applyRemoveElementsIf(predicate: (E) -> Boolean): MutableList<E> {
    this.removeElementsIf(predicate)
    return this
}