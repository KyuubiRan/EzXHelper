@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.misc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.github.kyuubiran.ezxhelper.EzXHelper

object ViewUtils {
    /**
     * Get the indices(IntRange) of the views in the view group.
     * For for-each.
     */
    inline val ViewGroup.indices: IntRange
        @JvmSynthetic get() = 0 until childCount

    /**
     * For-each for ViewGroup
     */
    @JvmSynthetic
    inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
        for (index in this.indices) {
            action(getChildAt(index))
        }
    }

    /**
     * For-each with index for ViewGroup
     */
    @JvmSynthetic
    inline fun ViewGroup.forEachIndexed(action: (index: Int, view: View) -> Unit) {
        for (index in this.indices) {
            action(index, getChildAt(index))
        }
    }

    /**
     * Check the view group is  empty.
     */
    val ViewGroup.isEmpty: Boolean
        get() = this.childCount == 0

    /**
     * Check the view group is not empty.
     */
    val ViewGroup.isNotEmpty: Boolean
        get() = this.childCount != 0

    /**
     * For-each the view group and find the view by condition.
     * @param condition condition
     * @return view or null if not found
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
     * @param condition condition
     * @return all the views that match the condition, or empty if non-matches
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
     * @param condition condition
     * @return view or null if not found
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> ViewGroup.findViewByConditionAs(condition: (view: View) -> Boolean): T? {
        return this.findViewByCondition(condition) as T?
    }

    /**
     * Find id by name
     * @param name R.id.[name]
     * @return id found id or 0 if not found
     */
    @SuppressLint("DiscouragedApi")
    @JvmStatic
    fun getIdByName(name: String, ctx: Context = EzXHelper.appContext): Int {
        return ctx.resources.getIdentifier(name, "id", ctx.packageName)
    }

    /**
     * Find view by id name
     * @param name name
     * @return view or null if not found
     */
    fun View.findViewByIdName(name: String): View? {
        val id = getIdByName(name, this.context)
        if (id == 0) return null
        return this.findViewById(id)
    }

    fun Activity.findViewByIdName(name: String): View? {
        val id = getIdByName(name, this)
        if (id == 0) return null
        return this.findViewById(id)
    }
}