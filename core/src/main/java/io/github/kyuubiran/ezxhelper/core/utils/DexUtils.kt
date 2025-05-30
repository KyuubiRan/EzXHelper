package io.github.kyuubiran.ezxhelper.core.utils

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isPrivate
import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isStatic
import java.lang.reflect.Field
import java.lang.reflect.Method

object DexUtils {
    private enum class MemberType {
        Field, Method
    }

    private class DexDescriptor(sig: String, type: MemberType) {

        private var name: String
        private var declaringClass: String
        private var signature: String

        init {
            when (type) {
                MemberType.Field -> {
                    val retIdx: Int = sig.indexOf("->")
                    val typeIdx: Int = sig.indexOf(':', retIdx)
                    declaringClass = sig.substring(0, retIdx)
                    name = sig.substring(retIdx + 2, typeIdx)
                    signature = sig.substring(typeIdx + 1)
                }

                MemberType.Method -> {
                    val retIdx: Int = sig.indexOf("->")
                    val argsIdx: Int = sig.indexOf('(', retIdx)
                    declaringClass = sig.substring(0, retIdx)
                    name = sig.substring(retIdx + 2, argsIdx)
                    signature = sig.substring(argsIdx)
                }
            }
        }

        fun getTypeSignature(type: Class<*>): String {
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
            return if (type.isArray) "[" + getTypeSignature(type.componentType!!)
            else "L" + type.name.replace('.', '/') + ";"
        }

        fun getMethodSignatureWithoutName(method: Method): String = buildString {
            append("(")
            method.parameterTypes.forEach { append(getTypeSignature(it)) }
            append(")")
            append(getTypeSignature(method.returnType))
        }

        @Throws(NoSuchMethodException::class)
        fun getMethod(clzLoader: ClassLoader): Method {
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

        fun getMethodOrNull(clzLoader: ClassLoader): Method? = try {
            getMethod(clzLoader)
        } catch (e: NoSuchMethodException) {
            null
        }

        @Throws(NoSuchFieldException::class)
        fun getField(clzLoader: ClassLoader): Field {
            try {
                var clz = Class.forName(declaringClass.substring(1, declaringClass.length - 1).replace('/', '.'), false, clzLoader)
                clz.declaredFields.forEach { f ->
                    if (f.name == name && getTypeSignature(f.type) == signature) return f
                }
                while (clz.superclass?.also { clz = it } != null) {
                    clz.declaredFields.forEach { f ->
                        if (f.isPrivate || f.isStatic) return@forEach
                        if (f.name == name && getTypeSignature(f.type) == signature) return f
                    }
                }
                throw NoSuchFieldException("$declaringClass->$name$signature")
            } catch (e: ClassNotFoundException) {
                throw NoSuchFieldException("$declaringClass->$name$signature").initCause(e)
            }
        }

        fun getFieldOrNull(clzLoader: ClassLoader): Field? = try {
            getField(clzLoader)
        } catch (e: NoSuchFieldException) {
            null
        }

        override fun toString(): String {
            return "$declaringClass->$name$signature"
        }
    }


    /**
     * Get method by signature or throw exception
     * @param signature signature
     * @param clzLoader class loader
     * @return method or throw [NoSuchMethodException]
     */
    @Throws(NoSuchMethodException::class)
    @JvmStatic
    fun getMethod(signature: String, clzLoader: ClassLoader? = null): Method =
        DexDescriptor(signature, MemberType.Method).getMethod(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get method by signature or null if not found
     * @param signature signature
     * @param clzLoader class loader
     * @return method or null
     */
    @JvmStatic
    fun getMethodOrNull(signature: String, clzLoader: ClassLoader? = null): Method? =
        DexDescriptor(signature, MemberType.Method).getMethodOrNull(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get field by signature or throw exception
     * @param signature signature
     * @param clzLoader class loader
     * @return method or throw [NoSuchFieldException]
     */
    @Throws(NoSuchFieldException::class)
    @JvmStatic
    fun getField(signature: String, clzLoader: ClassLoader? = null): Field =
        DexDescriptor(signature, MemberType.Field).getField(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get field by signature or null if not found
     * @param signature signature
     * @param clzLoader class loader
     * @return field or null
     */
    @JvmStatic
    fun getFieldOrNull(signature: String, clzLoader: ClassLoader? = null): Field? =
        DexDescriptor(signature, MemberType.Field).getFieldOrNull(clzLoader ?: ClassLoaderProvider.safeClassLoader)
}