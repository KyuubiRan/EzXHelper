@file:Suppress("unused","UNCHECKED_CAST", "NOTHING_TO_INLINE")
package io.github.kyuubiran.ezxhelper.xposed.common

import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedInterface.AfterHookCallback

/**
 * Wraps [BeforeHookCallback] with ergonomic helpers around `this`, arguments.
 *
 * 包装 [BeforeHookCallback] ，便于处理 `this`、参数。
 *
 * @param original callback supplied by the runtime. | 运行时提供的回调。
 * @param onSkip Invoked once when the hook short-circuits the original execution. | 当 Hook 中断原始执行时调用一次。
 */
class BeforeHookParam(
    private val original: BeforeHookCallback,
    private val onSkip: () -> Unit = {},
) {

    private var skipped: Boolean = false

    internal val isSkipped: Boolean
        get() = skipped

    private fun markSkipped() {
        if (!skipped) {
            skipped = true
            onSkip()
        }
    }

    /**
     * Gets the method / constructor to be hooked.
     *
     * 获取被 hook 的方法 / 构造器
     */
    val member
        get() = original.member

    /**
     * Non-null receiver instance for instance methods.
     *
     * 实例方法对应的非空 this 对象。
     *
     * @throws NullPointerException if the hooked method is static. | 如果被 Hook 的方法是静态的。
     */
    val thisObject: Any
        get() = original.thisObject
            ?: throw NullPointerException("static method should not have thisObject")

    /**
     * Receiver instance or `null` for static methods.
     *
     * 实例方法返回 this，静态方法返回 null。
     */
    val thisObjectOrNull: Any?
        get() = original.thisObject

    /**
     * Convenience cast of [thisObject] to a specific type T.
     *
     * 将 [thisObject] 快捷转换为指定类型 T。
     *
     * @throws NullPointerException if the hooked method is static (via [thisObject]). | 如果被 Hook 的方法是静态的
     * @throws ClassCastException if the object is not of type T. | 如果对象不是 T 类型。
     */
    inline fun <T> thisObjectAs(): T = thisObject as T

    /**
     * Arguments passed to the hooked method or constructor.
     * Modifications to this array will change the arguments passed
     * to the original member.
     *
     * 被 Hook 的方法或构造器的参数数组。
     * 修改该数组会影响传入原始方法或构造器的实参。
     */
    val args: Array<Any?>
        get() = original.args

    /**
     * Assign a return value and skip the original method or constructor.
     * For constructors the `result` is ignored.
     * Note: the  after invocation callback will still be invoked.
     *
     * 设置返回值并跳过原始方法或构造器。
     * 对于构造器，`result` 会被忽略。
     * 注意：after 调用回调仍会执行。
     *
     * @param result The return value. | 返回值。
     */
    var result: Any?
        get() = null
        set(value) {
            markSkipped()
            original.returnAndSkip(value)
        }

    /**
     * Throws the given exception and skips the original method or constructor.
     * Note: the after invocation callback will still be invoked.
     *
     * 抛出指定异常并跳过原始方法或构造器。
     * 注意：after 调用回调仍会执行。
     *
     * @param throwable The exception to be thrown. | 要抛出的异常。
     */
    var throwable: Throwable?
        get() = null
        set(value) {
            markSkipped()
            original.throwAndSkip(value)
        }
}

/**
 * Wraps [AfterHookCallback] to expose typed accessors for post-execution state.
 *
 * 包装 [AfterHookCallback]，便捷读取或修改方法执行结果。
 *
 * @param original callback supplied by the runtime. | 运行时提供的回调。
 */
class AfterHookParam(private val original: AfterHookCallback) {

    /**
     * Gets the method / constructor to be hooked.
     *
     * 获取被 hook 的方法 / 构造器
     */
    val member
        get() = original.member

    /**
     * Non-null receiver instance for instance methods.
     *
     * 实例方法对应的非空 this 对象。
     *
     * @throws NullPointerException if the hooked method is static. | 如果被 Hook 的方法是静态的。
     */
    val thisObject: Any
        get() = original.thisObject
            ?: throw NullPointerException("static method should not have thisObject")

    /**
     * Receiver instance or `null` for static methods.
     *
     * 实例方法返回 this，静态方法返回 null。
     */
    val thisObjectOrNull: Any?
        get() = original.thisObject

    /**
     * Convenience cast of [thisObject] to a specific type T.
     *
     * 将 [thisObject] 快捷转换为指定类型 T。
     *
     * @throws NullPointerException if the hooked method is static (via [thisObject]). | 如果被 Hook 的方法是静态的
     * @throws ClassCastException if the object is not of type T. | 如果对象不是 T 类型。
     */
    inline fun <T> thisObjectAs(): T = thisObject as T

    /**
     * Arguments passed to the hooked method or constructor.
     *
     * 被 Hook 的方法或构造器的参数数组。
     */
    val args: Array<Any?>
        get() = original.args

    /**
     * Indicates whether before invocation skipped the original invocation.
     *
     * 指示 before 回调是否跳过原始方法执行。
     */
    val isSkipped: Boolean
        get() = original.isSkipped

    /**
     * Read or replace the method result observed after execution.
     *
     * 获取或更新方法返回值。
     *
     * @param result The return value. | 返回值。
     */
    var result: Any?
        get() = original.result
        set(value) {
            original.setResult(value)
        }

    /**
     * Read, replace, or clear the thrown exception; setting `null` suppresses it.
     *
     * 获取、替换或清除方法抛出的异常；设为 null 可阻止异常抛出。
     *
     * @param throwable The exception to be thrown. | 要抛出的异常。
     */
    var throwable: Throwable?
        get() = original.throwable
        set(value) {
            original.throwable = value
        }
}
