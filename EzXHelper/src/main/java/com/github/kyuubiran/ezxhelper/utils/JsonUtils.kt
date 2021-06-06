package com.github.kyuubiran.ezxhelper.utils

import org.json.JSONArray

/**
 * 遍历json array
 * @param T 类型
 * @param action 操作
 */
@Suppress("UNCHECKED_CAST")
fun <T> JSONArray.forEach(action: (obj: T) -> Unit) {
    for (index in 0 until this.length()) {
        action(this.get(index) as T)
    }
}

/**
 * 将JSONArray 转换为ArrayList
 * @param T 类型
 * @return ArrayList
 */
fun <T> JSONArray.toArrayList(): ArrayList<T> {
    val arr = ArrayList<T>()
    this.forEach<T> { arr.add(it) }
    return arr
}

/**
 * 将JSONArray 转换为Array
 * @param T 类型
 * @return Array
 */
inline fun <reified T> JSONArray.toArray(): Array<T> {
    return this.toArrayList<T>().toTypedArray()
}

/**
 * 将JSONArray 转换为HashSet
 * @param E 类型
 * @return HashSet
 */
fun <E> JSONArray.toHashSet(): HashSet<E> {
    val hs = HashSet<E>()
    this.forEach<E> { hs.add(it) }
    return hs
}