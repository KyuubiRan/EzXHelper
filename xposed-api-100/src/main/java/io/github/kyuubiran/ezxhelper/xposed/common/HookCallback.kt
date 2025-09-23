package io.github.kyuubiran.ezxhelper.xposed.common

import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodAfterHookCallback
import io.github.kyuubiran.ezxhelper.xposed.interfaces.IMethodBeforeHookCallback
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Member
import java.util.TreeMap
import java.util.concurrent.CopyOnWriteArrayList

internal class HookCallback private constructor(
    private val hookerFactory: (priority: Int) -> XposedInterface.MethodUnhooker<out Member>,
    private val target: Member,
) {

    private data class Entry(
        val before: IMethodBeforeHookCallback?,
        val after: IMethodAfterHookCallback?,
    )

    private data class Bucket(
        val priority: Int,
        val entries: CopyOnWriteArrayList<Entry>,
        val unhooker: XposedInterface.MethodUnhooker<out Member>,
    )

    private val buckets = TreeMap<Int, Bucket>()
    private val lock = Any()
    private val beforeOrder = ThreadLocal<ArrayDeque<Int>?>()
    private val afterOrder = ThreadLocal<ArrayDeque<Int>?>()

    fun register(
        priority: Int,
        before: IMethodBeforeHookCallback?,
        after: IMethodAfterHookCallback?,
        onEmpty: (priority: Int) -> Unit,
    ): XposedInterface.MethodUnhooker<Member> {
        require(before != null || after != null) { "No hook callback specified" }
        val entry = Entry(before, after)

        synchronized(lock) {
            buckets[priority]?.let { bucket ->
                bucket.entries.add(entry)
                return object : XposedInterface.MethodUnhooker<Member> {
                    override fun getOrigin(): Member = target

                    override fun unhook() {
                        val notifyPriority = synchronized(lock) {
                            val current = buckets[priority] ?: return@synchronized null
                            current.entries.remove(entry)
                            if (current.entries.isEmpty()) {
                                buckets.remove(priority)
                                current.unhooker.unhookQuietly()
                                priority
                            } else {
                                null
                            }
                        }
                        if (notifyPriority != null) {
                            onEmpty(notifyPriority)
                        }
                    }
                }
            }

            val newBucket = Bucket(
                priority = priority,
                entries = CopyOnWriteArrayList<Entry>().apply { add(entry) },
                unhooker = hookerFactory(priority),
            )
            buckets[priority] = newBucket
        }

        return object : XposedInterface.MethodUnhooker<Member> {
            override fun getOrigin(): Member = target

            override fun unhook() {
                val notifyPriority = synchronized(lock) {
                    val current = buckets[priority] ?: return@synchronized null
                    current.entries.remove(entry)
                    if (current.entries.isEmpty()) {
                        buckets.remove(priority)
                        current.unhooker.unhookQuietly()
                        priority
                    } else {
                        null
                    }
                }
                if (notifyPriority != null) {
                    onEmpty(notifyPriority)
                }
            }
        }
    }

    fun dispatchBefore(callback: XposedInterface.BeforeHookCallback) {
        while (true) {
            val priority = nextBeforePriority() ?: return
            val entries = synchronized(lock) {
                buckets[priority]?.entries?.toTypedArray()
            } ?: continue
            if (entries.isEmpty()) continue
            entries.forEach { it.before?.onMethodHooked(BeforeHookParam(callback)) }
            return
        }
    }

    fun dispatchAfter(callback: XposedInterface.AfterHookCallback) {
        while (true) {
            val priority = nextAfterPriority() ?: return
            val entries = synchronized(lock) {
                buckets[priority]?.entries?.toTypedArray()
            } ?: continue
            if (entries.isEmpty()) continue
            for (index in entries.indices.reversed()) {
                entries[index].after?.onMethodHooked(AfterHookParam(callback))
            }
            return
        }
    }

    fun isEmpty(): Boolean = synchronized(lock) { buckets.isEmpty() }

    private fun XposedInterface.MethodUnhooker<out Member>?.unhookQuietly() {
        this ?: return
        try {
            unhook()
        } catch (_: Throwable) {
            // Ignore errors while best-effort releasing the hook.
        }
    }

    companion object {
        fun forMember(member: Member, hooker: (priority: Int) -> XposedInterface.MethodUnhooker<out Member>): HookCallback {
            return HookCallback(hooker, member)
        }
    }

    private fun nextBeforePriority(): Int? {
        var queue = beforeOrder.get()
        if (queue == null || queue.isEmpty()) {
            queue = synchronized(lock) {
                if (buckets.isEmpty()) {
                    ArrayDeque()
                } else {
                    ArrayDeque<Int>().apply { addAll(buckets.descendingKeySet()) }
                }
            }
            if (queue.isEmpty()) {
                beforeOrder.remove()
                return null
            }
            beforeOrder.set(queue)
        }
        val priority = queue.removeFirstOrNull()
        if (queue.isEmpty()) {
            beforeOrder.remove()
        }
        return priority
    }

    private fun nextAfterPriority(): Int? {
        var queue = afterOrder.get()
        if (queue == null || queue.isEmpty()) {
            queue = synchronized(lock) {
                if (buckets.isEmpty()) {
                    ArrayDeque()
                } else {
                    ArrayDeque<Int>().apply { addAll(buckets.keys) }
                }
            }
            if (queue.isEmpty()) {
                afterOrder.remove()
                return null
            }
            afterOrder.set(queue)
        }
        val priority = queue.removeFirstOrNull()
        if (queue.isEmpty()) {
            afterOrder.remove()
        }
        return priority
    }
}
