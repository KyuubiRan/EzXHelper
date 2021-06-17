package com.github.kyuubiran.ezxhelper.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XCallback
import java.lang.reflect.Constructor
import java.lang.reflect.Method

typealias Hooker = (param: XC_MethodHook.MethodHookParam) -> Unit
typealias ReplaceHooker = (param: XC_MethodHook.MethodHookParam) -> Any?

/**
 * 扩展函数 hook方法/构造
 * @param hookCallback XC_MethodHook
 */
fun Method.hookMethod(hookCallback: XC_MethodHook) {
    XposedBridge.hookMethod(this, hookCallback)
}

fun Constructor<*>.hookMethod(hookCallback: XC_MethodHook) {
    XposedBridge.hookMethod(this, hookCallback)
}

/**
 * 扩展函数 hook方法执行前
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Method.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: Hooker
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
 * 扩展函数 hook多个方法执行前
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Array<Method>.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
) {
    this.forEach {
        it.hookBefore(priority, hooker)
    }
}

/**
 * 扩展函数 hook构造执行前
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Constructor<*>.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: Hooker
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
 * 扩展函数 hook多个构造执行前
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Array<Constructor<*>>.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
) {
    this.forEach {
        it.hookBefore(priority, hooker)
    }
}


/**
 * 扩展函数 hook方法执行后
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Method.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: Hooker
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
 * 扩展函数 hook多个方法执行后
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Array<Method>.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
) {
    this.forEach {
        it.hookAfter(priority, hooker)
    }
}

/**
 * 扩展函数 hook构造执行后
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Constructor<*>.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
) {
    this.hookMethod(object : XC_MethodHook(priority) {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                hooker(param)
            } catch (thr: Throwable) {
                Log.t(thr)
            }
        }
    })
}

/**
 * 扩展函数 hook多个构造执行后
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Array<Constructor<*>>.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
) {
    this.forEach {
        it.hookAfter(priority, hooker)
    }
}

/**
 * 扩展函数 替换方法
 * @param priority 优先级 默认50
 * @param hook hook具体实现
 */
fun Method.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: ReplaceHooker
) {
    this.hookMethod(object : XC_MethodReplacement(priority) {
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            return try {
                hook(param)
            } catch (thr: Throwable) {
                Log.t(thr)
            }
        }
    })
}

/**
 * 扩展函数 替换多个方法
 *
 * 注意:会忽略hooker的返回值
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Array<Method>.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
) {
    this.forEach {
        it.hookReplace(priority, hooker)
    }
}

/**
 * 扩展函数 替换构造
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Constructor<*>.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
) {
    this.hookMethod(object : XC_MethodReplacement(priority) {
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            return try {
                hooker(param)
            } catch (thr: Throwable) {
                Log.t(thr)
            }
        }
    })
}

/**
 * 扩展函数 替换多个构造
 *
 * 注意:会忽略hooker的返回值
 * @param priority 优先级 默认50
 * @param hooker hook具体实现
 */
fun Array<Constructor<*>>.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
) {
    this.forEach {
        it.hookReplace(priority, hooker)
    }
}