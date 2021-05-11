package com.github.kyuubiran.ezxhelper.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XCallback
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 扩展函数 hook方法/构造
 * @param hookCallback XC_MethodHook
 * @throws IllegalArgumentException Member不为方法/构造
 */
fun Member.hookMethod(hookCallback: XC_MethodHook) {
    if (this !is Method && this !is Constructor<*>) throw IllegalArgumentException("Only methods and constructors can be hooked!")
    XposedBridge.hookMethod(this, hookCallback)
}

/**
 * 扩展函数 hook方法/构造执行前
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Member.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: (param: XC_MethodHook.MethodHookParam) -> Unit
) {
    this.hookMethod(object : XC_MethodHook(priority) {
        override fun beforeHookedMethod(param: MethodHookParam) {
            try {
                hook(param)
            } catch (thr: Throwable) {
                Log.t(thr)
            }
        }
    })
}

/**
 * 扩展函数 hook方法/构造执行后
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Member.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: (param: XC_MethodHook.MethodHookParam) -> Unit
) {
    this.hookMethod(object : XC_MethodHook(priority) {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                hook(param)
            } catch (thr: Throwable) {
                Log.t(thr)
            }
        }
    })
}

/**
 * 扩展函数 替换方法/构造
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Member.replaceHook(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: (param: XC_MethodHook.MethodHookParam) -> Any
) {
    this.hookMethod(object : XC_MethodReplacement(priority) {
        override fun replaceHookedMethod(param: MethodHookParam): Any {
            return try {
                hook(param)
            } catch (thr: Throwable) {
                Log.t(thr)
            }
        }
    })
}