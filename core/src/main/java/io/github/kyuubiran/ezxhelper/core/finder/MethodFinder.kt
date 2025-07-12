@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finder

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.finder.base.ExecutableFinder
import io.github.kyuubiran.ezxhelper.core.`interface`.IFindSuper
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Helper for finding method(s) in the class or collection.
 */
class MethodFinder private constructor(seq: Sequence<Method>) : ExecutableFinder<Method, MethodFinder>(seq), IFindSuper<MethodFinder> {

    private var clazz: Class<*>? = null

    override val name: String
        get() = "MethodFinder"

    @Suppress("ClassName")
    companion object `-Static` {

        @JvmStatic
        fun fromClass(clazz: Class<*>): MethodFinder {
            var seq = emptySequence<Method>()
            seq += clazz.declaredMethods.asSequence()
            seq += clazz.interfaces.flatMap { c -> c.declaredMethods.asSequence() }
            return MethodFinder(seq).apply {
                this.clazz = clazz
            }
        }

        @JvmSynthetic
        fun fromClass(kclazz: KClass<*>): MethodFinder = fromClass(kclazz.java)

        @JvmStatic
        fun fromClass(clazzName: String, classLoader: ClassLoader = ClassLoaderProvider.safeClassLoader) =
            fromClass(Class.forName(clazzName, false, classLoader))

        @JvmStatic
        fun fromSequence(seq: Sequence<Method>): MethodFinder {
            return MethodFinder(seq)
        }

        @JvmStatic
        fun fromArray(array: Array<Method>): MethodFinder {
            return MethodFinder(array.asSequence())
        }

        @JvmStatic
        fun fromVararg(vararg array: Method): MethodFinder {
            return MethodFinder(array.asSequence())
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Method>): MethodFinder {
            return MethodFinder(iterable.asSequence())
        }

        @JvmSynthetic
        fun Class<*>.methodFinder() = fromClass(this)

        @JvmSynthetic
        fun Array<Method>.methodFinder() = fromArray(this)

        @JvmSynthetic
        fun Iterable<Method>.methodFinder() = fromIterable(this)

        @JvmSynthetic
        fun Sequence<Method>.methodFinder() = fromSequence(this)
    }

    // region filter by

    /**
     * Filter by method name
     *
     * 过滤出名字相同的 [Method]
     *
     * @param name method name | 方法名称
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterByName(name: String) = filter { this.name == name }

    /**
     * Filter by method return type
     *
     * 过滤出具有指定返回类型的 [Method]
     *
     * @param returnType method return type | 方法返回类型
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterByReturnType(returnType: Class<*>) = filter { this.returnType == returnType }

    fun filterByReturnType(returnType: KClass<*>) = filterByReturnType(returnType.java)

    /**
     * Filter by method returns void type
     *
     * 过滤出返回类型为 void 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterVoidReturnType() = filterByReturnType(Void.TYPE)

    /**
     * DEPRECATED: Use [filterByReturnTypeExtendsFrom] instead
     *
     * Will be removed in future versions
     *
     * Filter by method assignable return type
     *
     * 过时方法: 请使用 [filterByReturnTypeExtendsFrom] 代替
     *
     * 将会在未来版本中移除
     *
     * 过滤出返回类型继承于参数类型的 [Method]
     *
     * @param returnType method return type | 方法返回类型
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    @Deprecated("Use filterByReturnTypeExtendsFrom instead", ReplaceWith("filterByReturnTypeExtendsFrom(superClass)"))
    fun filterByAssignableReturnType(returnType: Class<*>) = filter { this.returnType.isAssignableFrom(returnType) || returnType.isAssignableFrom(this.returnType) }

    /**
     * Filter by method return type extends from a specific class
     *
     * 过滤出返回类型继承自指定类的 [Method]
     *
     * e.g.
     * ```kotlin
     * fun test1() : String { return "Hello" }
     * fun test2() : CharSequence { return "World" }
     * ```
     * will be filtered by | 可以通过以下方式过滤
     * ```kotlin
     * val methods = finder.filterByReturnTypeExtendsFrom(CharSequence::class.java).toList()
     * ```
     *
     * @param superClass Class to check if the return type extends from | 要检查返回类型是否继承自的类
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterByReturnTypeExtendsFrom(superClass: Class<*>) = filter { superClass == this.returnType ||  superClass.isAssignableFrom(this.returnType) }

    @JvmSynthetic
    fun filterByReturnTypeExtendsFrom(superClass: KClass<*>) = filterByReturnTypeExtendsFrom(superClass.java)

// endregion

// region filter modifiers
    /**
     * Filter if they are abstract
     *
     * 过滤出 abstract 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterAbstract() = filter { Modifier.isAbstract(this.modifiers) }


    /**
     * Filter if they are non-abstract
     *
     * 过滤出非 abstract 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterNonAbstract() = filter { !Modifier.isAbstract(this.modifiers) }

    /**
     * Filter if they are static
     *
     * 过滤出 static 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterStatic() = filter { Modifier.isStatic(this.modifiers) }

    /**
     * Filter if they are non-static
     *
     * 过滤出非 static 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterNonStatic() = filter { !Modifier.isStatic(this.modifiers) }

    /**
     * Filter if they are final
     *
     * 过滤出 final 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterFinal() = filter { Modifier.isFinal(this.modifiers) }

    /**
     * Filter if they are non-final
     *
     * 过滤出非 final 的 [Method]
     *
     * @return [MethodFinder] new finder | 过滤后的 [MethodFinder]
     */
    fun filterNonFinal() = filter { !Modifier.isFinal(this.modifiers) }

// endregion

// region overrides

    override fun getParameterTypes(member: Method): Array<Class<*>> = member.parameterTypes
    override fun getExceptionTypes(member: Method): Array<Class<*>> = member.exceptionTypes

    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = makeNewFinder {
        var seq = sequence

        if (clazz == null) return@makeNewFinder seq

        var c: Class<*>? = clazz?.superclass ?: return@makeNewFinder seq

        while (c != null) {
            seq += c.declaredMethods.asSequence()
            seq += c.interfaces.flatMap { i -> i.declaredMethods.asSequence() }

            c = c.superclass

            if (untilPredicate != null) {
                if (untilPredicate(c)) break
            }
        }

        seq
    }

    override fun newFinder(sequence: Sequence<Method>): MethodFinder = MethodFinder(sequence)

// endregion
}