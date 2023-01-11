package com.github.kyuubiran.ezxhelper.utils.interfaces

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

fun interface IMethodHookCallback {
    fun onMethodHooked(param: MethodHookParam)
}