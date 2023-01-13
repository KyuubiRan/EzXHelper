@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.utils.interfaces.IOnValueChangedThrow
import com.github.kyuubiran.ezxhelper.utils.interfaces.IOnValueChangedEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * 监听一个对象，当值发生变化时调用 onValueChanged 中所有回调
 */
class Observe<T> {
    private var _value: T
    val onValueChangedEvent = ValueChangedEvent<T>()

    constructor(init: T, onValueChanged: IOnValueChangedEvent<T>? = null) {
        _value = init
        if (onValueChanged != null) this.onValueChangedEvent += onValueChanged
    }

    constructor(init: T, onValueChanged: ((T) -> Unit)? = null) {
        _value = init
        if (onValueChanged != null) this.onValueChangedEvent += onValueChanged
    }

    constructor(init: T) {
        _value = init
    }

    var isUnsafeInvoke
        set(value) = run { onValueChangedEvent.isUnsafeInvoke = value }
        get() = onValueChangedEvent.isUnsafeInvoke

    fun setOnThrow(onThrow: IOnValueChangedThrow<T>?) = this.apply {
        onValueChangedEvent.onThrow = onThrow
    }

    fun useUnsafeInvoke() = this.apply { isUnsafeInvoke = true }

    fun addListener(listener: IOnValueChangedEvent<T>) = this.apply {
        onValueChangedEvent += listener
    }

    fun removeListener(listener: IOnValueChangedEvent<T>) = this.apply {
        onValueChangedEvent -= listener
    }

    fun clearListener() = this.apply {
        onValueChangedEvent.clear()
    }

    operator fun plusAssign(listener: IOnValueChangedEvent<T>) {
        addListener(listener)
    }

    operator fun plusAssign(listener: (T) -> Unit) {
        addListener(listener)
    }

    operator fun minusAssign(listener: IOnValueChangedEvent<T>) {
        removeListener(listener)
    }

    operator fun minusAssign(listener: (T) -> Unit) {
        removeListener(listener)
    }

    var value: T
        get() = _value
        set(newValue) = synchronized(this) {
            if (_value == newValue) return@synchronized
            _value = newValue
            if (isUnsafeInvoke)
                onValueChangedEvent.unsafeInvoke(newValue)
            else
                onValueChangedEvent.invoke(newValue)
        }

    class ValueChangedEvent<T> {
        private val _listeners = ConcurrentHashMap.newKeySet<IOnValueChangedEvent<T>>()

        internal var isUnsafeInvoke = false

        internal var onThrow: IOnValueChangedThrow<T>? = null

        internal fun add(listener: IOnValueChangedEvent<T>) {
            _listeners.add(listener)
        }

        internal fun remove(listener: IOnValueChangedEvent<T>) {
            _listeners.remove(listener)
        }

        internal fun clear() {
            _listeners.clear()
        }

        internal operator fun plusAssign(listener: IOnValueChangedEvent<T>) {
            add(listener)
        }

        internal operator fun minusAssign(listener: IOnValueChangedEvent<T>) {
            remove(listener)
        }

        internal fun unsafeInvoke(value: T) {
            _listeners.forEach {
                it.onValueChanged(value)
            }
        }

        internal operator fun invoke(value: T) {
            for (listener in _listeners) {
                try {
                    listener.onValueChanged(value)
                } catch (thr: Throwable) {
                    onThrow?.onThrow(thr, value, listener)
                }
            }
        }
    }
}