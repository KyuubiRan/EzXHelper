package com.github.kyuubiran.ezxhelper.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * 返回一个空的JSONArray
 */
fun emptyJSONArray(): JSONArray {
    return JSONArray()
}

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

/**
 * 获取JSONObject中的 Long
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
 * 获取JSONObject中的 Int
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
 * 获取JSONObject中的 Int
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
 * 获取JSONObject中的 String
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
 * 获取JSONObject中的 Long
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
 * 获取JSONObject中的 Long
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