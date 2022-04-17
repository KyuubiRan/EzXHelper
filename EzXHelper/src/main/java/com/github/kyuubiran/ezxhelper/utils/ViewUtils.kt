package com.github.kyuubiran.ezxhelper.utils

import android.view.View
import android.view.ViewGroup

/**
 * 扩展函数 将View布局的高度和宽度设置为0
 */
fun View.setViewZeroSize() {
    this.layoutParams.height = 0
    this.layoutParams.width = 0
}

/**
 * 扩展函数 将View的visibility设置为Invisible
 */
fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

/**
 * 扩展函数 将View的visibility设置为Visible
 */
fun View.setVisible() {
    this.visibility = View.VISIBLE
}

/**
 * 扩展函数 将View的visibility设置为Gone
 */
fun View.setGone() {
    this.visibility = View.GONE
}

/**
 * 扩展属性 获取长度范围 用于for循环
 */
inline val ViewGroup.indices: IntRange
    get() = 0 until childCount

/**
 * 扩展函数 遍历ViewGroup
 */
inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
    for (index in this.indices) {
        action(getChildAt(index))
    }
}

/**
 * 扩展函数 带index遍历ViewGroup
 */
inline fun ViewGroup.forEachIndexed(action: (index: Int, view: View) -> Unit) {
    for (index in this.indices) {
        action(index, getChildAt(index))
    }
}

/**
 * 扩展函数 判断ViewGroup是否为空
 * @return 是否为空
 */
fun ViewGroup.isEmpty(): Boolean {
    return this.childCount == 0
}

/**
 * 扩展函数 判断ViewGroup是否不为空
 * @return 是否不为空
 */
fun ViewGroup.isNotEmpty(): Boolean {
    return this.childCount != 0
}

/**
 * 扩展函数 遍历ViewGroup 根据条件查找View
 * @param condition 条件
 * @return 成功时返回符合条件的view 失败时返回null
 */
fun ViewGroup.findViewByCondition(condition: (view: View) -> Boolean): View? {
    this.forEach {
        if (condition(it)) return it
        else if (it is ViewGroup) {
            val v = it.findViewByCondition(condition)
            if (v != null) return v
        }
    }
    return null
}

/**
 * 扩展函数 遍历ViewGroup 根据条件查找所有符合条件的View
 * @param condition 条件
 * @return 符合条件的ViewList
 */
fun ViewGroup.findAllViewsByCondition(condition: (view: View) -> Boolean): List<View> {
    val list = mutableListOf<View>()
    this.forEach {
        if (condition(it)) list.add(it)
        else if (it is ViewGroup) {
            val v = it.findAllViewsByCondition(condition)
            if (v.isNotEmpty()) list.addAll(v)
        }
    }
    return list
}

/**
 * 扩展函数 遍历ViewGroup 根据条件查找View 并将View转换为T?类型
 * @param condition 条件
 * @return 成功时返回符合条件的view 失败时返回null
 */
@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.findViewByConditionAs(condition: (view: View) -> Boolean): T? {
    return this.findViewByCondition(condition) as T?
}
