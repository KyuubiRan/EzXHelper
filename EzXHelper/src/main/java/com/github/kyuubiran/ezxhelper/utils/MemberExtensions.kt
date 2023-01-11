@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.utils

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object MemberExtensions {
    /**
     * 扩展属性 判断是否为Static
     */
    val Method.isStatic: Boolean
        @JvmStatic inline get() = Modifier.isStatic(this.modifiers)
    val Method.isNotStatic: Boolean
        @JvmStatic inline get() = !this.isStatic

    val Field.isStatic: Boolean
        @JvmStatic inline get() = Modifier.isStatic(this.modifiers)
    val Field.isNotStatic: Boolean
        @JvmStatic inline get() = !this.isStatic

    val Class<*>.isStatic: Boolean
        @JvmStatic inline get() = Modifier.isStatic(this.modifiers)
    val Class<*>.isNotStatic: Boolean
        @JvmStatic inline get() = !this.isStatic

    /**
     * 扩展属性 判断是否为Public
     */
    val Member.isPublic: Boolean
        @JvmStatic inline get() = Modifier.isPublic(this.modifiers)
    val Member.isNotPublic: Boolean
        @JvmStatic inline get() = !this.isPublic

    val Class<*>.isPublic: Boolean
        @JvmStatic inline get() = Modifier.isPublic(this.modifiers)
    val Class<*>.isNotPublic: Boolean
        @JvmStatic inline get() = !this.isPublic

    /**
     * 扩展属性 判断是否为Protected
     */
    val Member.isProtected: Boolean
        @JvmStatic inline get() = Modifier.isProtected(this.modifiers)
    val Member.isNotProtected: Boolean
        @JvmStatic inline get() = !this.isProtected

    val Class<*>.isProtected: Boolean
        @JvmStatic inline get() = Modifier.isProtected(this.modifiers)
    val Class<*>.isNotProtected: Boolean
        @JvmStatic inline get() = !this.isProtected

    /**
     * 扩展属性 判断是否为Private
     */
    val Member.isPrivate: Boolean
        @JvmStatic inline get() = Modifier.isPrivate(this.modifiers)
    val Member.isNotPrivate: Boolean
        @JvmStatic inline get() = !this.isPrivate

    val Class<*>.isPrivate: Boolean
        @JvmStatic inline get() = Modifier.isPrivate(this.modifiers)
    val Class<*>.isNotPrivate: Boolean
        @JvmStatic inline get() = !this.isPrivate

    /**
     * 扩展属性 判断是否为Package-Private
     */
    val Member.isPackagePrivate: Boolean
        @JvmStatic inline get() = this.isNotPublic && this.isNotProtected && this.isNotPrivate
    val Member.isNotPackagePrivate: Boolean
        @JvmStatic inline get() = !this.isPackagePrivate

    val Class<*>.isPackagePrivate: Boolean
        @JvmStatic inline get() = this.isNotPublic && this.isNotProtected && this.isNotPrivate
    val Class<*>.isNotPackagePrivate: Boolean
        @JvmStatic inline get() = !this.isPackagePrivate

    /**
     * 扩展属性 判断是否为Final
     */
    val Method.isFinal: Boolean
        @JvmStatic inline get() = Modifier.isFinal(this.modifiers)
    val Method.isNotFinal: Boolean
        @JvmStatic inline get() = !this.isFinal

    val Field.isFinal: Boolean
        @JvmStatic inline get() = Modifier.isFinal(this.modifiers)
    val Field.isNotFinal: Boolean
        @JvmStatic inline get() = !this.isFinal

    val Class<*>.isFinal: Boolean
        @JvmStatic inline get() = Modifier.isFinal(this.modifiers)
    val Class<*>.isNotFinal: Boolean
        @JvmStatic inline get() = !this.isFinal

    /**
     * 扩展属性 判断是否为Native
     */
    val Method.isNative: Boolean
        @JvmStatic inline get() = Modifier.isNative(this.modifiers)
    val Method.isNotNative: Boolean
        @JvmStatic inline get() = !this.isNative

    val Constructor<*>.isNative: Boolean
        @JvmStatic inline get() = Modifier.isNative(this.modifiers)
    val Constructor<*>.isNotNative: Boolean
        @JvmStatic inline get() = !this.isNative

    /**
     * 扩展属性 判断是否为Synchronized
     */
    val Method.isSynchronized: Boolean
        @JvmStatic inline get() = Modifier.isSynchronized(this.modifiers)
    val Method.isNotSynchronized: Boolean
        @JvmStatic inline get() = !this.isSynchronized

    /**
     * 扩展属性 判断是否为Abstract
     */
    val Method.isAbstract: Boolean
        @JvmStatic inline get() = Modifier.isAbstract(this.modifiers)
    val Method.isNotAbstract: Boolean
        @JvmStatic inline get() = !this.isAbstract

    val Class<*>.isAbstract: Boolean
        @JvmStatic inline get() = Modifier.isAbstract(this.modifiers)
    val Class<*>.isNotAbstract: Boolean
        @JvmStatic inline get() = !this.isAbstract

    /**
     * 扩展属性 判断是否为Transient
     */
    val Field.isTransient: Boolean
        @JvmStatic inline get() = Modifier.isTransient(this.modifiers)
    val Field.isNotTransient: Boolean
        @JvmStatic inline get() = !this.isTransient

    /**
     * 扩展属性 判断是否为Volatile
     */
    val Field.isVolatile: Boolean
        @JvmStatic inline get() = Modifier.isVolatile(this.modifiers)
    val Field.isNotVolatile: Boolean
        @JvmStatic inline get() = !this.isVolatile

    // Modifier.VARARGS = 0x0080

    /**
     * 扩展属性 判断是否为Varargs
     */
    val Method.isVarargs: Boolean
        @JvmStatic inline get() = (this.modifiers and 0x0080) != 0
    val Method.isNotVarargs: Boolean
        @JvmStatic inline get() = !this.isVarargs

    val Constructor<*>.isVarargs: Boolean
        @JvmStatic inline get() = (this.modifiers and 0x0080) != 0
    val Constructor<*>.isNotVarargs: Boolean
        @JvmStatic inline get() = !this.isVarargs

    /**
     * 扩展属性 获取方法的参数数量
     */
    val Method.paramCount: Int
        @JvmStatic inline get() = this.parameterTypes.size

    /**
     * 扩展属性 获取构造方法的参数数量
     */
    val Constructor<*>.paramCount: Int
        @JvmStatic inline get() = this.parameterTypes.size

    /**
     * 扩展属性 判断方法的参数是否为空
     */
    val Method.isEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount == 0
    val Method.isNotEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount != 0

    /**
     * 扩展属性 判断构造方法的参数是否为空
     */
    val Constructor<*>.isEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount == 0
    val Constructor<*>.isNotEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount != 0

}