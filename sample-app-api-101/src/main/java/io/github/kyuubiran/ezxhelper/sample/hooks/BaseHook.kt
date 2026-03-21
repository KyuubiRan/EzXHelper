package io.github.kyuubiran.ezxhelper.sample.hooks

abstract class BaseHook {
    abstract fun init()
    abstract val name: String
    var isInit: Boolean = false
}