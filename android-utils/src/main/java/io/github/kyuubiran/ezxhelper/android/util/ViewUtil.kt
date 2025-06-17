package io.github.kyuubiran.ezxhelper.android.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.github.kyuubiran.ezxhelper.android.misc.Threading

object ViewUtil {

    /**
     * Get the indices(IntRange) of the views in the view group
     * For for-each
     *
     * 获取 ViewGroup 中视图的索引(IntRange)
     * 用于 for-each 迭代
     */
    inline val ViewGroup.indices: IntRange
        get() = 0 until childCount

    /**
     * For-each for ViewGroup
     *
     * 遍历 ViewGroup 中的每个 View
     */
    inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
        for (index in this.indices) {
            action(getChildAt(index))
        }
    }

    /**
     * For-each with index for ViewGroup
     *
     * 遍历 ViewGroup 中的每个 View，并带有索引
     */
    inline fun ViewGroup.forEachIndexed(action: (index: Int, view: View) -> Unit) {
        for (index in this.indices) {
            action(index, getChildAt(index))
        }
    }

    /**
     * Check the view group is empty.
     *
     * 检查 ViewGroup 是否为空
     */
    val ViewGroup.isEmpty: Boolean
        get() = this.childCount == 0

    /**
     * Check the view group is not empty.
     *
     * 检查 ViewGroup 是否不为空
     */
    val ViewGroup.isNotEmpty: Boolean
        get() = this.childCount != 0

    /**
     * For-each the view group and find the view by condition.
     *
     * 遍历 ViewGroup 中的每个 View，并根据条件查找视图
     *
     * @param condition condition | 条件
     * @return view or null if not found | 没有找到则返回 null
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
     *  For-each the view group and find the views by condition.
     *
     *  遍历 ViewGroup 中的每个 View，并根据条件查找视图
     *
     * @param condition condition | 条件
     * @return all the views that match the condition, or empty if non-matches | 返回所有匹配条件的视图，如果没有匹配则返回空列表
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
     * For-each the view group and find the view by condition, and cast to the [T] type.
     *
     * 遍历 ViewGroup 中的每个 View，并根据条件查找视图，并转换为 [T] 类型
     *
     * @param condition condition | 条件
     * @return view or null if not found | 没有找到则返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> ViewGroup.findViewByConditionAs(condition: (view: View) -> Boolean): T? {
        return this.findViewByCondition(condition) as T?
    }

    /**
     * Get the resource id by name
     *
     * 获取 id 通过资源名称
     *
     * @param name R.[type].[name]
     * @return id or 0 if not found | 没有找到则返回 0
     */
    @SuppressLint("DiscouragedApi")
    fun getResourceIdByName(name: String, type: String = "id", ctx: Context): Int {
        return ctx.resources.getIdentifier(name, type, ctx.packageName)
    }

    /**
     * Find view by id name
     *
     * 在 View 中通过资源名称查找视图
     *
     * @param name name | 资源名称
     * @return view or null if not found | 没有找到则返回 null
     */
    fun View.findViewByIdName(name: String): View? {
        val id = getResourceIdByName(name, ctx = this.context)
        if (id == 0) return null
        return this.findViewById(id)
    }

    fun Activity.findViewByIdName(name: String): View? {
        val id = getResourceIdByName(name, ctx = this)
        if (id == 0) return null
        return this.findViewById(id)
    }

    private var lastToast: Toast? = null

    /**
     * Show a toast message.
     *
     * 显示一个 Toast 消息
     *
     * @param msg message | 消息内容
     * @param cancelLast cancelLast | 是否取消上一个 Toast，默认为 true
     * @param duration duration | 持续时间，默认为 Toast.LENGTH_SHORT
     */
    @JvmSynthetic
    fun Context.makeToast(msg: String, cancelLast: Boolean = true, duration: Int = Toast.LENGTH_SHORT) {
        Threading.runOnUiThread {
            if (cancelLast) {
                lastToast?.cancel()
            }
            Toast.makeText(this, msg, duration).also { lastToast = it }.show()
        }
    }

    /**
     * Show a toast message with resource id.
     *
     * 显示一个带资源 id 的 Toast 消息
     *
     * @param msg message | 消息内容的资源 id
     * @param cancelLast cancelLast | 是否取消上一个 Toast，默认为 true
     * @param duration duration | 持续时间，默认为 Toast.LENGTH_SHORT
     */
    @JvmSynthetic
    fun Context.makeToast(msg: Int, cancelLast: Boolean = true, duration: Int = Toast.LENGTH_SHORT) {
        Threading.runOnUiThread {
            if (cancelLast) {
                lastToast?.cancel()
            }
            Toast.makeText(this, msg, duration).also { lastToast = it }.show()
        }
    }
}