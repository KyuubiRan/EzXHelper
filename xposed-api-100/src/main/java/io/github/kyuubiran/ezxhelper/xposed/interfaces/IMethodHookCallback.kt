package io.github.kyuubiran.ezxhelper.xposed.interfaces

import io.github.kyuubiran.ezxhelper.xposed.common.AfterHookParam
import io.github.kyuubiran.ezxhelper.xposed.common.BeforeHookParam

fun interface IMethodBeforeHookCallback {
    fun onMethodHooked(param: BeforeHookParam)
}

fun interface IMethodAfterHookCallback {
    fun onMethodHooked(param: AfterHookParam)
}