@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import com.github.kyuubiran.ezxhelper.interfaces.IMethodHookCallback
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.Unhook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.function.Consumer

/**
 * The hook factory to quick create a method/constructor hook
 */
class HookFactory {
    private val target: Member
    private var beforeHook: IMethodHookCallback? = null
    private var afterHook: IMethodHookCallback? = null

    private constructor(method: Method) {
        target = method
    }

    private constructor(constructor: Constructor<*>) {
        target = constructor
    }

    /**
     * Hook method before invoke
     * @param callback before hook callback
     */
    fun before(callback: IMethodHookCallback?) {
        beforeHook = callback
    }

    /**
     * Hook method after invoked
     * @param callback after hook callback
     */
    fun after(callback: IMethodHookCallback?) {
        afterHook = callback
    }

    /**
     * Replace the method, just a wrapper of [before]
     */
    fun replace(callback: (param: XC_MethodHook.MethodHookParam) -> Any?) {
        beforeHook = IMethodHookCallback { param -> param.result = callback(param) }
    }

    /**
     * Interrupt the method, make method return null, just a wrapper of [before].
     *
     * **WARNING: MAY CAUSE EXCEPTION IF METHOD RETURNS NON-NULL TYPE**
     */
    fun interrupt() {
        beforeHook = IMethodHookCallback { param -> param.result = null }
    }

    /**
     * Replace the result of the method, just a wrapper of [before]
     * @param constant the constant value to replace
     */
    fun returnConstant(constant: Any?) {
        beforeHook = IMethodHookCallback { param -> param.result = constant }
    }

    private fun create(): Unhook = XposedBridge.hookMethod(target, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            beforeHook?.onMethodHooked(param)
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            afterHook?.onMethodHooked(param)
        }
    })

    @Suppress("ClassName")
    companion object `-Static` {
        @JvmName("-createMethodHook")
        @KotlinOnly
        fun Method.createHook(block: HookFactory.() -> Unit): Unhook = HookFactory(this).also(block).create()

        @JvmName("-createConstructorHook")
        @KotlinOnly
        fun Constructor<*>.createHook(block: HookFactory.() -> Unit): Unhook = HookFactory(this).also(block).create()

        @JvmName("-createMethodHooks")
        @KotlinOnly
        fun Iterable<Method>.createHooks(block: HookFactory.() -> Unit): List<Unhook> = map { it.createHook(block) }

        @JvmName("-createMethodHooks")
        @KotlinOnly
        fun Array<Method>.createHooks(block: HookFactory.() -> Unit): List<Unhook> = map { it.createHook(block) }

        @JvmName("-createConstructorHooks")
        @KotlinOnly
        fun Iterable<Constructor<*>>.createHooks(block: HookFactory.() -> Unit): List<Unhook> = map { it.createHook(block) }

        @JvmName("-createConstructorHooks")
        @KotlinOnly
        fun Array<Constructor<*>>.createHooks(block: HookFactory.() -> Unit): List<Unhook> = map { it.createHook(block) }

        @JvmName("createMethodHook")
        @JvmStatic
        fun createHook(m: Method, block: Consumer<HookFactory>): XC_MethodHook.Unhook =
            HookFactory(m).also { block.accept(it) }.create()

        @JvmName("createConstructorHook")
        @JvmStatic
        fun createHook(c: Constructor<*>, block: Consumer<HookFactory>): XC_MethodHook.Unhook =
            HookFactory(c).also { block.accept(it) }.create()

        @JvmName("createMethodHooks")
        @JvmStatic
        fun createHooks(m: Iterable<Method>, block: Consumer<HookFactory>): List<XC_MethodHook.Unhook> =
            m.map { createHook(it, block) }

        @JvmName("createMethodHooks")
        @JvmStatic
        fun createHooks(m: Array<Method>, block: Consumer<HookFactory>): List<XC_MethodHook.Unhook> =
            m.map { createHook(it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        fun createHooks(c: Iterable<Constructor<*>>, block: Consumer<HookFactory>): List<XC_MethodHook.Unhook> =
            c.map { createHook(it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        fun createHooks(c: Array<Constructor<*>>, block: Consumer<HookFactory>): List<XC_MethodHook.Unhook> =
            c.map { createHook(it, block) }
    }
}

