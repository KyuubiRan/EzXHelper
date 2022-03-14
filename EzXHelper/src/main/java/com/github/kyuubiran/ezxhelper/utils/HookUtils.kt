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
 * @param hookCallback [XC_MethodHook]
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Method.hookMethod(hookCallback: XC_MethodHook): XC_MethodHook.Unhook {
    return XposedBridge.hookMethod(this, hookCallback)
}

fun Constructor<*>.hookMethod(hookCallback: XC_MethodHook): XC_MethodHook.Unhook {
    return XposedBridge.hookMethod(this, hookCallback)
}

/**
 * 扩展函数 hook方法执行前
 * @param priority 优先级 默认50
 * @param hook [Hooker] hook具体实现
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Method.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: Hooker
): XC_MethodHook.Unhook {
    return this.hookMethod(object : XC_MethodHook(priority) {
        override fun beforeHookedMethod(param: MethodHookParam) {
            try {
                hook(param)
            } catch (thr: Throwable) {
                Log.ex(thr)
            }
        }
    })
}

/**
 * 扩展函数 hook多个方法执行前
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Array<Method>.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>()
        .also { ret ->
            this.forEach { m ->
                ret += m.hookBefore(priority, hooker)
            }
        }.toTypedArray()
}

/**
 * 扩展函数 hook构造执行前
 * @param priority 优先级 默认50
 * @param hook [Hooker] hook具体实现
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Constructor<*>.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: Hooker
): XC_MethodHook.Unhook {
    return this.hookMethod(object : XC_MethodHook(priority) {
        override fun beforeHookedMethod(param: MethodHookParam) {
            try {
                hook(param)
            } catch (thr: Throwable) {
                Log.ex(thr)
            }
        }
    })
}

/**
 * 扩展函数 hook多个构造执行前
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Array<Constructor<*>>.hookBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>()
        .also { ret ->
            this.forEach {
                ret += it.hookBefore(priority, hooker)
            }
        }.toTypedArray()
}


/**
 * 扩展函数 hook方法执行后
 * @param priority 优先级 默认50
 * @param hook [Hooker] hook具体实现
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Method.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: Hooker
): XC_MethodHook.Unhook {
    return this.hookMethod(object : XC_MethodHook(priority) {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                hook(param)
            } catch (thr: Throwable) {
                Log.ex(thr)
            }
        }
    })
}

/**
 * 扩展函数 hook多个方法执行后
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Array<Method>.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>()
        .also { ret ->
            this.forEach { m ->
                ret += m.hookAfter(priority, hooker)
            }
        }.toTypedArray()
}

/**
 * 扩展函数 hook构造执行后
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Constructor<*>.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): XC_MethodHook.Unhook {
    return this.hookMethod(object : XC_MethodHook(priority) {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                hooker(param)
            } catch (thr: Throwable) {
                Log.ex(thr)
            }
        }
    })
}

/**
 * 扩展函数 hook多个构造执行后
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Array<Constructor<*>>.hookAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>()
        .also { ret ->
            this.forEach {
                ret += it.hookAfter(priority, hooker)
            }
        }.toTypedArray()
}

/**
 * 扩展函数 替换方法
 * @param priority 优先级 默认50
 * @param hook [Hooker] hook具体实现
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Method.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: ReplaceHooker
): XC_MethodHook.Unhook {
    return this.hookMethod(object : XC_MethodReplacement(priority) {
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            return try {
                hook(param)
            } catch (thr: Throwable) {
                Log.ex(thr)
            }
        }
    })
}

/**
 * 扩展函数 替换多个方法
 *
 * 注意:会忽略hooker的返回值
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Array<Method>.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>()
        .also { ret ->
            this.forEach { m ->
                ret += m.hookReplace(priority, hooker)
            }
        }.toTypedArray()
}

/**
 * 扩展函数 替换构造
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhook [XC_MethodHook.Unhook]
 */
fun Constructor<*>.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
): XC_MethodHook.Unhook {
    return this.hookMethod(object : XC_MethodReplacement(priority) {
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            return try {
                hooker(param)
            } catch (thr: Throwable) {
                Log.ex(thr)
            }
        }
    })
}

/**
 * 扩展函数 替换多个构造
 *
 * 注意:会忽略hooker的返回值
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Array<Constructor<*>>.hookReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>()
        .also { ret ->
            this.forEach {
                ret += it.hookReplace(priority, hooker)
            }
        }.toTypedArray()
}

/**
 * 扩展函数 hook类的所有构造前
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Class<*>.hookAllConstructorBefore(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return this.declaredConstructors.hookBefore(priority, hooker)
}

/**
 * 扩展函数 hook类的所有构造后
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Class<*>.hookAllConstructorAfter(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return this.declaredConstructors.hookAfter(priority, hooker)
}

/**
 * 扩展函数 替换类的所有构造
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun Class<*>.hookAllConstructorReplace(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: ReplaceHooker
): Array<XC_MethodHook.Unhook> {
    return this.declaredConstructors.hookReplace(priority, hooker)
}

/**
 * hook类的所有构造前
 * @param clzName 类名
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun hookAllConstructorBefore(
    clzName: String,
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return loadClass(clzName).declaredConstructors.hookBefore(priority, hooker)
}

/**
 * hook类的所有构造后
 * @param clzName 类名
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun hookAllConstructorAfter(
    clzName: String,
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return loadClass(clzName).declaredConstructors.hookAfter(priority, hooker)
}

/**
 * 替换类的所有构造
 * @param clzName 类名
 * @param priority 优先级 默认50
 * @param hooker [Hooker] hook具体实现
 * @return unhooks Array<[XC_MethodHook.Unhook]>
 */
fun hookAllConstructorReplace(
    clzName: String,
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hooker: Hooker
): Array<XC_MethodHook.Unhook> {
    return loadClass(clzName).declaredConstructors.hookReplace(priority, hooker)
}

/**
 * Hook工厂类
 */
class XposedHookUtilFactory(priority: Int = XCallback.PRIORITY_DEFAULT) : XC_MethodHook(priority) {
    private var beforeMethod: Hooker? = null
    private var afterMethod: Hooker? = null

    override fun beforeHookedMethod(param: MethodHookParam) {
        beforeMethod?.invoke(param)
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        afterMethod?.invoke(param)
    }

    /**
     * hook方法执行前
     */
    fun before(before: Hooker) {
        this.beforeMethod = before
    }

    /**
     * hook方法执行后
     */
    fun after(after: Hooker) {
        this.afterMethod = after
    }
}

/**
 * 扩展函数 hook方法
 * 直接以
 *
 * before { }
 *
 * after { }
 *
 * 的形式进行hook 两者均为可选
 * @param priority 优先级 默认50
 * @param hook 传递的XposedHookUtilFactory
 * @return Unhook
 * @see XposedHookUtilFactory
 */
fun Method.hookMethod(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: XposedHookUtilFactory.() -> Unit
): XC_MethodHook.Unhook {
    val factory = XposedHookUtilFactory(priority)
    hook.invoke(factory)
    return this.hookMethod(factory)
}

/**
 * 扩展函数 hook构造
 * 直接以
 *
 * before { }
 *
 * after { }
 *
 * 的形式进行hook 两者均为可选
 * @param priority 优先级 默认50
 * @param hook 传递的XposedHookUtilFactory
 * @return Unhook
 * @see XposedHookUtilFactory
 */
fun Constructor<*>.hookMethod(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: XposedHookUtilFactory.() -> Unit
): XC_MethodHook.Unhook {
    val factory = XposedHookUtilFactory(priority)
    hook.invoke(factory)
    return this.hookMethod(factory)
}

/**
 * 扩展函数 hook多个方法
 * 直接以
 *
 * before { }
 *
 * after { }
 *
 * 的形式进行hook 两者均为可选
 * @param priority 优先级 默认50
 * @param hook 传递的XposedHookUtilFactory
 * @return Array<Unhook>
 * @see XposedHookUtilFactory
 */
fun Array<Method>.hookMethod(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: XposedHookUtilFactory.() -> Unit
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>().also { ret ->
        this.forEach { m ->
            ret += m.hookMethod(priority, hook)
        }
    }.toTypedArray()
}

/**
 * 扩展函数 hook多个构造
 * 直接以
 *
 * before { }
 *
 * after { }
 *
 * 的形式进行hook 两者均为可选
 * @param priority 优先级 默认50
 * @param hook 传递的XposedHookUtilFactory
 * @return Array<Unhook>
 * @see XposedHookUtilFactory
 */
fun Array<Constructor<*>>.hookMethod(
    priority: Int = XCallback.PRIORITY_DEFAULT,
    hook: XposedHookUtilFactory.() -> Unit
): Array<XC_MethodHook.Unhook> {
    return ArrayList<XC_MethodHook.Unhook>().also { ret ->
        this.forEach { m ->
            ret += m.hookMethod(priority, hook)
        }
    }.toTypedArray()
}

fun Array<XC_MethodHook.Unhook>.unhookAll() {
    this.forEach { it.unhook() }
}
