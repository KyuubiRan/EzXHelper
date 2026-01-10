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
    private val invocationStates = ThreadLocal<ArrayDeque<InvocationState>>()

    private class ExecutionFrame(
        val snapshot: Array<Entry>,
        val maxIndex: Int
    )

    private class InvocationState(
        private val beforeQueue: ArrayDeque<Array<Entry>>,
    ) {
        private val executedStack = ArrayDeque<ExecutionFrame>()
        var beforeActive: Int = 0
        var afterActive: Int = 0

        fun consumeNextBeforeSnapshot(): Array<Entry>? = beforeQueue.removeFirstOrNull()

        fun pushExecution(snapshot: Array<Entry>, maxIndex: Int) {
            executedStack.addLast(ExecutionFrame(snapshot, maxIndex))
        }

        fun popExecution(): ExecutionFrame? = if (executedStack.isEmpty()) null else executedStack.removeLast()

        fun markSkip() {
            beforeQueue.clear()
        }

        fun shouldRelease(): Boolean = beforeQueue.isEmpty() && executedStack.isEmpty() && beforeActive == 0 && afterActive == 0
    }

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
        val state = obtainStateForBefore() ?: return
        val snapshot = state.consumeNextBeforeSnapshot() ?: run {
            releaseStateIfDone(state)
            return
        }

        var executedIndex = -1
        val param = BeforeHookParam(callback) { state.markSkip() }
        state.beforeActive++
        try {
            for (i in snapshot.indices) {
                executedIndex = i
                val entry = snapshot[i]
                val before = entry.before
                if (before != null) {
                    try {
                        before.onMethodHooked(param)
                    } catch (_: Throwable) {
                        // Ignore callback errors to keep the chain alive.
                    }
                }
                if (param.isSkipped) {
                    break
                }
            }
        } finally {
            state.pushExecution(snapshot, executedIndex)
            state.beforeActive--
            releaseStateIfDone(state)
        }
    }

    fun dispatchAfter(callback: XposedInterface.AfterHookCallback) {
        val state = obtainStateForAfter() ?: return
        val frame = state.popExecution() ?: run {
            releaseStateIfDone(state)
            return
        }

        val execution = frame.snapshot
        if (execution.isEmpty() || frame.maxIndex < 0) {
            releaseStateIfDone(state)
            return
        }

        val param = AfterHookParam(callback)
        if (param.isSkipped) {
            state.markSkip()
        }
        state.afterActive++
        try {
            for (index in frame.maxIndex downTo 0) {
                val after = execution[index].after ?: continue
                val lastResult = param.result
                val lastThrowable = param.throwable
                try {
                    after.onMethodHooked(param)
                } catch (_: Throwable) {
                    if (lastThrowable == null) {
                        param.result = lastResult
                    } else {
                        param.throwable = lastThrowable
                    }
                }
            }
        } finally {
            state.afterActive--
            releaseStateIfDone(state)
        }
    }

    fun isEmpty(): Boolean = synchronized(lock) { buckets.isEmpty() }

    private fun obtainStateForBefore(): InvocationState? {
        val stack = invocationStates.get()
        val current = stack?.lastOrNull()
        if (current == null || current.beforeActive > 0 || current.afterActive > 0) {
            val newState = createState() ?: return null
            val targetStack = stack ?: ArrayDeque<InvocationState>().also { invocationStates.set(it) }
            targetStack.addLast(newState)
            return newState
        }
        if (current.shouldRelease()) {
            releaseState(current)
            return obtainStateForBefore()
        }
        return current
    }

    private fun obtainStateForAfter(): InvocationState? {
        val stack = invocationStates.get() ?: return null
        val current = stack.lastOrNull() ?: return null
        if (current.shouldRelease()) {
            releaseState(current)
            return null
        }
        return current
    }

    private fun createState(): InvocationState? = synchronized(lock) {
        if (buckets.isEmpty()) return null
        val snapshots = ArrayDeque<Array<Entry>>()
        for (bucket in buckets.descendingMap().values) {
            val entries = bucket.entries.toTypedArray()
            if (entries.isNotEmpty()) {
                snapshots.addLast(entries)
            }
        }
        if (snapshots.isEmpty()) return null
        InvocationState(snapshots)
    }

    private fun releaseState(state: InvocationState) {
        val stack = invocationStates.get() ?: return
        val iterator = stack.iterator()
        while (iterator.hasNext()) {
            if (iterator.next() === state) {
                iterator.remove()
                break
            }
        }
        if (stack.isEmpty()) {
            invocationStates.remove()
        }
    }

    private fun releaseStateIfDone(state: InvocationState) {
        if (state.shouldRelease()) {
            releaseState(state)
        }
    }

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
}
