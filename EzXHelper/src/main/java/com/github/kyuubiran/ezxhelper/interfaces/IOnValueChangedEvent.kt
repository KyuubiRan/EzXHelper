package com.github.kyuubiran.ezxhelper.interfaces

fun interface IOnValueChangedEvent<T> {
    fun onValueChanged(changedValue: T)
}

fun interface IOnValueChangedThrow<T> {
    fun onThrow(thr: Throwable, obj: T, handler: IOnValueChangedEvent<T>)
}