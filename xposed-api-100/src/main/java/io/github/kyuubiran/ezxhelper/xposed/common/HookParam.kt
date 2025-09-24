package io.github.kyuubiran.ezxhelper.xposed.common

import io.github.libxposed.api.XposedInterface

class BeforeHookParam(
    private val original: XposedInterface.BeforeHookCallback,
    private val onSkip: () -> Unit = {},
) {

    private var skipped: Boolean = false

    val isSkipped: Boolean
        get() = skipped

    private fun markSkipped() {
        if (!skipped) {
            skipped = true
            onSkip()
        }
    }

    val thisObject: Any
        get() = original.thisObject
            ?: throw IllegalStateException("Expected non-null thisObject but got null")

    val thisObjectOrNull: Any?
        get() = original.thisObject

    val args: Array<Any?>
        get() = original.args

    var result: Any?
        get() = null
        set(value) {
            markSkipped()
            original.returnAndSkip(value)
        }

    var throwable: Throwable?
        get() = null
        set(value) {
            markSkipped()
            original.throwAndSkip(value)
        }
}

class AfterHookParam(private val original: XposedInterface.AfterHookCallback) {

    val thisObject: Any
        get() = original.thisObject
            ?: throw IllegalStateException("Expected non-null thisObject but got null")

    val thisObjectOrNull: Any?
        get() = original.thisObject

    val args: Array<Any?>
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
