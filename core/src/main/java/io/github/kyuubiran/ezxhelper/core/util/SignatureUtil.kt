package io.github.kyuubiran.ezxhelper.core.util

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.extension.MemberExtension.isPrivate
import io.github.kyuubiran.ezxhelper.core.extension.MemberExtension.isStatic
import java.lang.reflect.Field
import java.lang.reflect.Method

object SignatureUtil {
    private enum class MemberType {
        Field, Method
    }

    private class SignatureDescriptor(sig: String, type: MemberType) {

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
     *
     * 通过签名获取方法，如果未找到则抛出异常
     *
     * e.g.: [String.toString] `Ljava/lang/String;->toString()Ljava/lang/String;`
     *
     * @param signature signature | 签名
     * @param clzLoader class loader | 类加载器
     * @return method or throw [NoSuchMethodException]
     */
    @Throws(NoSuchMethodException::class)
    @JvmStatic
    fun getMethod(signature: String, clzLoader: ClassLoader? = null): Method =
        SignatureDescriptor(signature, MemberType.Method).getMethod(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get method by signature or null if not found
     *
     * 通过签名获取方法，如果未找到则返回 null
     *
     * e.g.: [String.toString] `Ljava/lang/String;->toString()Ljava/lang/String;`
     *
     * @param signature signature | 签名
     * @param clzLoader class loader | 类加载器
     * @return method or null | 未找到则返回 null
     */
    @JvmStatic
    fun getMethodOrNull(signature: String, clzLoader: ClassLoader? = null): Method? =
        SignatureDescriptor(signature, MemberType.Method).getMethodOrNull(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get field by signature or throw exception
     *
     * 通过签名获取字段，如果未找到则抛出异常
     *
     * e.g.: [Integer.MAX_VALUE] `Ljava/lang/Integer;->MAX_VALUE:I`
     *
     * @param signature signature | 签名
     * @param clzLoader class loader | 类加载器
     * @return method or throw [NoSuchFieldException] | 未找到则抛出 [NoSuchFieldException]
     */
    @Throws(NoSuchFieldException::class)
    @JvmStatic
    fun getField(signature: String, clzLoader: ClassLoader? = null): Field =
        SignatureDescriptor(signature, MemberType.Field).getField(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get field by signature or null if not found
     *
     * 通过签名获取字段，如果未找到则返回 null
     *
     * e.g.: [Integer.MAX_VALUE] `Ljava/lang/Integer;->MAX_VALUE:I`
     *
     * @param signature signature | 签名
     * @param clzLoader class loader | 类加载器
     * @return field or null | 未找到则返回 null
     */
    @JvmStatic
    fun getFieldOrNull(signature: String, clzLoader: ClassLoader? = null): Field? =
        SignatureDescriptor(signature, MemberType.Field).getFieldOrNull(clzLoader ?: ClassLoaderProvider.safeClassLoader)

    /**
     * Get method signature
     *
     * 获取方法签名
     *
     * @param method method
     */
    @JvmStatic
    fun getSignature(method: Method): String = buildString {
        append(getTypeSignature(method.declaringClass))
        append("->")
        append(method.name)
        append("(")
        method.parameterTypes.forEach { append(getTypeSignature(it)) }
        append(")")
        append(getTypeSignature(method.returnType))
    }

    /**
     * Get field signature
     *
     * 获取字段签名
     *
     * @param field field | 字段
     */
    @JvmStatic
    fun getSignature(field: Field): String = buildString {
        append(getTypeSignature(field.declaringClass))
        append("->")
        append(field.name)
        append(":")
        append(getTypeSignature(field.type))
    }

    /**
     * Get type signature
     *
     * 获取类型签名
     *
     * @param type class type | 类型
     */
    @JvmStatic
    fun getTypeSignature(type: Class<*>): String {
        if (type.isPrimitive) {
            return when (type.name) {
                java.lang.Boolean.TYPE.name -> "Z"
                java.lang.Byte.TYPE.name -> "B"
                java.lang.Short.TYPE.name -> "S"
                java.lang.Character.TYPE.name -> "C"
                java.lang.Integer.TYPE.name -> "I"
                java.lang.Long.TYPE.name -> "J"
                java.lang.Float.TYPE.name -> "F"
                java.lang.Double.TYPE.name -> "D"
                java.lang.Void.TYPE.name -> "V"
                else -> throw IllegalStateException("Type: " + type.name + " is not a primitive type")
            }
        }
        return if (type.isArray) "[" + getTypeSignature(type.componentType!!)
        else "L" + type.name.replace('.', '/') + ";"
    }

    /** Convert signature to types
     *
     * 将签名转换为类型
     *
     * e.g. `IIZLjava/lang/String;` => `[Int, Int, Boolean, String]`
     *
     *  @param signature signature
     *  @return list of types
     */
    @JvmStatic
    fun signatureToTypes(signature: String, classLoader: ClassLoader? = null): List<Class<*>> {
        val types = mutableListOf<Class<*>>()
        if (signature.isEmpty()) return types
        var index = 0
        while (index < signature.length) {
            when (val c = signature[index]) {
                'V' -> {
                    types.add(java.lang.Void.TYPE)
                    index++
                }

                'Z' -> {
                    types.add(java.lang.Boolean.TYPE)
                    index++
                }

                'B' -> {
                    types.add(java.lang.Byte.TYPE)
                    index++
                }

                'S' -> {
                    types.add(java.lang.Short.TYPE)
                    index++
                }

                'C' -> {
                    types.add(java.lang.Character.TYPE)
                    index++
                }

                'I' -> {
                    types.add(java.lang.Integer.TYPE)
                    index++
                }

                'J' -> {
                    types.add(java.lang.Long.TYPE)
                    index++
                }

                'F' -> {
                    types.add(java.lang.Float.TYPE)
                    index++
                }

                'D' -> {
                    types.add(java.lang.Double.TYPE)
                    index++
                }

                'L' -> {
                    val endIndex = signature.indexOf(';', index)
                    if (endIndex == -1) throw IllegalArgumentException("Invalid signature: $signature")
                    val className = signature.substring(index + 1, endIndex).replace('/', '.')
                    types.add(Class.forName(className, false, classLoader ?: ClassLoaderProvider.safeClassLoader))
                    index = endIndex + 1
                }

                '[' -> {
                    var arrDim = 0
                    while (signature[index] == '[') {
                        arrDim++
                        index++
                    }
                    val elementSignature = signature.substring(index)
                    val elementType = signatureToTypes(elementSignature, classLoader).first()
                    var arrayType = elementType
                    repeat(arrDim) {
                        arrayType = java.lang.reflect.Array.newInstance(arrayType, 0).javaClass
                    }
                    val consumed = getTypeSignatureLength(signature.substring(index))
                    index += consumed
                    types.add(arrayType)
                }

                else -> throw IllegalArgumentException("Unsupported type: $c in signature: $signature")
            }
        }

        return types
    }

    private fun getTypeSignatureLength(signature: String): Int {
        var idx = 0
        when (signature[idx]) {
            '[' -> {
                while (signature[idx] == '[') idx++
                return idx + getTypeSignatureLength(signature.substring(idx))
            }
            'L' -> {
                val end = signature.indexOf(';', idx)
                if (end == -1) throw IllegalArgumentException("Invalid signature: $signature")
                return end + 1
            }
            else -> return 1
        }
    }
}