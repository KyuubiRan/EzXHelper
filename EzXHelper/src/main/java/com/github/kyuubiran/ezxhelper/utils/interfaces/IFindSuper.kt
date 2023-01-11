package com.github.kyuubiran.ezxhelper.utils.interfaces

interface IFindSuper<Self> {
    fun findSuper(untilPredicate: (Class<*>.() -> Boolean)? = null): Self
}