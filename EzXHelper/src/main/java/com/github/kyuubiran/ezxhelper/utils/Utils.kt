package com.github.kyuubiran.ezxhelper.utils

import android.app.Activity
import dalvik.system.BaseDexClassLoader
import java.util.*
import kotlin.system.exitProcess

/**
 * 尝试执行一块代码，如果成功返true，失败则返回false
 * @param block 执行的代码块
 * @return 成功为true，失败为false
 */
inline fun tryOrFalse(block: () -> Unit): Boolean {
    return try {
        block()
        true
    } catch (thr: Throwable) {
        false
    }
}

/**
 * 尝试执行一块代码，如果成功返true，失败则返回false并且记录日志
 * @param block 执行的代码块
 * @return 成功为true，失败为false
 */
inline fun tryOrLogFalse(block: () -> Unit): Boolean {
    return try {
        block()
        true
    } catch (thr: Throwable) {
        Log.e(thr)
        false
    }
}

/**
 * 尝试执行一块代码，如果成功返回代码块执行的结果，失败则返回null
 * @param block 执行的代码块
 * @return 成功返回代码块执行的返回值，失败返回null
 */
inline fun <T> tryOrNull(block: () -> T?): T? {
    return try {
        block()
    } catch (thr: Throwable) {
        null
    }
}

/**
 * 尝试执行一块代码，如果成功返回代码块执行的结果，失败则返回null并且记录日志
 * @param block 执行的代码块
 * @return 成功返回代码块执行的返回值，失败返回null
 */
inline fun <T> tryOrLogNull(block: () -> T?): T? {
    return try {
        block()
    } catch (thr: Throwable) {
        Log.e(thr)
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

/**
 * 扩展函数 往列表中添加非空元素
 * @param element 元素
 */
fun <T> MutableList<T>.addIfNonNull(element: T?) {
    if (element != null) {
        this.add(element)
    }
}

/**
 * 扩展函数 尝试往列表中添加元素
 * @param action 行为
 */
inline fun <T> MutableList<T>.tryAdd(action: () -> T?) {
    runCatching { this.addIfNonNull(action()) }
}

/**
 * 扩展函数 保留可变集合中符合条件的元素
 * @param predicate 条件
 */
inline fun <E> MutableSet<E>.keepIf(predicate: (E) -> Boolean) {
    val collection = mutableSetOf<E>()
    this.forEach { item -> if (!predicate(item)) collection.add(item) }
    this.removeAll(collection)
}

/**
 * 扩展函数 保留可变集合中符合条件的元素 并返回可变集合
 * @param predicate 条件
 * @return 保留符合条件的元素之后的可变集合
 */
inline fun <E> MutableSet<E>.applyKeepIf(predicate: (E) -> Boolean): MutableSet<E> {
    this.keepIf(predicate)
    return this
}

/**
 * 扩展函数 移除可变字集合符合条件的元素
 * @param predicate 条件
 */
inline fun <E> MutableSet<E>.dropIf(predicate: (E) -> Boolean) {
    val collection = mutableSetOf<E>()
    this.forEach { item -> if (predicate(item)) collection.add(item) }
    this.removeAll(collection)
}

/**
 * 扩展函数 移除可变集合中符合条件的元素 并返回可变集合
 * @param predicate 条件
 * @return 移除符合条件的元素之后的可变集合
 */
inline fun <E> MutableSet<E>.applyDropIf(predicate: (E) -> Boolean): MutableSet<E> {
    this.dropIf(predicate)
    return this
}

/**
 * 扩展函数 保留可变字典中符合条件的元素
 * @param predicate 条件
 */
inline fun <K, V> MutableMap<K, V>.keepIf(predicate: (K, V) -> Boolean) {
    val collection = mutableMapOf<K, V>()
    this.forEach { (key, value) ->
        if (!predicate(key, value)) collection[key] = value
    }
    collection.forEach { this.remove(it.key) }
}

/**
 * 扩展函数 保留可变字典中符合条件的元素 并返回可变字典
 * @param predicate 条件
 * @return 保留符合条件的元素之后的可变字典
 */
inline fun <K, V> MutableMap<K, V>.applyKeepIf(predicate: (K, V) -> Boolean): MutableMap<K, V> {
    this.keepIf(predicate)
    return this
}

/**
 * 扩展函数 移除可变字典中符合条件的元素
 * @param predicate 条件
 */
inline fun <K, V> MutableMap<K, V>.dropIf(predicate: (K, V) -> Boolean) {
    val collection = mutableMapOf<K, V>()
    this.forEach { (key, value) ->
        if (predicate(key, value)) collection[key] = value
    }
    collection.forEach { this.remove(it.key) }
}

/**
 * 扩展函数 移除可变字典中符合条件的元素 并返回可变字典
 * @param predicate 条件
 * @return 移除符合条件的元素之后的可变字典
 */
inline fun <K, V> MutableMap<K, V>.applyDropIf(
    predicate: (K, V) -> Boolean
): MutableMap<K, V> {
    this.dropIf(predicate)
    return this
}

/**
 * 取自 哔哩漫游
 * 获取所有类名
 * @see `https://github.com/yujincheng08/BiliRoaming`
 */
fun ClassLoader.getAllClassesList(delegate: (BaseDexClassLoader) -> BaseDexClassLoader = { loader -> loader }): List<String> {
    var loader = this
    while (loader !is BaseDexClassLoader) {
        loader = loader.parent ?: return emptyList()
    }
    return delegate(loader).getObjectOrNull("pathList")
        ?.getObjectOrNullAs<Array<Any>>("dexElements")
        ?.flatMap {
            it.getObjectOrNull("dexFile")
                ?.invokeMethodAutoAs<Enumeration<String>>("entries")
                ?.toList().orEmpty()
        }.orEmpty()
}

/**
 * 重新启动宿主App
 */
fun restartHostApp(activity: Activity) {
    val pm = activity.packageManager
    val intent = pm.getLaunchIntentForPackage(activity.packageName)
    activity.finishAffinity()
    activity.startActivity(intent)
    exitProcess(0)
}

fun <T> Array<T>.stream() = this.toList().stream()
