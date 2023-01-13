@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper.misc

import com.github.kyuubiran.ezxhelper.interfaces.IOnValueChangedEvent
import com.github.kyuubiran.ezxhelper.interfaces.IOnValueChangedThrow
import java.util.concurrent.ConcurrentHashMap

/**
 * Observe an object, when the object's value changed, the callback will be auto invoked.
 */
class Observe<T> {
    private var _value: T
    private val onValueChangedEvent = ValueChangedEvent<T>()

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

    /**
     * Set the new value for the object.
     * @param newVal new value
     */
    operator fun invoke(newVal: T) = run { this.value = newVal }

    /**
     * Set the new value for the object.
     * @param block block
     */
    operator fun invoke(block: () -> T) = run { this.value = block() }

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

    private class ValueChangedEvent<T> {
        private val _listeners = ConcurrentHashMap.newKeySet<IOnValueChangedEvent<T>>()

        var isUnsafeInvoke = false

        var onThrow: IOnValueChangedThrow<T>? = null

        fun add(listener: IOnValueChangedEvent<T>) {
            _listeners.add(listener)
        }

        fun remove(listener: IOnValueChangedEvent<T>) {
            _listeners.remove(listener)
        }

        fun clear() {
            _listeners.clear()
        }

        operator fun plusAssign(listener: IOnValueChangedEvent<T>) {
            add(listener)
        }

        operator fun minusAssign(listener: IOnValueChangedEvent<T>) {
            remove(listener)
        }

        @Throws(Throwable::class)
        fun unsafeInvoke(value: T) = _listeners.forEach {
            it.onValueChanged(value)
        }

        operator fun invoke(value: T) {
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