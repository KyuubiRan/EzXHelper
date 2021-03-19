package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.init.InitFields.mClassLoader
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * 通过模块加载类
 * @param clzName 类名
 * @param clzLoader 类加载器
 * @return 被加载的类
 * @throws IllegalArgumentException 当类名为空时
 * @throws ClassNotFoundException 当无法找到类时
 */
fun loadClass(clzName: String, clzLoader: ClassLoader = mClassLoader): Class<*> {
    if (clzName.isEmpty()) throw  IllegalArgumentException("Class name must not be null or empty!")
    return clzLoader.loadClass(clzName)
}

/**
 * 获取类的所有方法
 * @param clzName 类名
 * @return 方法数组
 * @throws IllegalArgumentException 当类名为空时
 */
fun getMethods(clzName: String): Array<Method> {
    if (clzName.isEmpty()) throw  IllegalArgumentException("Class name must not be null or empty!")
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
 * @throws IllegalArgumentException 当类名为空时
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
 * @throws IllegalArgumentException 当方法名为空时
 * @throws NoSuchMethodException 当找不到方法时
 */
fun Any.getMethodByClzOrObj(
    methodName: String,
    isStatic: Boolean = false,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = arrayOf()
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
 * @throws IllegalArgumentException 当方法名为空时
 */
fun Class<*>.getStaticMethodByClz(
    methodName: String,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = arrayOf()
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
 * @throws IllegalArgumentException 当方法名为空时
 */
fun getMethod(
    clzName: String,
    isStatic: Boolean = false,
    methodName: String,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = arrayOf()
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
 *  @throws NoSuchMethodException 未找到方法时抛出
 */
fun Array<Method>.findMethodByCondition(condition: (Method) -> Boolean): Method {
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
 * @throws NoSuchMethodException 当未找到方法时
 */
fun findMethodByCondition(clz: Class<*>, condition: (Method) -> Boolean): Method {
    return clz.declaredMethods.findMethodByCondition(condition)
}

/**
 * 通过条件查找方法
 * @param clzName 类名
 * @param condition 条件
 * @throws NoSuchMethodException 当未找到方法时
 */
fun findMethodByCondition(clzName: String, condition: (Method) -> Boolean): Method {
    return getMethods(clzName).findMethodByCondition(condition)
}


/**
 * 扩展函数 通过类或者对象获取单个属性
 * @param fieldName 属性名
 * @param isStatic 是否静态类型
 * @param fieldType 属性类型
 * @throws IllegalArgumentException 当属性名为空时
 * @throws NoSuchFieldException 当未找到属性时
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
 * 扩展函数 通过类获取静态属性
 * @param fieldName 属性名称
 * @param fieldType 属性类型
 * @throws IllegalArgumentException 当属性名为空时
 * @throws NoSuchFieldException 当未找到属性时
 */
fun Class<*>.getStaticFiledByClass(fieldName: String, fieldType: Class<*>? = null): Field {
    if (fieldName.isEmpty()) throw IllegalArgumentException("Field name must not be null or empty!")
    return this.getFieldByClzOrObj(fieldName, true, fieldType)
}

/**
 * 扩展函数 通过对象 获取对象中的对象
 * 注意 请勿对类使用此函数
 * @param objName 对象名称
 * @param type 类型
 * @throws IllegalArgumentException 当对象是一个Class时
 * @throws IllegalArgumentException 当目标对象名为空时
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
 * 扩展函数 通过对象 获取对象中的对象
 * 注意 请勿对类使用此函数
 * @param field 属性
 * @throws IllegalArgumentException 当对象是一个Class时
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
 * 扩展函数 通过Class获取目标实例化对象中的对象
 * @param objName 需要获取的对象名
 * @param fieldType 类型
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
 * 扩展函数 设置对象中对象的值
 * 注意 请勿对类使用此函数
 * @param objName 需要设置的对象名称
 * @param value 值
 * @param fieldType 对象类型
 * @throws IllegalArgumentException 当对象是一个类时
 * @throws IllegalArgumentException 当对象名为空时
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
 * @throws IllegalArgumentException 当对象是一个类时
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
 * 扩展函数 设置类中静态对象值
 * @param objName 需要设置的对象名称
 * @param value 值
 * @param fieldType 对象类型
 * @throws IllegalArgumentException 当对象名为空时
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
 * @param args 参数表 可空
 * @param argTypes 参数类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当方法名为空时
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 * @throws IllegalArgumentException 当对象是一个Class时
 */
fun Any.invokeMethod(
    methodName: String,
    args: Array<out Any> = arrayOf(),
    argTypes: Array<out Class<*>> = arrayOf(),
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
 * 扩展函数 调用类的静态方法
 * @param methodName 方法名
 * @param args 参数表 可空
 * @param argTypes 参数类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
fun Class<*>.invokeStaticMethod(
    methodName: String,
    args: Array<out Any> = arrayOf(),
    argTypes: Array<out Class<*>> = arrayOf(),
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
 * 扩展函数 创建新的实例化对象
 * @param args 构造函数的参数表
 * @param argTypes 构造函数的参数类型
 * @return 成功时返回实例化的对象 失败时返回null
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
fun Class<*>.newInstance(
    args: Array<out Any> = arrayOf(),
    argTypes: Array<out Class<*>> = arrayOf()
): Any? {
    if (args.size != argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    return try {
        val constructor: Constructor<*> =
            if (argTypes.isNotEmpty())
                this.getDeclaredConstructor(*argTypes)
            else
                this.getDeclaredConstructor()
        if (args.isNullOrEmpty()) {
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
 * 扩展属性 判断方法是否为Static
 */
val Method.isStatic: Boolean
    get() = Modifier.isStatic(this.modifiers)

/**
 * 扩展属性 判断方法是否为Public
 */
val Method.isPublic: Boolean
    get() = Modifier.isPublic(this.modifiers)

/**
 * 扩展属性 判断方法是否为Protected
 */
val Method.isProtected: Boolean
    get() = Modifier.isProtected(this.modifiers)

/**
 * 扩展属性 判断方法是否为Private
 */
val Method.isPrivate: Boolean
    get() = Modifier.isPrivate(this.modifiers)

/**
 * 扩展属性 判断方法是否为Final
 */
val Method.isFinal: Boolean
    get() = Modifier.isFinal(this.modifiers)

/**
 * 扩展属性 判断属性是否为Static
 */
val Field.isStatic: Boolean
    get() = Modifier.isStatic(this.modifiers)

/**
 * 扩展属性 判断属性是否为Public
 */
val Field.isPublic: Boolean
    get() = Modifier.isPublic(this.modifiers)

/**
 * 扩展属性 判断属性是否为Protected
 */
val Field.isProtected: Boolean
    get() = Modifier.isProtected(this.modifiers)

/**
 * 扩展属性 判断属性是否为Private
 */
val Field.isPrivate: Boolean
    get() = Modifier.isPrivate(this.modifiers)

/**
 * 扩展属性 判断属性是否为Final
 */
val Field.isFinal: Boolean
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