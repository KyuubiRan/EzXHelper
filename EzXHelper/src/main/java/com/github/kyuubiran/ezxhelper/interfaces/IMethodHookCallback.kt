package com.github.kyuubiran.ezxhelper.interfaces

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

fun interface IMethodHookCallback {
    fun onMethodHooked(param: MethodHookParam)
}