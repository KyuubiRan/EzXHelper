@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.Unhook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

class HookFactory {
    private val target: Member
    private var beforeHook: ((XC_MethodHook.MethodHookParam) -> Unit)? = null
    private var afterHook: ((XC_MethodHook.MethodHookParam) -> Unit)? = null

    private constructor(method: Method) {
        target = method
    }

    private constructor(constructor: Constructor<*>) {
        target = constructor
    }

    fun before(callback: ((param: XC_MethodHook.MethodHookParam) -> Unit)?) {
        beforeHook = callback
    }

    fun after(callback: ((param: XC_MethodHook.MethodHookParam) -> Unit)?) {
        afterHook = callback
    }

    fun replace(callback: (param: XC_MethodHook.MethodHookParam) -> Any?) {
        beforeHook = {
            it.result = callback(it)
        }
    }

    fun interrupt() {
        beforeHook = { it.result = null }
    }

    fun returnConstant(constant: Any?) {
        beforeHook = { it.result = constant }
    }

    private fun create(): Unhook = XposedBridge.hookMethod(target, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            beforeHook?.invoke(param)
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            afterHook?.invoke(param)
        }
    })

    companion object {
        fun Method.createHook(block: HookFactory.() -> Unit): Unhook = HookFactory(this).also(block).create()

        fun Constructor<*>.createHook(block: HookFactory.() -> Unit): Unhook = HookFactory(this).also(block).create()
    }
}

