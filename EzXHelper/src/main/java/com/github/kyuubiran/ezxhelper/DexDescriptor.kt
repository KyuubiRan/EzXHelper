@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper

import com.github.kyuubiran.ezxhelper.MemberExtensions.isPrivate
import com.github.kyuubiran.ezxhelper.MemberExtensions.isStatic
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Use the signature to find the method or field.
 */
class DexDescriptor private constructor(sig: String, type: TYPE) {
    private var name: String
    private var declaringClass: String
    private var signature: String

    init {
        when (type) {
            TYPE.FIELD -> {
                val retIdx: Int = sig.indexOf("->")
                val typeIdx: Int = sig.indexOf(':', retIdx)
                declaringClass = sig.substring(0, retIdx)
                name = sig.substring(retIdx + 2, typeIdx)
                signature = sig.substring(typeIdx + 1)
            }

            TYPE.METHOD -> {
                val retIdx: Int = sig.indexOf("->")
                val argsIdx: Int = sig.indexOf('(', retIdx)
                declaringClass = sig.substring(0, retIdx)
                name = sig.substring(retIdx + 2, argsIdx)
                signature = sig.substring(argsIdx)
            }
        }
    }

    @Suppress("ClassName")
    companion object `-Static` {
        private enum class TYPE {
            METHOD, FIELD
        }

        /**
         * Get method by signature or throw exception
         * @param sig signature
         * @param clzLoader class loader
         * @return method or throw [NoSuchMethodException]
         */
        @Throws(NoSuchMethodException::class)
        @JvmStatic
        fun getMethod(sig: String, clzLoader: ClassLoader? = null): Method =
            DexDescriptor(sig, TYPE.METHOD).getMethod(clzLoader ?: ClassLoaderProvider.safeClassLoader)

        /**
         * Get method by signature or null if not found
         * @param sig signature
         * @param clzLoader class loader
         * @return method or null
         */
        @JvmStatic
        fun getMethodOrNull(sig: String, clzLoader: ClassLoader? = null): Method? =
            DexDescriptor(sig, TYPE.METHOD).getMethodOrNull(clzLoader ?: ClassLoaderProvider.safeClassLoader)

        /**
         * Get field by signature or throw exception
         * @param sig signature
         * @param clzLoader class loader
         * @return method or throw [NoSuchFieldException]
         */
        @Throws(NoSuchFieldException::class)
        @JvmStatic
        fun getField(sig: String, clzLoader: ClassLoader? = null): Field =
            DexDescriptor(sig, TYPE.FIELD).getField(clzLoader ?: ClassLoaderProvider.safeClassLoader)

        /**
         * Get field by signature or null if not found
         * @param sig signature
         * @param clzLoader class loader
         * @return field or null
         */
        @JvmStatic
        fun getFieldOrNull(sig: String, clzLoader: ClassLoader? = null): Field? =
            DexDescriptor(sig, TYPE.FIELD).getFieldOrNull(clzLoader ?: ClassLoaderProvider.safeClassLoader)

        private fun getTypeSig(type: Class<*>): String {
            if (type.isPrimitive) {
                return when (type.name) {
                    Void.TYPE.name -> "V"
                    Integer.TYPE.name -> "I"
                    java.lang.Boolean.TYPE.name -> "Z"
                    java.lang.Byte.TYPE.name -> "B"
                    java.lang.Long.TYPE.name -> "L"
                    java.lang.Float.TYPE.name -> "F"
                    java.lang.Double.TYPE.name -> "D"
                    Character.TYPE.name -> "C"
                    java.lang.Short.TYPE.name -> "S"
                    else -> throw IllegalStateException("Type: " + type.name + " is not a primitive type")
                }
            }
            return if (type.isArray) "[" + getTypeSig(type.componentType!!)
            else "L" + type.name.replace('.', '/') + ";"
        }

        private fun getMethodSignatureWithoutName(method: Method): String = buildString {
            append("(")
            method.parameterTypes.forEach { append(getTypeSig(it)) }
            append(")")
            append(getTypeSig(method.returnType))
        }

        /**
         * Get method signature.
         * @param method method
         * e.g. [String.toString] => `Ljava/lang/String;->toString()Ljava/lang/String;`
         */
        @JvmStatic
        fun getSignature(method: Method) = buildString {
            append(getTypeSig(method.declaringClass))
            append("->")
            append(method.name)
            append("(")
            method.parameterTypes.forEach { append(getTypeSig(it)) }
            append(")")
            append(getTypeSig(method.returnType))
        }

        /**
         * Get field signature.
         * @param field field
         * e.g. [Integer.MAX_VALUE] => `Ljava/lang/Integer;->MAX_VALUE:I`
         */
        @JvmStatic
        fun getSignature(field: Field) = buildString {
            append(getTypeSig(field.declaringClass))
            append("->")
            append(field.name)
            append(":")
            append(getTypeSig(field.type))
        }
    }

    override fun toString(): String {
        return "$declaringClass->$name$signature"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other == null || javaClass != other.javaClass) false else toString() == other.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    private fun getDeclaringClassName(): String {
        return declaringClass.substring(1, declaringClass.length - 1).replace('/', '.')
    }

    @Throws(NoSuchMethodException::class)
    private fun getMethod(clzLoader: ClassLoader): Method {
        try {
            var clz = Class.forName(declaringClass.substring(1, declaringClass.length - 1).replace('/', '.'), false, clzLoader)
            clz.declaredMethods.forEach { m ->
                if (m.name == name && getMethodSignatureWithoutName(m) == signature) return m
            }
            while (clz.superclass?.also { clz = it } != null) {
                clz.declaredMethods.forEach { m ->
                    if (m.isPrivate || m.isStatic) return@forEach
                    if (m.name == name && getMethodSignatureWithoutName(m) == signature) return m
                }
            }
            throw NoSuchMethodException("$declaringClass->$name$signature")
        } catch (e: ClassNotFoundException) {
            throw NoSuchMethodException("$declaringClass->$name$signature").initCause(e)
        }
    }

    private fun getMethodOrNull(clzLoader: ClassLoader): Method? = try {
        getMethod(clzLoader)
    } catch (e: NoSuchMethodException) {
        null
    }

    @Throws(NoSuchFieldException::class)
    private fun getField(clzLoader: ClassLoader): Field {
        try {
            var clz = Class.forName(declaringClass.substring(1, declaringClass.length - 1).replace('/', '.'), false, clzLoader)
            clz.declaredFields.forEach { f ->
                if (f.name == name && getTypeSig(f.type) == signature) return f
            }
            while (clz.superclass?.also { clz = it } != null) {
                clz.declaredFields.forEach { f ->
                    if (f.isPrivate || f.isStatic) return@forEach
                    if (f.name == name && getTypeSig(f.type) == signature) return f
                }
            }
            throw NoSuchFieldException("$declaringClass->$name$signature")
        } catch (e: ClassNotFoundException) {
            throw NoSuchFieldException("$declaringClass->$name$signature").initCause(e)
        }
    }

    private fun getFieldOrNull(clzLoader: ClassLoader): Field? = try {
        getField(clzLoader)
    } catch (e: NoSuchFieldException) {
        null
    }
}