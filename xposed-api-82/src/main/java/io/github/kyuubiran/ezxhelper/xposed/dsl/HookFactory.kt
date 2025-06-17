package io.github.kyuubiran.ezxhelper.xposed.dsl

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodHookCallback
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.function.Consumer

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
     *
     * Hook 方法执行前
     *
     * @param callback before hook callback | 执行前回调
     */
    fun before(callback: IMethodHookCallback?) {
        beforeHook = callback
    }

    /**
     * Hook method after invoked
     *
     * Hook 方法执行后
     *
     * @param callback after hook callback | 执行后回调
     */
    fun after(callback: IMethodHookCallback?) {
        afterHook = callback
    }

    /**
     * Replace the method, just a wrapper of [before]
     *
     * 替换方法，等同于 [before] 的包装
     *
     */
    fun replace(callback: (param: XC_MethodHook.MethodHookParam) -> Any?) {
        beforeHook = IMethodHookCallback { param -> param.result = callback(param) }
    }

    /**
     * Interrupt the method, make method return null, just a wrapper of [before] and same as [returnConstant]`(null)`
     *
     * 中断方法，使方法返回 null，等同于 [before] 的包装，并且与 [returnConstant]`(null)` 相同
     *
     * **WARNING: MAY CAUSE EXCEPTION IF METHOD RETURNS NON-NULL TYPE**
     *
     * **警告：如果方法返回非 null 类型，可能会导致异常**
     */
    fun interrupt() {
        beforeHook = IMethodHookCallback { param -> param.result = null }
    }

    /**
     * Replace the result of the method, just a wrapper of [before]
     *
     * 替换方法的返回值，等同于 [before] 的包装
     *
     * @param constant the constant value to replace | 要替换的常量值
     */
    fun returnConstant(constant: Any?) {
        beforeHook = IMethodHookCallback { param -> param.result = constant }
    }

    private fun create(priority: Int = XC_MethodHook.PRIORITY_DEFAULT): XC_MethodHook.Unhook =
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
        @JvmSynthetic
        fun Method.createHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): XC_MethodHook.Unhook =
            HookFactory(this).also(block).create(priority)

        @JvmName("-createMethodBeforeHook")
        @JvmSynthetic
        fun Method.createBeforeHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmName("-createMethodAfterHook")
        @JvmSynthetic
        fun Method.createAfterHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(this).apply { afterHook = block }.create(priority)

        @JvmName("-createConstructorHook")
        @JvmSynthetic
        fun Constructor<*>.createHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): XC_MethodHook.Unhook =
            HookFactory(this).also(block).create(priority)

        @JvmName("-createConstructorBeforeHook")
        @JvmSynthetic
        fun Constructor<*>.createBeforeHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmName("-createConstructorAfterHook")
        @JvmSynthetic
        fun Constructor<*>.createAfterHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(this).apply { afterHook = block }.create(priority)

        @JvmName("-createMethodHooks")
        @JvmSynthetic
        fun Iterable<Method>.createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XC_MethodHook.Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createMethodBeforeHooks")
        @JvmSynthetic
        fun Iterable<Method>.createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createMethodAfterHooks")
        @JvmSynthetic
        fun Iterable<Method>.createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createMethodHooks")
        @JvmSynthetic
        fun Array<Method>.createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XC_MethodHook.Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createMethodBeforeHooks")
        @JvmSynthetic
        fun Array<Method>.createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createMethodAfterHooks")
        @JvmSynthetic
        fun Array<Method>.createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createConstructorHooks")
        @JvmSynthetic
        fun Iterable<Constructor<*>>.createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XC_MethodHook.Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createConstructorBeforeHooks")
        @JvmSynthetic
        fun Iterable<Constructor<*>>.createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createConstructorAfterHooks")
        @JvmSynthetic
        fun Iterable<Constructor<*>>.createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createConstructorHooks")
        @JvmSynthetic
        fun Array<Constructor<*>>.createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XC_MethodHook.Unhook> =
            map { it.createHook(priority, block) }

        @JvmName("-createConstructorBeforeHooks")
        @JvmSynthetic
        fun Array<Constructor<*>>.createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createConstructorAfterHooks")
        @JvmSynthetic
        fun Array<Constructor<*>>.createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            map { it.createAfterHook(priority, block) }

        @JvmName("createMethodHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            method: Method,
            block: Consumer<HookFactory>
        ): XC_MethodHook.Unhook =
            HookFactory(method).also { block.accept(it) }.create(priority)

        @JvmName("createMethodBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            method: Method, block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(method).apply { beforeHook = block }.create(priority)

        @JvmName("createMethodAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            method: Method,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(method).apply { afterHook = block }.create(priority)

        @JvmName("createConstructorHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: Consumer<HookFactory>
        ): XC_MethodHook.Unhook =
            HookFactory(ctor).also { block.accept(it) }.create(priority)

        @JvmName("createConstructorBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctor: Constructor<*>, block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(ctor).apply { beforeHook = block }.create(priority)

        @JvmName("createConstructorAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: IMethodHookCallback
        ): XC_MethodHook.Unhook =
            HookFactory(ctor).apply { afterHook = block }.create(priority)

        @JvmName("createMethodHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            methods.map { createHook(priority, it, block) }

        @JvmName("createMethodBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createBeforeHook(priority, it, block) }

        @JvmName("createMethodAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createAfterHook(priority, it, block) }

        @JvmName("createMethodHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            methods.map { createHook(priority, it, block) }

        @JvmName("createMethodBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createBeforeHook(priority, it, block) }

        @JvmName("createMethodAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            methods.map { createAfterHook(priority, it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createHook(priority, it, block) }

        @JvmName("createConstructorBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createBeforeHook(priority, it, block) }

        @JvmName("createConstructorAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createAfterHook(priority, it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: Consumer<HookFactory>
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createHook(priority, it, block) }

        @JvmName("createConstructorBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createBeforeHook(priority, it, block) }

        @JvmName("createConstructorAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XC_MethodHook.PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: IMethodHookCallback
        ): List<XC_MethodHook.Unhook> =
            ctors.map { createAfterHook(priority, it, block) }
    }
}