package io.github.kyuubiran.ezxhelper.xposed.interfaces

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

fun interface IMethodHookCallback {
    fun onMethodHooked(param: MethodHookParam)
}