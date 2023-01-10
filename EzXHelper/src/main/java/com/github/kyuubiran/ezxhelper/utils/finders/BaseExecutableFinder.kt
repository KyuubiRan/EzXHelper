@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.utils.finders

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope
import java.lang.reflect.Member
import java.lang.reflect.Modifier

context (IXposedScope)
@Suppress("UNCHECKED_CAST", "unused")
abstract class BaseExecutableFinder<T, S> internal constructor(protected var memberSequence: Sequence<T>) where T : Member {
    protected inline fun applyThis(block: BaseExecutableFinder<T, S>.() -> Unit) = this.apply { block() } as S

    fun firstOrNull(): T? = memberSequence.firstOrNull()

    @Throws(NoSuchElementException::class)
    fun first(): T = memberSequence.first()

    // #region contact
    fun contact(other: BaseExecutableFinder<T, S>): S = applyThis {
        memberSequence += other.memberSequence
    }

    fun contact(other: Sequence<T>): S = applyThis {
        memberSequence += other
    }

    fun contact(other: Array<T>): S = applyThis {
        memberSequence += other.asSequence()
    }

    fun contact(other: Iterable<T>): S = applyThis {
        memberSequence += other.asSequence()
    }

    operator fun plus(other: BaseExecutableFinder<T, S>) = contact(other)
    operator fun plus(other: Sequence<T>) = contact(other)
    operator fun plus(other: Array<T>) = contact(other)
    operator fun plus(other: Iterable<T>) = contact(other)

    operator fun plusAssign(other: BaseExecutableFinder<T, S>) {
        contact(other)
    }

    operator fun plusAssign(other: Sequence<T>) {
        contact(other)
    }

    operator fun plusAssign(other: Array<T>) {
        contact(other)
    }

    operator fun plusAssign(other: Iterable<T>) {
        contact(other)
    }
    // #endregion

    fun filter(filter: T.() -> Boolean): S = applyThis {
        memberSequence.filter(filter)
    }

    // #region filter by
    fun filterByParamTypes(vararg paramTypes: Class<*>?): S = applyThis {
        memberSequence.filter f@{
            val pt = getParameterTypes(it)
            if (pt.size != paramTypes.size) return@f false

            for (i in pt.indices) {
                if (paramTypes[i] == null) continue
                if (pt[i] != paramTypes[i]) return@f false
            }

            true
        }
    }

    fun filterByParamTypes(predicate: (Array<Class<*>>) -> Boolean): S = applyThis {
        memberSequence.filter { predicate(getParameterTypes(it)) }
    }

    fun filterByParamCount(count: Int): S = applyThis {
        memberSequence.filter { getParameterTypes(it).size == count }
    }

    fun filterByParamCount(predicate: (Int) -> Boolean): S = applyThis {
        memberSequence.filter { predicate(getParameterTypes(it).size) }
    }

    fun filterByParamCount(range: IntRange) = applyThis {
        memberSequence.filter { getParameterTypes(it).size in range }
    }

    fun filterByExceptionTypes(vararg exceptionTypes: Class<*>): S = applyThis {
        val set = exceptionTypes.toSet()
        memberSequence.filter { getExceptionTypes(it).toSet() == set }
    }
    // #endregion

    // #region filter modifiers
    fun filterByModifiers(modifiers: Int): S = applyThis {
        memberSequence.filter { it.modifiers == modifiers }
    }

    fun filterByModifiers(predicate: (modifiers: Int) -> Boolean): S = applyThis {
        memberSequence.filter { predicate(it.modifiers) }
    }

    fun filterIncludeModifiers(modifiers: Int): S = applyThis {
        memberSequence.filter { (it.modifiers and modifiers) != 0 }
    }

    fun filterExcludeModifiers(modifiers: Int): S = applyThis {
        memberSequence.filter { (it.modifiers and modifiers) == 0 }
    }

    fun filterPublic() = filterIncludeModifiers(Modifier.PUBLIC)
    fun filterNonPublic() = filterExcludeModifiers(Modifier.PUBLIC)

    fun filterProtected() = filterIncludeModifiers(Modifier.PROTECTED)
    fun filterNonProtected() = filterExcludeModifiers(Modifier.PROTECTED)

    fun filterPrivate() = filterIncludeModifiers(Modifier.PRIVATE)
    fun filterNonPrivate() = filterExcludeModifiers(Modifier.PRIVATE)

    fun filterPackagePrivate() = filterExcludeModifiers(Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE)
    fun filterNonPackagePrivate() = filterExcludeModifiers(Modifier.PUBLIC or Modifier.PROTECTED or Modifier.PRIVATE)

    fun filterNative() = filterIncludeModifiers(Modifier.NATIVE)
    fun filterNonNative() = filterExcludeModifiers(Modifier.NATIVE)

    fun filterVarargs() = filterIncludeModifiers(0x0080)
    fun filterNonVarargs() = filterExcludeModifiers(0x0080)
    // #endregion

    // #region for-each
    fun onEach(action: (T) -> Unit): S = applyThis { memberSequence.onEach(action) }
    fun onEachIndexed(action: (index: Int, T) -> Unit): S = applyThis { memberSequence.onEachIndexed(action) }
    fun forEach(action: (T) -> Unit) = memberSequence.forEach(action)
    fun forEachIndexed(action: (index: Int, T) -> Unit) = memberSequence.forEachIndexed(action)
    // #endregion

    // #region mapTo
    fun <R> mapToList(transform: (T) -> R): List<R> = memberSequence.map(transform).toList()
    fun <R> mapToMutableList(transform: (T) -> R): List<R> = memberSequence.map(transform).toMutableList()
    fun <R> mapToSet(transform: (T) -> R): Set<R> = memberSequence.map(transform).toSet()
    fun <R> mapToMutableSet(transform: (T) -> R): MutableSet<R> = memberSequence.map(transform).toMutableSet()
    fun <R> mapToHashSet(transform: (T) -> R): HashSet<R> = memberSequence.map(transform).toHashSet()
    fun <R, C> mapToCollection(destination: C, transform: (T) -> R): C where C : MutableCollection<in R> =
        memberSequence.map(transform).toCollection(destination)
    // #endregion

    // #region toCollection
    fun toList(): List<T> = memberSequence.toList()
    fun toMutableList(): MutableList<T> = memberSequence.toMutableList()
    fun toSet(): Set<T> = memberSequence.toSet()
    fun toMutableSet(): MutableSet<T> = memberSequence.toMutableSet()
    fun toHashSet(): HashSet<T> = memberSequence.toHashSet()
    fun <C> toCollection(collection: C): C where C : MutableCollection<T> = memberSequence.toCollection(collection)
    // #endregion

    // #region abstract
    protected abstract fun getParameterTypes(member: T): Array<Class<*>>
    protected abstract fun getExceptionTypes(member: T): Array<Class<*>>
    // #endregion
}