package io.github.kyuubiran.ezxhelper.xposed.interfaces

import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Member

class BeforeHookParam(private val original: XposedInterface.BeforeHookCallback) {
    val member: Member
        get() = original.member

    val thisObject: Any
        get() = original.thisObject
            ?: throw IllegalStateException("Expected non-null thisObject but got null")

    val thisObjectOrNull: Any?
        get() = original.thisObject

    val args: Array<Any>
        get() = original.args.map {
            it ?: throw IllegalStateException("Expected non-null argument but got null in args array")
        }.toTypedArray()

    val argsOrNull: Array<Any?>
        get() = original.args

    var result: Any?
        get() = null
        set(value) {
            original.returnAndSkip(value)
        }

    var throwable: Throwable?
        get() = null
        set(value) {
            original.throwAndSkip(value)
        }
}

class AfterHookParam(private val original: XposedInterface.AfterHookCallback) {
    val member: Member
        get() = original.member

    val thisObject: Any
        get() = original.thisObject
            ?: throw IllegalStateException("Expected non-null thisObject but got null")

    val thisObjectOrNull: Any?
        get() = original.thisObject

    val args: Array<Any>
        get() = original.args.map {
            it ?: throw IllegalStateException("Expected non-null argument but got null in args array")
        }.toTypedArray()

    val argsOrNull: Array<Any?>
        get() = original.args

    val isSkipped: Boolean
        get() = original.isSkipped

    var result: Any?
        get() = original.result
        set(value) {
            original.setResult(value)
        }

    var throwable: Throwable?
        get() = original.throwable
        set(value) {
            original.throwable = value
        }
}

fun interface IMethodBeforeHookCallback {
    fun onMethodHooked(param: BeforeHookParam)
}

fun interface IMethodAfterHookCallback {
    fun onMethodHooked(param: AfterHookParam)
}