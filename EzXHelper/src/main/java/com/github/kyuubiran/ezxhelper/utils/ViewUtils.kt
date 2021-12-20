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
 * 扩展函数 遍历ViewGroup
 */
inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

/**
 * 扩展函数 带index遍历ViewGroup
 */
inline fun ViewGroup.forEachIndexed(action: (index: Int, view: View) -> Unit) {
    for (index in 0 until childCount) {
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
        if (condition(it)) {
            return it
        }
    }
    return null
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
