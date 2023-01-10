package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope

object XposedScope : IXposedScope

@Suppress("FunctionName")
inline fun XposedScope(block: XposedScope.() -> Unit) = XposedScope.block()

@Suppress("FunctionName")
inline fun <T> XposedScope(block: XposedScope.() -> T) = XposedScope.block()
