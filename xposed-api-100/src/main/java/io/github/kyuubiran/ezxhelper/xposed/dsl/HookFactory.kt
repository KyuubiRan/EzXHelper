package io.github.kyuubiran.ezxhelper.xposed.dsl

import io.github.kyuubiran.ezxhelper.xposed.api.XposedApi.hook
import io.github.kyuubiran.ezxhelper.xposed.common.AfterHookParam
import io.github.kyuubiran.ezxhelper.xposed.common.BeforeHookParam
import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodAfterHookCallback
import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodBeforeHookCallback
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

class HookFactory private constructor(private val target: Member) {

    private var beforeHook: IMethodBeforeHookCallback? = null
    private var afterHook: IMethodAfterHookCallback? = null

    /**
     * Hook method before invoke
     *
     * Hook 方法执行前
     *
     * @param callback before hook callback | 执行前回调
     */
    fun before(callback: IMethodBeforeHookCallback?) {
        beforeHook = callback
    }

    /**
     * Hook method after invoked
     *
     * Hook 方法执行后
     *
     * @param callback after hook callback | 执行后回调
     */
    fun after(callback: IMethodAfterHookCallback?) {
        afterHook = callback
    }

    /**
     * Replace the method, just a wrapper of [before]
     *
     * 替换方法，等同于 [before] 的包装
     *
     */
    fun replace(callback: (param: BeforeHookParam) -> Any?) {
        beforeHook = IMethodBeforeHookCallback { param -> param.result = callback(param) }
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
        beforeHook = IMethodBeforeHookCallback { param -> param.result = null }
    }

    /**
     * Replace the result of the method, just a wrapper of [before]
     *
     * 替换方法的返回值，等同于 [before] 的包装
     *
     * @param constant the constant value to replace | 要替换的常量值
     */
    fun returnConstant(constant: Any?) {
        beforeHook = IMethodBeforeHookCallback { param -> param.result = constant }
    }

    private fun create(priority: Int = XposedInterface.PRIORITY_DEFAULT): XposedInterface.MethodUnhooker<out Member> {
        hooks[target] = beforeHook to afterHook

        val unhooker = when (target) {
            is Method -> hook(target, priority, GenericHooker::class.java)
            is Constructor<*> -> hook(target, priority, GenericHooker::class.java)
            else -> throw IllegalStateException("Unsupported member type: $target")
        }

        return object : XposedInterface.MethodUnhooker<Member> {
            override fun getOrigin(): Member = target
            override fun unhook() {
                unhooker.unhook()
                hooks.remove(target)
            }
        }
    }

    @Suppress("ClassName")
    companion object `-Static` {
        internal val hooks = ConcurrentHashMap<Member, Pair<IMethodBeforeHookCallback?, IMethodAfterHookCallback?>>()

        class GenericHooker : XposedInterface.Hooker {
            companion object {
                @JvmStatic
                fun before(callback: XposedInterface.BeforeHookCallback) {
                    hooks[callback.member]?.first?.onMethodHooked(BeforeHookParam(callback))
                }

                @JvmStatic
                fun after(callback: XposedInterface.AfterHookCallback) {
                    hooks[callback.member]?.second?.onMethodHooked(AfterHookParam(callback))
                }
            }
        }

        // region Internal-API
        @JvmSynthetic
        private fun <T : Executable> T.internalCreateHook(
            priority: Int,
            block: HookFactory.() -> Unit
        ): XposedInterface.MethodUnhooker<out Member> = HookFactory(this).also(block).create(priority)

        @JvmSynthetic
        private fun <T : Executable> T.internalCreateHook(
            priority: Int,
            block: Consumer<HookFactory>
        ): XposedInterface.MethodUnhooker<out Member> = HookFactory(this).also { block.accept(it) }.create(priority)

        @JvmSynthetic
        private fun <T : Executable> T.internalCreateBeforeHook(
            priority: Int,
            block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmSynthetic
        private fun <T : Executable> T.internalCreateAfterHook(
            priority: Int,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = HookFactory(this).apply { afterHook = block }.create(priority)
        // endregion

        @JvmName("-createMethodHook")
        @JvmSynthetic
        fun Method.createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): XposedInterface.MethodUnhooker<out Member> = internalCreateHook(priority, block)

        @JvmName("-createMethodBeforeHook")
        @JvmSynthetic
        fun Method.createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = internalCreateBeforeHook(priority, block)

        @JvmName("-createMethodAfterHook")
        @JvmSynthetic
        fun Method.createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = internalCreateAfterHook(priority, block)

        @JvmName("-createConstructorHook")
        @JvmSynthetic
        fun Constructor<*>.createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): XposedInterface.MethodUnhooker<out Member> = internalCreateHook(priority, block)

        @JvmName("-createConstructorBeforeHook")
        @JvmSynthetic
        fun Constructor<*>.createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = internalCreateBeforeHook(priority, block)

        @JvmName("-createConstructorAfterHook")
        @JvmSynthetic
        fun Constructor<*>.createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = internalCreateAfterHook(priority, block)

        @JvmName("-createMethodHooks")
        @JvmSynthetic
        fun Iterable<Method>.createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createHook(priority, block) }

        @JvmName("-createMethodBeforeHooks")
        @JvmSynthetic
        fun Iterable<Method>.createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createMethodAfterHooks")
        @JvmSynthetic
        fun Iterable<Method>.createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createMethodHooks")
        @JvmSynthetic
        fun Array<Method>.createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createHook(priority, block) }

        @JvmName("-createMethodBeforeHooks")
        @JvmSynthetic
        fun Array<Method>.createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createMethodAfterHooks")
        @JvmSynthetic
        fun Array<Method>.createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createConstructorHooks")
        @JvmSynthetic
        fun Iterable<Constructor<*>>.createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createHook(priority, block) }

        @JvmName("-createConstructorBeforeHooks")
        @JvmSynthetic
        fun Iterable<Constructor<*>>.createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createConstructorAfterHooks")
        @JvmSynthetic
        fun Iterable<Constructor<*>>.createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createAfterHook(priority, block) }

        @JvmName("-createConstructorHooks")
        @JvmSynthetic
        fun Array<Constructor<*>>.createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createHook(priority, block) }

        @JvmName("-createConstructorBeforeHooks")
        @JvmSynthetic
        fun Array<Constructor<*>>.createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createBeforeHook(priority, block) }

        @JvmName("-createConstructorAfterHooks")
        @JvmSynthetic
        fun Array<Constructor<*>>.createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            map { it.createAfterHook(priority, block) }

        @JvmName("createMethodHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            method: Method,
            block: Consumer<HookFactory>
        ): XposedInterface.MethodUnhooker<out Member> = method.internalCreateHook(priority, block)

        @JvmName("createMethodBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            method: Method, block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = method.internalCreateBeforeHook(priority, block)

        @JvmName("createMethodAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            method: Method,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = method.internalCreateAfterHook(priority, block)

        @JvmName("createConstructorHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: Consumer<HookFactory>
        ): XposedInterface.MethodUnhooker<out Member> = ctor.internalCreateHook(priority, block)

        @JvmName("createConstructorBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctor: Constructor<*>, block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = ctor.internalCreateBeforeHook(priority, block)

        @JvmName("createConstructorAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> = ctor.internalCreateAfterHook(priority, block)

        @JvmName("createMethodHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: Consumer<HookFactory>
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            methods.map { createHook(priority, it, block) }

        @JvmName("createMethodBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            methods.map { createBeforeHook(priority, it, block) }

        @JvmName("createMethodAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            methods: Iterable<Method>,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            methods.map { createAfterHook(priority, it, block) }

        @JvmName("createMethodHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: Consumer<HookFactory>
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            methods.map { createHook(priority, it, block) }

        @JvmName("createMethodBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            methods.map { createBeforeHook(priority, it, block) }

        @JvmName("createMethodAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            methods: Array<Method>,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            methods.map { createAfterHook(priority, it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: Consumer<HookFactory>
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            ctors.map { createHook(priority, it, block) }

        @JvmName("createConstructorBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            ctors.map { createBeforeHook(priority, it, block) }

        @JvmName("createConstructorAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctors: Iterable<Constructor<*>>,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            ctors.map { createAfterHook(priority, it, block) }

        @JvmName("createConstructorHooks")
        @JvmStatic
        @JvmOverloads
        fun createHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: Consumer<HookFactory>
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            ctors.map { createHook(priority, it, block) }

        @JvmName("createConstructorBeforeHooks")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: IMethodBeforeHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            ctors.map { createBeforeHook(priority, it, block) }

        @JvmName("createConstructorAfterHooks")
        @JvmStatic
        @JvmOverloads
        fun createAfterHooks(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctors: Array<Constructor<*>>,
            block: IMethodAfterHookCallback
        ): List<XposedInterface.MethodUnhooker<out Member>> =
            ctors.map { createAfterHook(priority, it, block) }
    }
}