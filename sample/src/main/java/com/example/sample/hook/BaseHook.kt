package com.example.sample.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
}