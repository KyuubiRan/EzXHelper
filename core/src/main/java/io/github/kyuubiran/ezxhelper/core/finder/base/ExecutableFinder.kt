@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finder.base

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.extension.MemberExtension
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil
import io.github.kyuubiran.ezxhelper.core.util.SignatureUtil
import java.lang.reflect.Member
import java.lang.reflect.Modifier

abstract class ExecutableFinder<E : Member, Finder>(seq: Sequence<E>) : BaseMemberFinder<E, Finder>(seq) {
    // region filter by

    /**
     * Filter by parameter types, or if null to skip check some parameters
     *
     * 过滤参数类型，或如果为 null 则跳过检查某些参数
     * @param paramTypes parameter types | 参数类型
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByParamTypes(vararg paramTypes: Class<*>?) = filter f@{
        val pt = getParameterTypes(this@f)
        if (pt.size != paramTypes.size) return@f false

        for (i in pt.indices) {
            val clz1 = pt[i]
            val clz2 = paramTypes[i] ?: continue
            if (clz1 != clz2) return@f false
        }

        true
    }

    /**
     * Filter by parameter types or subclass of types, or if null to skip check some parameters
     *
     * 过滤参数类型或子类类型，或如果为 null 则跳过检查某些参数
     * @param paramTypes parameter types | 参数类型
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByAssignableParamTypes(vararg paramTypes: Class<*>?) = filter f@{
        val pt = getParameterTypes(this@f)
        if (pt.size != paramTypes.size) return@f false

        for (i in pt.indices) {
            val clz1 = pt[i]
            val clz2 = paramTypes[i] ?: continue
            if (clz1.isAssignableFrom(clz2)) continue
            if (ClassUtil.isPrimitiveTypeMatch(clz1, clz2)) continue
            if (clz1 != clz2) return@f false
        }

        true
    }

    /**
     * Filter by parameter signature, e.g. "IIIZ" for `(int, int, int, boolean)`
     *
     * 过滤参数签名 例如 "IIIZ" 表示 `(int, int, int, boolean)`
     * @param sig parameter signature | 参数签名
     * @param cl class loader to use for resolving types, default is [ClassLoaderProvider.safeClassLoader] | 用于解析类型的类加载器，默认为 [ClassLoaderProvider.safeClassLoader]
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByParamSignature(sig: String, cl: ClassLoader = ClassLoaderProvider.safeClassLoader) = filter {
        val sigTypes = SignatureUtil.signatureToTypes(sig, cl)
        val paramTypes = getParameterTypes(this)
        if (sigTypes.isEmpty() && paramTypes.isEmpty()) return@filter true
        if (sigTypes.size != paramTypes.size) return@filter false
        sigTypes.indices.all { i ->
            val clz1 = paramTypes[i]
            val clz2 = sigTypes[i]
            clz1 == clz2 || ClassUtil.isPrimitiveTypeMatch(clz1, clz2)
        }
    }

    /**
     * Filter the executable if the parameter is empty
     *
     * 过滤出参数为空的可执行方法/构造器
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterEmptyParam() = filter { getParameterTypes(this).isEmpty() }

    /**
     * Filter the executable if the parameter is not empty
     *
     * 过滤出参数不为空的可执行方法/构造器
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterNotEmptyParam() = filter { getParameterTypes(this).isNotEmpty() }

    /**
     * Use condition to filter parameter types
     *
     * 过滤参数类型
     * @param predicate condition | 条件
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByParamTypes(predicate: (Array<Class<*>>) -> Boolean) = filter { predicate(getParameterTypes(this)) }


    /**
     * Filter by parameter count
     *
     * 过滤参数个数
     * @param count parameter count | 参数个数
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByParamCount(count: Int) = filter { getParameterTypes(this).size == count }

    /**
     * Use condition to filter parameter count
     *
     * 通过条件过滤参数个数
     * @param predicate condition | 条件
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByParamCount(predicate: (Int) -> Boolean) = filter { predicate(getParameterTypes(this).size) }

    /**
     * Filter by parameter count in range
     *
     * 通过范围过滤参数个数
     * @param range parameter count range | 参数个数范围
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByParamCount(range: IntRange) = filter { getParameterTypes(this).size in range }

    /**
     * Filter by exception types
     *
     * 过滤异常类型，用于 Java 方法带有 throws Exception 的情况
     * @param exceptionTypes exception types | 异常类型
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterByExceptionTypes(vararg exceptionTypes: Class<*>) = exceptionTypes.toSet().let { set ->
        filter { getExceptionTypes(this).run { size == set.size && toSet() == set } }
    }

    // endregion

    // region filter modifiers

    /**
     * Filter if they are native
     *
     * 过滤出 native 的方法
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterNative() = filter { Modifier.isNative(modifiers) }

    /**
     * Filter if they are non-native
     *
     * 过滤出非 native 的方法
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterNonNative() = filter { !Modifier.isNative(modifiers) }

    /**
     * Filter if they are varargs
     *
     * 过滤出 varargs 的方法
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterVarargs() = filter { modifiers and MemberExtension.VARARGS != 0 }

    /**
     * Filter if they are non-varargs
     *
     * 过滤出非 varargs
     * @return [Finder] new finder | 过滤后的 [Finder]
     */
    fun filterNonVarargs() = filter { modifiers and MemberExtension.VARARGS == 0 }

    // endregion

    // region abstracts

    protected abstract fun getParameterTypes(member: E): Array<Class<*>>
    protected abstract fun getExceptionTypes(member: E): Array<Class<*>>

    // endregion
}