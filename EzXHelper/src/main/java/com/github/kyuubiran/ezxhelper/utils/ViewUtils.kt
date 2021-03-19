package com.github.kyuubiran.ezxhelper.utils

import android.view.View

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