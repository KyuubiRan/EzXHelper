@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * The extensions for [Member] to quick check modifiers or do something easily
 */
object MemberExtensions {
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


    val Member.isPublic: Boolean
        @JvmStatic inline get() = Modifier.isPublic(this.modifiers)
    val Member.isNotPublic: Boolean
        @JvmStatic inline get() = !this.isPublic

    val Class<*>.isPublic: Boolean
        @JvmStatic inline get() = Modifier.isPublic(this.modifiers)
    val Class<*>.isNotPublic: Boolean
        @JvmStatic inline get() = !this.isPublic


    val Member.isProtected: Boolean
        @JvmStatic inline get() = Modifier.isProtected(this.modifiers)
    val Member.isNotProtected: Boolean
        @JvmStatic inline get() = !this.isProtected

    val Class<*>.isProtected: Boolean
        @JvmStatic inline get() = Modifier.isProtected(this.modifiers)
    val Class<*>.isNotProtected: Boolean
        @JvmStatic inline get() = !this.isProtected


    val Member.isPrivate: Boolean
        @JvmStatic inline get() = Modifier.isPrivate(this.modifiers)
    val Member.isNotPrivate: Boolean
        @JvmStatic inline get() = !this.isPrivate

    val Class<*>.isPrivate: Boolean
        @JvmStatic inline get() = Modifier.isPrivate(this.modifiers)
    val Class<*>.isNotPrivate: Boolean
        @JvmStatic inline get() = !this.isPrivate


    val Member.isPackagePrivate: Boolean
        @JvmStatic inline get() = this.isNotPublic && this.isNotProtected && this.isNotPrivate
    val Member.isNotPackagePrivate: Boolean
        @JvmStatic inline get() = !this.isPackagePrivate

    val Class<*>.isPackagePrivate: Boolean
        @JvmStatic inline get() = this.isNotPublic && this.isNotProtected && this.isNotPrivate
    val Class<*>.isNotPackagePrivate: Boolean
        @JvmStatic inline get() = !this.isPackagePrivate


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


    val Method.isNative: Boolean
        @JvmStatic inline get() = Modifier.isNative(this.modifiers)
    val Method.isNotNative: Boolean
        @JvmStatic inline get() = !this.isNative

    val Constructor<*>.isNative: Boolean
        @JvmStatic inline get() = Modifier.isNative(this.modifiers)
    val Constructor<*>.isNotNative: Boolean
        @JvmStatic inline get() = !this.isNative


    val Method.isSynchronized: Boolean
        @JvmStatic inline get() = Modifier.isSynchronized(this.modifiers)
    val Method.isNotSynchronized: Boolean
        @JvmStatic inline get() = !this.isSynchronized


    val Method.isAbstract: Boolean
        @JvmStatic inline get() = Modifier.isAbstract(this.modifiers)
    val Method.isNotAbstract: Boolean
        @JvmStatic inline get() = !this.isAbstract

    val Class<*>.isAbstract: Boolean
        @JvmStatic inline get() = Modifier.isAbstract(this.modifiers)
    val Class<*>.isNotAbstract: Boolean
        @JvmStatic inline get() = !this.isAbstract


    val Field.isTransient: Boolean
        @JvmStatic inline get() = Modifier.isTransient(this.modifiers)
    val Field.isNotTransient: Boolean
        @JvmStatic inline get() = !this.isTransient


    val Field.isVolatile: Boolean
        @JvmStatic inline get() = Modifier.isVolatile(this.modifiers)
    val Field.isNotVolatile: Boolean
        @JvmStatic inline get() = !this.isVolatile

    // AccessFlag.VARARGS = 0x0080
    const val VARARGS = 0x0080


    val Method.isVarargs: Boolean
        @JvmStatic inline get() = (this.modifiers and VARARGS) != 0
    val Method.isNotVarargs: Boolean
        @JvmStatic inline get() = !this.isVarargs

    val Constructor<*>.isVarargs: Boolean
        @JvmStatic inline get() = (this.modifiers and VARARGS) != 0
    val Constructor<*>.isNotVarargs: Boolean
        @JvmStatic inline get() = !this.isVarargs


    val Method.paramCount: Int
        @JvmStatic inline get() = this.parameterTypes.size


    val Constructor<*>.paramCount: Int
        @JvmStatic inline get() = this.parameterTypes.size


    val Method.isEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount == 0
    val Method.isNotEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount != 0


    val Constructor<*>.isEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount == 0
    val Constructor<*>.isNotEmptyParam: Boolean
        @JvmStatic inline get() = this.paramCount != 0

}