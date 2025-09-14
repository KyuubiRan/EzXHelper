package io.github.kyuubiran.ezxhelper.xposed.interfaces

import io.github.libxposed.api.XposedInterface

fun interface IMethodBeforeHookCallback {
    fun onMethodHooked(param: XposedInterface.BeforeHookCallback)
}

fun interface IMethodAfterHookCallback {
    fun onMethodHooked(param: XposedInterface.AfterHookCallback)
}
