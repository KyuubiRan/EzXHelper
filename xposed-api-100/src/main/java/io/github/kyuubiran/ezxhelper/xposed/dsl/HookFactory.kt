package io.github.kyuubiran.ezxhelper.xposed.dsl

import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.interfaces.AfterHookParam
import io.github.kyuubiran.ezxhelper.xposed.interfaces.BeforeHookParam
import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodAfterHookCallback
import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodBeforeHookCallback
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

class HookFactory private constructor(private val target: Member) {

    private var beforeHook: IMethodBeforeHookCallback? = null
    private var afterHook: IMethodAfterHookCallback? = null

    fun before(callback: IMethodBeforeHookCallback?) {
        beforeHook = callback
    }

    fun after(callback: IMethodAfterHookCallback?) {
        afterHook = callback
    }

    fun replace(callback: (param: BeforeHookParam) -> Any?) {
        beforeHook = IMethodBeforeHookCallback { param -> param.result = callback(param) }
    }

    fun interrupt() {
        beforeHook = IMethodBeforeHookCallback { param -> param.result = null }
    }

    fun returnConstant(constant: Any?) {
        beforeHook = IMethodBeforeHookCallback { param -> param.result = constant }
    }

    private fun create(priority: Int = XposedInterface.PRIORITY_DEFAULT): XposedInterface.MethodUnhooker<out Member> {
        hooks[target] = Pair(beforeHook, afterHook)

        fun <T> doHookConstructor(constructor: Constructor<T>): XposedInterface.MethodUnhooker<Constructor<T>> {
            return EzXposed.hook(constructor, priority, GenericHooker::class.java)
        }

        val unhooker = when (target) {
            is Method -> EzXposed.hook(target, priority, GenericHooker::class.java)
            is Constructor<*> -> doHookConstructor(target)
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

        @JvmStatic
        fun getHooksForTest(): ConcurrentHashMap<Member, Pair<IMethodBeforeHookCallback?, IMethodAfterHookCallback?>> {
            return hooks
        }

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

        @JvmName("-createMethodHook")
        @JvmSynthetic
        fun Method.createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(this).also(block).create(priority)

        @JvmName("-createMethodBeforeHook")
        @JvmSynthetic
        fun Method.createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmName("-createMethodAfterHook")
        @JvmSynthetic
        fun Method.createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(this).apply { afterHook = block }.create(priority)

        @JvmName("-createConstructorHook")
        @JvmSynthetic
        fun Constructor<*>.createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: HookFactory.() -> Unit
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(this).also(block).create(priority)

        @JvmName("-createConstructorBeforeHook")
        @JvmSynthetic
        fun Constructor<*>.createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(this).apply { beforeHook = block }.create(priority)

        @JvmName("-createConstructorAfterHook")
        @JvmSynthetic
        fun Constructor<*>.createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(this).apply { afterHook = block }.create(priority)

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
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(method).also { block.accept(it) }.create(priority)

        @JvmName("createMethodBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            method: Method, block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(method).apply { beforeHook = block }.create(priority)

        @JvmName("createMethodAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            method: Method,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(method).apply { afterHook = block }.create(priority)

        @JvmName("createConstructorHook")
        @JvmStatic
        @JvmOverloads
        fun createHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: Consumer<HookFactory>
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(ctor).also { block.accept(it) }.create(priority)

        @JvmName("createConstructorBeforeHook")
        @JvmStatic
        @JvmOverloads
        fun createBeforeHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctor: Constructor<*>, block: IMethodBeforeHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(ctor).apply { beforeHook = block }.create(priority)

        @JvmName("createConstructorAfterHook")
        @JvmStatic
        @JvmOverloads
        fun createAfterHook(
            priority: Int = XposedInterface.PRIORITY_DEFAULT,
            ctor: Constructor<*>,
            block: IMethodAfterHookCallback
        ): XposedInterface.MethodUnhooker<out Member> =
            HookFactory(ctor).apply { afterHook = block }.create(priority)

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