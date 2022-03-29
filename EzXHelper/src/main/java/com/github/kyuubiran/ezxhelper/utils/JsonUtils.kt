package com.github.kyuubiran.ezxhelper.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * 返回一个空的JSONArray
 */
fun emptyJSONArray(): JSONArray = JSONArray()

/**
 * 扩展属性 判断JSONArray是否为空
 */
val JSONArray.isEmpty: Boolean
    inline get() = this.length() == 0
val JSONArray.isNotEmpty: Boolean
    inline get() = this.length() != 0

/**
 * 扩展属性 获取长度范围 用于for循环
 */
val JSONArray.indices: IntRange
    inline get() = 0 until this.length()

/**
 * 扩展函数 遍历JSONArray
 * @param action 执行操作
 */
inline fun JSONArray.forEach(action: (Any) -> Unit) {
    for (i in this.indices) action(this.get(i))
}

/**
 * 扩展函数 遍历JSONArray 并包含索引
 * @param action 执行操作
 */
inline fun JSONArray.forEachIndexed(action: (Int, Any) -> Unit) {
    for (i in this.indices) action(i, this.get(i))
}

/**
 * 扩展函数 遍历JSONArray 并返回同一个JSONArray
 * @param action 执行操作
 */
inline fun JSONArray.onEach(action: (Any) -> Unit): JSONArray {
    for (i in this.indices) action(this.get(i))
    return this
}

/**
 * 扩展函数 遍历JSONArray 包含索引 并返回同一个JSONArray
 * @param action 执行操作
 */
inline fun JSONArray.onEachIndexed(action: (Int, Any) -> Unit): JSONArray {
    for (i in this.indices) action(i, this.get(i))
    return this
}

/**
 * 扩展函数 对JSONArray进行过滤 并返回新的JSONArray
 * @param predicate 过滤条件
 */
inline fun JSONArray.filter(predicate: (Any) -> Boolean): JSONArray {
    val result = JSONArray()
    for (i in this.indices) if (predicate(this.get(i))) result.put(this.get(i))
    return result
}

/**
 * 扩展函数 对JSONArray进行转换 并返回新的JSONArray
 * @param transform 转换函数
 */
inline fun JSONArray.map(transform: (Any) -> Any): JSONArray {
    val result = JSONArray()
    for (i in this.indices) result.put(transform(this.get(i)))
    return result
}

/**
 * 扩展函数 对JSONArray进行转换 并返List
 * @param transform 转换函数
 */
inline fun <T> JSONArray.mapToList(transform: (Any) -> T): List<T> {
    val result = ArrayList<T>(this.length())
    for (i in this.indices) result.add(transform(this.get(i)))
    return result
}

/**
 * 扩展函数 获取JSONObject中的 Long
 * 获取失败时返回缺省值
 * @param key 键值
 * @param defValue 缺省值
 * @return 获取成功时返回获取到的值 否则返回缺省值
 */
fun JSONObject.getLongOrDefault(key: String, defValue: Long = 0L): Long {
    return try {
        this.getLong(key)
    } catch (e: JSONException) {
        defValue
    }
}

/**
 * 扩展函数 获取JSONObject中的 Int
 * 获取失败时返回缺省值
 * @param key 键值
 * @param defValue 缺省值
 * @return 获取成功时返回获取到的值 否则返回缺省值
 */
fun JSONObject.getIntOrDefault(key: String, defValue: Int = 0): Int {
    return try {
        this.getInt(key)
    } catch (e: JSONException) {
        defValue
    }
}

/**
 * 扩展函数 获取JSONObject中的 Int
 * 获取失败时返回缺省值
 * @param key 键值
 * @param defValue 缺省值
 * @return 获取成功时返回获取到的值 否则返回缺省值
 */
fun JSONObject.getBooleanOrDefault(key: String, defValue: Boolean = false): Boolean {
    return try {
        this.getBoolean(key)
    } catch (e: JSONException) {
        defValue
    }
}

/**
 * 扩展函数 获取JSONObject中的 String
 * 获取失败时返回缺省值
 * @param key 键值
 * @param defValue 缺省值
 * @return 获取成功时返回获取到的值 否则返回缺省值
 */
fun JSONObject.getStringOrDefault(key: String, defValue: String = ""): String {
    return try {
        this.getString(key)
    } catch (e: JSONException) {
        defValue
    }
}

/**
 * 扩展函数 获取JSONObject中的 Long
 * 获取失败时返回缺省值
 * @param key 键值
 * @return 获取成功时返回获取到的值 否则返回null
 */
fun JSONObject.getObjectOrNull(key: String): Any? {
    return try {
        this.get(key)
    } catch (e: JSONException) {
        null
    }
}

/**
 * 扩展函数 获取JSONObject中的 Long
 * 获取失败时返回缺省值
 * @param key 键值
 * @return 获取成功时返回JSONArray 否则返回空JSONArray
 */
fun JSONObject.getJSONArrayOrEmpty(key: String): JSONArray {
    return try {
        this.getJSONArray(key)
    } catch (e: JSONException) {
        emptyJSONArray()
    }
}

/**
 * 构建一个JSONObject
 */
inline fun buildJSONObject(builder: JSONObject.() -> Unit): JSONObject = JSONObject().apply(builder)

/**
 * 构建一个JSONArray
 */
inline fun buildJSONArray(builder: JSONArray.() -> Unit): JSONArray = JSONArray().apply(builder)
