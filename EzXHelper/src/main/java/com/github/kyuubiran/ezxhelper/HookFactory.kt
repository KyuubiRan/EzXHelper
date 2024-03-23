@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.annotations.KotlinOnly
import com.github.kyuubiran.ezxhelper.interfaces.IMethodHookCallback
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.PRIORITY_DEFAULT
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
     * Interrupt the method, make method return null, just a wrapper of [before] and same as [returnConstant]`(null)`
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

    private fun create(priority: Int = PRIORITY_DEFAULT): Unhook =
        XposedBridge.hookMethod(target, object : XC_MethodHook(priority) {
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
        fun Method.createHook(
            priority: Int = PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): Unhook =
            HookFactory(this).also(block).create(priority)

        @JvmName("-createMethodBeforeHook")
        @KotlinOnly
        fun Method.createBeforeHook(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): Unhook =
            HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmName("-createMethodAfterHook")
        @KotlinOnly
        fun Method.createAfterHook(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): Unhook =
            HookFactory(this).apply { afterHook = block }.create(priority)

        @JvmName("-createConstructorHook")
        @KotlinOnly
        fun Constructor<*>.createHook(
            priority: Int = PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): Unhook =
            HookFactory(this).also(block).create(priority)

        @JvmName("-createConstructorBeforeHook")
        @KotlinOnly
        fun Constructor<*>.createBeforeHook(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): Unhook =
            HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmName("-createConstructorAfterHook")
        @KotlinOnly
        fun Constructor<*>.createAfterHook(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): Unhook =
            HookFactory(this).apply { afterHook = block }.create(priority)

        @JvmName("-createMethodHooks")
        @KotlinOnly
        fun Iterable<Method>.createHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createMethodBeforeHooks")
        @KotlinOnly
        fun Iterable<Method>.createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createMethodAfterHooks")
        @KotlinOnly
        fun Iterable<Method>.createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createMethodHooks")
        @KotlinOnly
        fun Array<Method>.createHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createMethodBeforeHooks")
        @KotlinOnly
        fun Array<Method>.createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createMethodAfterHooks")
        @KotlinOnly
        fun Array<Method>.createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createConstructorHooks")
        @KotlinOnly
        fun Iterable<Constructor<*>>.createHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createConstructorBeforeHooks")
        @KotlinOnly
        fun Iterable<Constructor<*>>.createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createConstructorAfterHooks")
        @KotlinOnly
        fun Iterable<Constructor<*>>.createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createConstructorHooks")
        @KotlinOnly
        fun Array<Constructor<*>>.createHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createConstructorBeforeHooks")
        @KotlinOnly
        fun Array<Constructor<*>>.createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createConstructorAfterHooks")
        @KotlinOnly
        fun Array<Constructor<*>>.createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("createMethodHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = PRIORITY_DEFAULT,
            method: Method,
            block: Consumer<HookFactory>
        ): XC_MethodHook.Unhook =
            HookFactory(method).also { block.accept(it) }.create(priority)

        @JvmName("createMethodBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = PRIORITY_DEFAULT,
            method: Method, block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(method).apply { beforeHook = block }.create(priority)

        @JvmName("createMethodAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = PRIORITY_DEFAULT,
            method: Method,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(method).apply { afterHook = block }.create(priority)

        @JvmName("createConstructorHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: Consumer<HookFactory>
        ): XC_MethodHook.Unhook =
            HookFactory(ctor).also { block.accept(it) }.create(priority)

        @JvmName("createConstructorBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = PRIORITY_DEFAULT,
            ctor: Constructor<*>, block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(ctor).apply { beforeHook = block }.create(priority)

        @JvmName("createConstructorAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(ctor).apply { afterHook = block }.create(priority)

        @JvmName("createMethodHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            methods.map { createHook(priority, it, block) }

        @JvmName("createMethodBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createBeforeHook(priority, it, block) }

        @JvmName("createMethodAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createAfterHook(priority, it, block) }

        @JvmName("createMethodHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            methods.map { createHook(priority, it, block) }

        @JvmName("createMethodBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createBeforeHook(priority, it, block) }

        @JvmName("createMethodAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createAfterHook(priority, it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createHook(priority, it, block) }

        @JvmName("createConstructorBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createBeforeHook(priority, it, block) }

        @JvmName("createConstructorAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createAfterHook(priority, it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createHook(priority, it, block) }

        @JvmName("createConstructorBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createBeforeHook(priority, it, block) }

        @JvmName("createConstructorAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createAfterHook(priority, it, block) }
    }
}

