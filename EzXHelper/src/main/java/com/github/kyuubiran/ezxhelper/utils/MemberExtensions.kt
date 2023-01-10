@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.interfaces.IXposedScope
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

context (IXposedScope)
object MemberExtensions {
    /**
     * 扩展属性 判断是否为Static
     */
    val Method.isStatic: Boolean
        inline get() = Modifier.isStatic(this.modifiers)
    val Method.isNotStatic: Boolean
        inline get() = !this.isStatic

    val Field.isStatic: Boolean
        inline get() = Modifier.isStatic(this.modifiers)
    val Field.isNotStatic: Boolean
        inline get() = !this.isStatic

    val Class<*>.isStatic: Boolean
        inline get() = Modifier.isStatic(this.modifiers)
    val Class<*>.isNotStatic: Boolean
        inline get() = !this.isStatic

    /**
     * 扩展属性 判断是否为Public
     */
    val Member.isPublic: Boolean
        inline get() = Modifier.isPublic(this.modifiers)
    val Member.isNotPublic: Boolean
        inline get() = !this.isPublic

    val Class<*>.isPublic: Boolean
        inline get() = Modifier.isPublic(this.modifiers)
    val Class<*>.isNotPublic: Boolean
        inline get() = !this.isPublic

    /**
     * 扩展属性 判断是否为Protected
     */
    val Member.isProtected: Boolean
        inline get() = Modifier.isProtected(this.modifiers)
    val Member.isNotProtected: Boolean
        inline get() = !this.isProtected

    val Class<*>.isProtected: Boolean
        inline get() = Modifier.isProtected(this.modifiers)
    val Class<*>.isNotProtected: Boolean
        inline get() = !this.isProtected

    /**
     * 扩展属性 判断是否为Private
     */
    val Member.isPrivate: Boolean
        inline get() = Modifier.isPrivate(this.modifiers)
    val Member.isNotPrivate: Boolean
        inline get() = !this.isPrivate

    val Class<*>.isPrivate: Boolean
        inline get() = Modifier.isPrivate(this.modifiers)
    val Class<*>.isNotPrivate: Boolean
        inline get() = !this.isPrivate

    /**
     * 扩展属性 判断是否为Package-Private
     */
    val Member.isPackagePrivate: Boolean
        inline get() = this.isNotPublic && this.isNotProtected && this.isNotPrivate
    val Member.isNotPackagePrivate: Boolean
        inline get() = !this.isPackagePrivate

    val Class<*>.isPackagePrivate: Boolean
        inline get() = this.isNotPublic && this.isNotProtected && this.isNotPrivate
    val Class<*>.isNotPackagePrivate: Boolean
        inline get() = !this.isPackagePrivate

    /**
     * 扩展属性 判断是否为Final
     */
    val Method.isFinal: Boolean
        inline get() = Modifier.isFinal(this.modifiers)
    val Method.isNotFinal: Boolean
        inline get() = !this.isFinal

    val Field.isFinal: Boolean
        inline get() = Modifier.isFinal(this.modifiers)
    val Field.isNotFinal: Boolean
        inline get() = !this.isFinal

    val Class<*>.isFinal: Boolean
        inline get() = Modifier.isFinal(this.modifiers)
    val Class<*>.isNotFinal: Boolean
        inline get() = !this.isFinal

    /**
     * 扩展属性 判断是否为Native
     */
    val Method.isNative: Boolean
        inline get() = Modifier.isNative(this.modifiers)
    val Method.isNotNative: Boolean
        inline get() = !this.isNative

    val Constructor<*>.isNative: Boolean
        inline get() = Modifier.isNative(this.modifiers)
    val Constructor<*>.isNotNative: Boolean
        inline get() = !this.isNative

    /**
     * 扩展属性 判断是否为Synchronized
     */
    val Method.isSynchronized: Boolean
        inline get() = Modifier.isSynchronized(this.modifiers)
    val Method.isNotSynchronized: Boolean
        inline get() = !this.isSynchronized

    /**
     * 扩展属性 判断是否为Abstract
     */
    val Method.isAbstract: Boolean
        inline get() = Modifier.isAbstract(this.modifiers)
    val Method.isNotAbstract: Boolean
        inline get() = !this.isAbstract

    val Class<*>.isAbstract: Boolean
        inline get() = Modifier.isAbstract(this.modifiers)
    val Class<*>.isNotAbstract: Boolean
        inline get() = !this.isAbstract

    /**
     * 扩展属性 判断是否为Transient
     */
    val Field.isTransient: Boolean
        inline get() = Modifier.isTransient(this.modifiers)
    val Field.isNotTransient: Boolean
        inline get() = !this.isTransient

    /**
     * 扩展属性 判断是否为Volatile
     */
    val Field.isVolatile: Boolean
        inline get() = Modifier.isVolatile(this.modifiers)
    val Field.isNotVolatile: Boolean
        inline get() = !this.isVolatile

    // Modifier.VARARGS = 0x0080

    /**
     * 扩展属性 判断是否为Varargs
     */
    val Method.isVarargs: Boolean
        inline get() = (this.modifiers and 0x0080) != 0
    val Method.isNotVarargs: Boolean
        inline get() = !this.isVarargs

    val Constructor<*>.isVarargs: Boolean
        inline get() = (this.modifiers and 0x0080) != 0
    val Constructor<*>.isNotVarargs: Boolean
        inline get() = !this.isVarargs

    /**
     * 扩展属性 获取方法的参数数量
     */
    val Method.paramCount: Int
        inline get() = this.parameterTypes.size

    /**
     * 扩展属性 获取构造方法的参数数量
     */
    val Constructor<*>.paramCount: Int
        inline get() = this.parameterTypes.size

    /**
     * 扩展属性 判断方法的参数是否为空
     */
    val Method.isEmptyParam: Boolean
        inline get() = this.paramCount == 0
    val Method.isNotEmptyParam: Boolean
        inline get() = this.paramCount != 0

    /**
     * 扩展属性 判断构造方法的参数是否为空
     */
    val Constructor<*>.isEmptyParam: Boolean
        inline get() = this.paramCount == 0
    val Constructor<*>.isNotEmptyParam: Boolean
        inline get() = this.paramCount != 0

}