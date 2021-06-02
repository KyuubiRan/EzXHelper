package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.init.InitFields
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.*

/**
 * 通过模块加载类
 * @param clzName 类名
 * @param clzLoader 类加载器
 * @return 被加载的类
 * @throws IllegalArgumentException 类名为空
 * @throws ClassNotFoundException 未找到类
 */
fun loadClass(clzName: String, clzLoader: ClassLoader = InitFields.ezXClassLoader): Class<*> {
    if (clzName.isEmpty()) throw  IllegalArgumentException("Class name must not be null or empty!")
    return clzLoader.loadClass(clzName)
}

/**
 * 获取类的所有方法
 * @param clzName 类名
 * @return 方法数组
 * @throws IllegalArgumentException 类名为空
 */
fun getMethods(clzName: String): Array<Method> {
    if (clzName.isEmpty()) throw IllegalArgumentException("Class name must not be null or empty!")
    return loadClass(clzName).declaredMethods
}

/**
 * 扩展函数 获取类/实例化对象的所有方法
 * @return 方法数组
 */
fun Any.getMethodsByObject(): Array<Method> {
    val clz: Class<*> = if (this is Class<*>) this else this::class.java
    return clz.declaredMethods
}

/**
 * 获取类的所有属性
 * @param clzName 类名
 * @return 属性数组
 * @throws IllegalArgumentException 类名为空
 */
fun getFields(clzName: String): Array<Field> {
    if (clzName.isEmpty()) throw  IllegalArgumentException("Class name must not be null or empty!")
    return loadClass(clzName).declaredFields
}

/**
 * 扩展函数 通过类或者对象获取单个方法
 * @param methodName 方法名
 * @param isStatic 是否为静态方法
 * @param returnType 方法返回值 填入null为无视返回值
 * @param argTypes 方法形参表类型
 * @return 符合条件的方法
 * @throws IllegalArgumentException 方法名为空
 * @throws NoSuchMethodException 未找到方法
 */
fun Any.getMethodByClzOrObj(
    methodName: String,
    isStatic: Boolean = false,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = emptyArray()
): Method {
    if (methodName.isEmpty()) throw IllegalArgumentException("Method name must not be null or empty!")
    var clz = if (this is Class<*>) this else this.javaClass
    do {
        method@ for (m in clz.declaredMethods) {
            if ((isStatic && !m.isStatic) || (!isStatic && m.isStatic)) continue
            if (m.name != methodName) continue
            if (returnType != null && m.returnType != returnType) continue
            if (m.parameterTypes.isNotEmpty()) {
                for (i in m.parameterTypes.indices) {
                    if (argTypes[i] != m.parameterTypes[i]) continue@method
                }
            }
            m.isAccessible = true
            return m
        }
        if (clz.superclass == null) throw NoSuchMethodException()
        clz = clz.superclass
    } while (true)
}

/**
 * 扩展函数 通过类获取单个静态方法
 * @param methodName 方法名
 * @param returnType 方法返回值 填入null为无视返回值
 * @param argTypes 方法形参表类型
 * @throws IllegalArgumentException 方法名为空
 */
fun Class<*>.getStaticMethodByClz(
    methodName: String,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = emptyArray()
): Method {
    if (methodName.isEmpty()) throw IllegalArgumentException("Method name must not be null or empty!")
    return this.getMethodByClzOrObj(methodName, true, returnType, argTypes)
}

/**
 * 获取单个方法
 * @param clzName 类名
 * @param isStatic 是否为静态方法
 * @param methodName 方法名
 * @param returnType 方法返回值 填入null为无视返回值
 * @param argTypes 方法形参表类型
 * @throws IllegalArgumentException 方法名为空
 */
fun getMethod(
    clzName: String,
    isStatic: Boolean = false,
    methodName: String,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = emptyArray()
): Method {
    if (methodName.isEmpty()) throw IllegalArgumentException("Method name must not be null or empty!")
    return loadClass(clzName).getMethodByClzOrObj(
        methodName,
        isStatic,
        returnType,
        argTypes
    )
}

/**
 *  通过方法数组 根据条件查找方法
 *  @param condition 方法的具体条件
 *  @return 符合条件的方法
 *  @throws NoSuchMethodException 未找到方法
 */
fun Array<Method>.findMethodByCondition(condition: (m: Method) -> Boolean): Method {
    for (m in this) {
        if (condition(m)) {
            m.isAccessible = true
            return m
        }
    }
    throw NoSuchMethodException()
}

/**
 * 通过条件查找方法
 * @param clz 类
 * @param condition 条件
 * @return 符合条件的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun findMethodByCondition(clz: Class<*>, condition: (m: Method) -> Boolean): Method {
    return clz.declaredMethods.findMethodByCondition(condition)
}

/**
 * 通过条件查找方法
 * @param clzName 类名
 * @param condition 条件
 * @return 符合条件的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun findMethodByCondition(clzName: String, condition: (m: Method) -> Boolean): Method {
    return getMethods(clzName).findMethodByCondition(condition)
}

/**
 * 扩展函数 通过类或者对象获取单个属性
 * @param fieldName 属性名
 * @param isStatic 是否静态类型
 * @param fieldType 属性类型
 * @return 符合条件的属性
 * @throws IllegalArgumentException 属性名为空
 * @throws NoSuchFieldException 未找到属性
 */
fun Any.getFieldByClzOrObj(
    fieldName: String,
    isStatic: Boolean = false,
    fieldType: Class<*>? = null
): Field {
    if (fieldName.isEmpty()) throw IllegalArgumentException("Field name must not be null or empty!")
    var clz: Class<*> = if (this is Class<*>) this else this.javaClass
    do {
        for (f in clz.declaredFields) {
            if ((isStatic && !f.isStatic) || (!isStatic && f.isStatic)) continue
            if ((fieldType == null || f.type == fieldType) && (f.name == fieldName)) {
                f.isAccessible = true
                return f
            }
        }
        if (clz.superclass == null) throw NoSuchFieldException()
        clz = clz.superclass
    } while (true)
}

/**
 * 扩展函数 通过类型获取属性
 * @param type 类型
 * @param isStatic 是否静态
 * @return 符合条件的属性
 * @throws NoSuchFileException 未找到属性
 */
fun Any.getFieldByType(type: Class<*>, isStatic: Boolean = false): Field {
    var clz: Class<*> = if (this is Class<*>) this else this.javaClass
    do {
        for (f in clz.declaredFields) {
            if ((isStatic && !f.isStatic) || (!isStatic && f.isStatic)) continue
            if (f.type == type) {
                f.isAccessible = true
                return f
            }
        }
        if (clz.superclass == null) throw NoSuchFieldException()
        clz = clz.superclass
    } while (true)
}

fun Any.getStaticFieldByType(type: Class<*>): Field {
    return this.getFieldByType(type, true)
}

/**
 * 扩展函数 通过类获取静态属性
 * @param fieldName 属性名称
 * @param fieldType 属性类型
 * @return 符合条件的属性
 * @throws IllegalArgumentException 属性名为空
 * @throws NoSuchFieldException 未找到属性
 */
fun Class<*>.getStaticFiledByClass(fieldName: String, fieldType: Class<*>? = null): Field {
    if (fieldName.isEmpty()) throw IllegalArgumentException("Field name must not be null or empty!")
    return this.getFieldByClzOrObj(fieldName, true, fieldType)
}

/**
 * 扩展函数 获取实例化对象中的对象
 * 注意 请勿对Class使用此函数
 * @param objName 对象名称
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
fun Any.getObjectOrNull(objName: String, type: Class<*>? = null): Any? {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
    try {
        val f = this.javaClass.getFieldByClzOrObj(objName, false, type)
        f.let {
            it.isAccessible = true
            return it.get(this)
        }
    } catch (e: Exception) {
        Log.e(e)
        return null
    }
}

/**
 * 扩展函数 获取实例化对象中的对象
 * 注意 请勿对Class使用此函数
 * @param objName 对象名称
 * @param type 类型
 * @param T 转换的类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
fun <T> Any.getObjectOrNullAs(objName: String, type: Class<*>? = null): T? {
    @Suppress("UNCHECKED_CAST")
    return this.getObjectOrNull(objName, type) as T?
}

/**
 * 扩展函数 获取实例化对象中的对象
 * 注意 请勿对Class使用此函数
 * @param field 属性
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.getObjectOrNull(field: Field): Any? {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    try {
        field.let {
            it.isAccessible = true
            return it.get(this)
        }
    } catch (e: Exception) {
        Log.e(e)
        return null
    }
}

/**
 * 扩展函数 获取实例化对象中的对象 并且转换为T?类型
 * 注意 请勿对Class使用此函数
 * @param field 属性
 * @param T 转换的类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
fun <T> Any.getObjectOrNullAs(field: Field): T? {
    @Suppress("UNCHECKED_CAST")
    return this.getObjectOrNull(field) as T?
}

/**
 * 扩展函数 通过类型 获取实例化对象中的对象
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 * 注意 请勿对Class使用此函数
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.getObjectOrNullByType(type: Class<*>): Any? {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    return try {
        this.getFieldByType(type).get(this)
    } catch (e: Exception) {
        Log.e(e)
        null
    }
}

/**
 * 扩展函数 获取类中的静态对象
 * @param objName 需要获取的对象名
 * @param fieldType 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 当名字为空时
 */
fun Class<*>.getStaticObjectOrNull(
    objName: String,
    fieldType: Class<*>? = null
): Any? {
    try {
        if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
        val f: Field
        try {
            f = this.getStaticFiledByClass(objName, fieldType)
        } catch (e: NoSuchFieldException) {
            return null
        }
        f.let {
            it.isAccessible = true
            return it.get(this)
        }
    } catch (e: Exception) {
        Log.e(e)
        return null
    }
}

/**
 * 扩展函数 获取类中的静态对象 并且转换为T?类型
 * @param objName 需要获取的对象名
 * @param fieldType 类型
 * @param T 转换的类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 当名字为空时
 */
fun <T> Class<*>.getStaticObjectOrNullAs(
    objName: String,
    fieldType: Class<*>? = null
): T? {
    @Suppress("UNCHECKED_CAST")
    return this.getStaticObjectOrNull(objName, fieldType) as T?
}

/**
 * 扩展函数 通过类型 获取类中的静态对象
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 */
fun Class<*>.getStaticObjectOrNullByType(type: Class<*>): Any? {
    return try {
        this.getStaticFieldByType(type).get(null)
    } catch (e: Exception) {
        Log.e(e)
        null
    }
}

/**
 * 扩展函数 设置对象中对象的值
 * 注意 请勿对类使用此函数
 * @param objName 需要设置的对象名称
 * @param value 值
 * @param fieldType 对象类型
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 对象名为空
 */
fun Any.putObject(objName: String, value: Any?, fieldType: Class<*>? = null) {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
    try {
        val f = this.getFieldByClzOrObj(objName, false, fieldType)
        f.let {
            it.isAccessible = true
            it.set(this, value)
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

/**
 * 扩展函数 设置对象中对象的值
 * 注意 请勿对类使用此函数
 * @param field 属性
 * @param value 值
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.putObject(field: Field, value: Any?) {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    try {
        field.let {
            it.isAccessible = true
            it.set(this, value)
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

/**
 * 扩展函数 通过类型设置值
 * 不推荐使用 只会设置第一个类型符合的对象的值
 * @param value 值
 * @param type 类型
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.putObjectByType(value: Any?, type: Class<*>) {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    try {
        val f = this.getFieldByType(type)
        f.let {
            it.isAccessible = true
            it.set(this, value)
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

/**
 * 扩展函数 通过类型设置类中的静态对象的值
 * 不推荐使用 只会设置第一个类型符合的对象的值
 * @param value 值
 * @param type 类型
 */
fun Class<*>.putStaticObjectByType(value: Any?, type: Class<*>) {
    try {
        val f = this.getStaticFieldByType(type)
        f.let {
            it.isAccessible = true
            it.set(null, value)
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

/**
 * 扩展函数 设置类中静态对象值
 * @param objName 需要设置的对象名称
 * @param value 值
 * @param fieldType 对象类型
 * @throws IllegalArgumentException 对象名为空
 */
fun Class<*>.putStaticObject(objName: String, value: Any?, fieldType: Class<*>? = null) {
    try {
        if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
        val f: Field
        try {
            f = this.getStaticFiledByClass(objName, fieldType)
        } catch (e: NoSuchFieldException) {
            return
        }
        f.let {
            it.isAccessible = true
            it.set(null, value)
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

/**
 * 扩展函数 调用对象的方法
 * 注意 请勿对类使用此函数
 * @param methodName 方法名
 * @param args 形参表 可空
 * @param argTypes 形参类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当方法名为空时
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 * @throws IllegalArgumentException 当对象是一个Class时
 */
fun Any.invokeMethod(
    methodName: String,
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray(),
    returnType: Class<*>? = null
): Any? {
    if (methodName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    if (args.size != argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    val m: Method
    if (args.isEmpty()) {
        try {
            m = this.getMethodByClzOrObj(methodName, false, returnType)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this)
        }
    } else {
        try {
            m = this.getMethodByClzOrObj(methodName, false, returnType, argTypes)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this, *args)
        }
    }
}

/**
 * 扩展函数 调用对象的方法 并且将结果转换为T?类型
 * 注意 请勿对类使用此函数
 * @param methodName 方法名
 * @param args 形参表 可空
 * @param argTypes 形参类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @param T 转换的类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当方法名为空时
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 * @throws IllegalArgumentException 当对象是一个Class时
 */
fun <T> Any.invokeMethodAs(
    methodName: String,
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray(),
    returnType: Class<*>? = null
): T? {
    @Suppress("UNCHECKED_CAST")
    return this.invokeMethod(methodName, args, argTypes, returnType) as T?
}

/**
 * 扩展函数 调用类的静态方法
 * @param methodName 方法名
 * @param args 形参表 可空
 * @param argTypes 形参类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
fun Class<*>.invokeStaticMethod(
    methodName: String,
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray(),
    returnType: Class<*>? = null
): Any? {
    if (args.size != argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    val m: Method
    if (args.isEmpty()) {
        try {
            m = this.getMethodByClzOrObj(methodName, true, returnType)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this)
        }
    } else {
        try {
            m = this.getMethodByClzOrObj(methodName, true, returnType, argTypes)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this, *args)
        }
    }
}

/**
 * 扩展函数 调用类的静态方法 并且将结果转换为T?类型
 * @param methodName 方法名
 * @param args 形参表 可空
 * @param argTypes 形参类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
fun <T> Class<*>.invokeStaticMethodAs(
    methodName: String,
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray(),
    returnType: Class<*>? = null
): T? {
    @Suppress("UNCHECKED_CAST")
    return this.invokeStaticMethod(methodName, args, argTypes, returnType) as T?
}

/**
 * 扩展函数 创建新的实例化对象
 * @param args 构造函数的形参表
 * @param argTypes 构造函数的形参类型
 * @return 成功时返回实例化的对象 失败时返回null
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
fun Class<*>.newInstance(
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray()
): Any? {
    if (args.size != argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    return try {
        val constructor: Constructor<*> =
            if (argTypes.isNotEmpty())
                this.getDeclaredConstructor(*argTypes)
            else
                this.getDeclaredConstructor()
        if (args.isEmpty()) {
            constructor.newInstance()
        } else {
            constructor.newInstance(*args)
        }
    } catch (e: Exception) {
        Log.e(e)
        null
    }
}

/**
 * 扩展函数 创建新的实例化对象 并将对象转换为T?类型
 * @param args 构造函数的形参表
 * @param argTypes 构造函数的形参类型
 * @return 成功时返回实例化的对象 失败时返回null
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
fun <T> Class<*>.newInstanceAs(
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray()
): T? {
    @Suppress("UNCHECKED_CAST")
    return this.newInstance(args, argTypes) as T?
}

/**
 * 扩展函数 调用原方法
 * @param obj 被调用对象
 * @param args 形参表 为null时则为无参
 * @return 原方法调用后的返回值
 */
fun Method.invokedByOriginal(obj: Any?, args: Array<Any?>? = null): Any? {
    return XposedBridge.invokeOriginalMethod(this, obj, args)
}

/**
 * 扩展函数 调用原方法 并将结果转换为T?类型
 * @param obj 被调用对象
 * @param args 形参表 为null时则为无参
 * @return 原方法调用后的返回值
 */
fun <T> Method.invokedByOriginalAs(
    obj: Any?, args: Array<Any?>? = null
): T? {
    @Suppress("UNCHECKED_CAST")
    return XposedBridge.invokeOriginalMethod(this, obj, args) as T?
}

/**
 * 扩展函数 调用方法 并将结果转换为T?类型
 * @param obj 被调用对象
 * @param args 形参表
 */
fun <T> Method.invokeAs(obj: Any?, vararg args: Any?): T? {
    @Suppress("UNCHECKED_CAST")
    return this.invoke(obj, args) as T?
}

/**
 * 扩展属性 判断是否为Static
 */
val Member.isStatic: Boolean
    get() = Modifier.isStatic(this.modifiers)

/**
 * 扩展属性 判断是否为Public
 */
val Member.isPublic: Boolean
    get() = Modifier.isPublic(this.modifiers)

/**
 * 扩展属性 判断是否为Protected
 */
val Member.isProtected: Boolean
    get() = Modifier.isProtected(this.modifiers)

/**
 * 扩展属性 判断是否为Private
 */
val Member.isPrivate: Boolean
    get() = Modifier.isPrivate(this.modifiers)

/**
 * 扩展属性 判断是否为Final
 */
val Member.isFinal: Boolean
    get() = Modifier.isFinal(this.modifiers)

/**
 * 深拷贝一个对象
 * @param srcObj 源对象
 * @param newObj 新对象
 * @return 成功返回拷贝后的对象 失败返回null
 */
fun <T> fieldCpy(srcObj: T, newObj: T): T? {
    return try {
        var clz: Class<*> = srcObj!!::class.java
        var fields: Array<Field>
        while (Object::class.java != clz) {
            fields = clz.declaredFields
            for (f in fields) {
                f.isAccessible = true
                f.set(newObj, f.get(srcObj))
            }
            clz = clz.superclass
        }
        newObj
    } catch (e: Exception) {
        Log.e(e)
        null
    }
}