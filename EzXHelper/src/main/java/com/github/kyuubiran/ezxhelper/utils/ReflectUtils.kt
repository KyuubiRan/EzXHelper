package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.init.InitFields
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
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
fun Any.getMethodByClassOrObject(
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
            if (m.parameterTypes.size != argTypes.size) continue
            if (returnType != null && m.returnType != returnType) continue
            for (i in m.parameterTypes.indices) {
                if (argTypes[i] != m.parameterTypes[i]) continue@method
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
fun Class<*>.getStaticMethodByClass(
    methodName: String,
    returnType: Class<*>? = null,
    argTypes: Array<out Class<*>> = emptyArray()
): Method {
    if (methodName.isEmpty()) throw IllegalArgumentException("Method name must not be null or empty!")
    return this.getMethodByClassOrObject(methodName, true, returnType, argTypes)
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
    return loadClass(clzName).getMethodByClassOrObject(
        methodName,
        isStatic,
        returnType,
        argTypes
    )
}

/**
 *  扩展函数 通过条件查找方法
 *  @param condition 方法的具体条件
 *  @return 符合条件的方法
 *  @throws NoSuchMethodException 未找到方法
 */
fun Array<Method>.findMethodByCondition(condition: (m: Method) -> Boolean): Method {
    this.forEach {
        if (condition(it)) {
            it.isAccessible = true
            return it
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
 * 扩展函数 通过遍历方法数组 返回符合条件的方法数组
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun Array<Method>.getMethodArrayByCondition(condition: (m: Method) -> Boolean): Array<Method> {
    return ArrayList<Method>().also { lst ->
        this.forEach {
            if (condition(it)) {
                it.isAccessible = true
                lst.add(it)
            }
        }
    }.toTypedArray()
}

/**
 * 通过条件获取方法数组
 * @param clzName 类名
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun getMethodArrayByCondition(clzName: String, condition: (m: Method) -> Boolean): Array<Method> {
    return loadClass(clzName).declaredMethods.getMethodArrayByCondition(condition)
}

/**
 * 通过条件获取方法数组
 * @param clz 类
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun getMethodArrayByCondition(clz: Class<*>, condition: (m: Method) -> Boolean): Array<Method> {
    return clz.declaredMethods.getMethodArrayByCondition(condition)
}

/**
 * 扩展函数 通过条件查找属性
 * @param condition 条件
 * @return 符合条件的属性
 * @throws NoSuchFieldError 未找到属性
 */
fun Array<Field>.findFieldByCondition(condition: (f: Field) -> Boolean): Field {
    this.forEach {
        if (condition(it)) {
            it.isAccessible = true
            return it
        }
    }
    throw NoSuchFieldError()
}

/**
 * 通过条件查找属性
 * @param clzName 类名
 * @param condition 条件
 * @return 符合条件的属性
 * @throws NoSuchFieldError 未找到属性
 */
fun findFieldByCondition(clzName: String, condition: (f: Field) -> Boolean): Field {
    return loadClass(clzName).declaredFields.findFieldByCondition(condition)
}

/**
 * 通过条件查找属性
 * @param clz 类
 * @param condition 条件
 * @return 符合条件的属性
 * @throws NoSuchFieldError 未找到属性
 */
fun findFieldByCondition(clz: Class<*>, condition: (f: Field) -> Boolean): Field {
    return clz.declaredFields.findFieldByCondition(condition)
}

/**
 * 扩展函数 通过遍历属性数组 返回符合条件的属性数组
 * @param condition 条件
 * @return 符合条件的属性数组
 */
fun Array<Field>.getFieldArrayByCondition(condition: (f: Field) -> Boolean): Array<Field> {
    return ArrayList<Field>().also { lst ->
        this.forEach {
            if (condition(it)) {
                it.isAccessible = true
                lst.add(it)
            }
        }
    }.toTypedArray()
}

/**
 * 通过条件获取属性数组
 * @param clzName 类名
 * @param condition 条件
 * @return 符合条件的属性数组
 */
fun getFieldArrayByCondition(clzName: String, condition: (f: Field) -> Boolean): Array<Field> {
    return loadClass(clzName).declaredFields.getFieldArrayByCondition(condition)
}

/**
 * 通过条件获取属性数组
 * @param clz 类
 * @param condition 条件
 * @return 符合条件的属性数组
 */
fun getFieldArrayByCondition(clz: Class<*>, condition: (f: Field) -> Boolean): Array<Field> {
    return clz.declaredFields.getFieldArrayByCondition(condition)
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
fun Any.getFieldByClassOrObject(
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
 * @param type 属性类型
 * @return 符合条件的属性
 * @throws IllegalArgumentException 属性名为空
 * @throws NoSuchFieldException 未找到属性
 */
fun Class<*>.getStaticFiledByClass(fieldName: String, type: Class<*>? = null): Field {
    if (fieldName.isEmpty()) throw IllegalArgumentException("Field name must not be null or empty!")
    return this.getFieldByClassOrObject(fieldName, true, type)
}

/**
 * 扩展函数 获取实例化对象中的对象
 *
 * 注意: 请勿对Class使用此函数
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
        val f = this.javaClass.getFieldByClassOrObject(objName, false, type)
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
 *
 * 注意: 请勿对Class使用此函数
 * @param objName 对象名称
 * @param type 类型
 * @param T 转换的类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.getObjectOrNullAs(objName: String, type: Class<*>? = null): T? {
    return this.getObjectOrNull(objName, type) as T?
}


/**
 * 扩展函数 获取实例化对象中的对象
 *
 * 注意: 请勿对Class使用此函数
 * @param objName 对象名称
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
fun Any.getObject(objName: String, type: Class<*>? = null): Any {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
    val f = this.javaClass.getFieldByClassOrObject(objName, false, type)
    f.let {
        it.isAccessible = true
        return it.get(this)!!
    }
}

/**
 * 扩展函数 获取实例化对象中的对象 并转化为类型T
 *
 * 注意: 请勿对Class使用此函数
 * @param objName 对象名称
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
fun <T> Any.getObjectAs(objName: String, type: Class<*>? = null): T {
    @Suppress("UNCHECKED_CAST")
    return this.getObject(objName, type) as T
}

/**
 * 扩展函数 获取实例化对象中的对象
 *
 * 注意: 请勿对Class使用此函数
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
 *
 * 注意: 请勿对Class使用此函数
 * @param field 属性
 * @param T 转换的类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.getObjectOrNullAs(field: Field): T? {
    return this.getObjectOrNull(field) as T?
}

/**
 * 扩展函数 通过类型 获取实例化对象中的对象
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 *
 * 注意: 请勿对Class使用此函数
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
 * 扩展函数 通过类型 获取实例化对象中的对象 并转换为T?类型
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 *
 * 注意: 请勿对Class使用此函数
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.getObjectOrNullByTypeAs(type: Class<*>): T? {
    return this.getObjectOrNullByType(type) as T?
}

/**
 * 扩展函数 通过类型 获取实例化对象中的对象
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 *
 * 注意: 请勿对Class使用此函数
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.getObjectByType(type: Class<*>): Any {
    return this.getFieldByType(type).get(this)!!
}

/**
 * 扩展函数 通过类型 获取实例化对象中的对象 并转换为T类型
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 *
 * 注意: 请勿对Class使用此函数
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 对类调用此函数
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.getObjectByTypeAs(type: Class<*>): T {
    return this.getObjectByType(type) as T
}

/**
 * 扩展函数 获取类中的静态对象
 * @param objName 需要获取的对象名
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 当名字为空时
 */
fun Class<*>.getStaticObjectOrNull(
    objName: String,
    type: Class<*>? = null
): Any? {
    try {
        if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
        val f: Field
        try {
            f = this.getStaticFiledByClass(objName, type)
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
 * @param type 类型
 * @param T 转换的类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 当名字为空时
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.getStaticObjectOrNullAs(
    objName: String,
    type: Class<*>? = null
): T? {
    return this.getStaticObjectOrNull(objName, type) as T?
}

/**
 * 扩展函数 获取类中的静态对象
 * @param objName 需要获取的对象名
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 当名字为空时
 */
fun Class<*>.getStaticObject(
    objName: String,
    type: Class<*>? = null
): Any {
    if (objName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
    this.getStaticFiledByClass(objName, type).let {
        it.isAccessible = true
        return it.get(this)!!
    }
}

/**
 * 扩展函数 获取类中的静态对象 并转换为T类型
 * @param objName 需要获取的对象名
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 当名字为空时
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.getStaticObjectAs(
    objName: String,
    type: Class<*>? = null
): T {
    return this.getStaticObject(objName, type) as T
}

/**
 * 获取Field中的对象
 * @param field 属性
 * @return 返回获取到的对象(Nullable)
 */
fun getStaticObjectOrNull(field: Field): Any? {
    field.let {
        it.isAccessible = true
        return it.get(null)
    }
}

/**
 * 获取Field中的对象 并转换为T?类型
 * @param field 属性
 * @return 返回获取到的对象(Nullable)
 */
@Suppress("UNCHECKED_CAST")
fun <T> getStaticObjectOrNullAs(field: Field): T? {
    return getStaticObjectOrNull(field) as T?
}

/**
 * 获取Field中的对象
 * @param field 属性
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
fun getStaticObject(field: Field): Any {
    field.let {
        it.isAccessible = true
        return it.get(null)!!
    }
}

/**
 * 获取Field中的对象 并转换为T类型
 * @param field 属性
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
@Suppress("UNCHECKED_CAST")
fun <T> getStaticObjectAs(field: Field): T {
    return getStaticObject(field) as T
}

/**
 * 扩展函数 通过类型 获取类中的静态对象
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
fun Class<*>.getStaticObjectByType(type: Class<*>): Any {
    return this.getStaticFieldByType(type).get(null)!!
}

/**
 * 扩展函数 通过类型 获取类中的静态对象 并转换为T类型
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.getStaticObjectByTypeAs(type: Class<*>): T {
    return this.getStaticFieldByType(type).get(null) as T
}

/**
 * 扩展函数 通过类型 获取类中的静态对象
 *
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
 * 扩展函数 通过类型 获取类中的静态对象 并转换为T？类型
 *
 * 不推荐使用 此函数只会返回第一次匹配到的对象
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.getStaticObjectOrNullByTypeAs(type: Class<*>): T? {
    return this.getStaticFieldByType(type) as T?
}

/**
 * 扩展函数 设置对象中对象的值
 *
 * 注意: 请勿对类使用此函数
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
        this.getFieldByClassOrObject(objName, false, fieldType).let {
            it.isAccessible = true
            it.set(this, value)
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

/**
 * 扩展函数 设置对象中对象的值
 *
 * 注意: 请勿对类使用此函数
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
 *
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
 *
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
 *
 * 注意: 请勿对类使用此函数
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
            m = this.getMethodByClassOrObject(methodName, false, returnType)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this)
        }
    } else {
        try {
            m = this.getMethodByClassOrObject(methodName, false, returnType, argTypes)
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
 * 扩展函数 调用对象的方法 并且将返回值转换为T?类型
 *
 * 注意: 请勿对类使用此函数
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
@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeMethodAs(
    methodName: String,
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray(),
    returnType: Class<*>? = null
): T? {
    return this.invokeMethod(methodName, args, argTypes, returnType) as T?
}

/**
 * 扩展函数 调用对象与形参表最佳匹配的方法
 * @param methodName 方法名
 * @param args 形参
 * @return 函数调用时的返回值
 * @throws IllegalArgumentException 当对象是一个Class时
 */
fun Any.invokeMethodAuto(
    methodName: String,
    vararg args: Any?
): Any? {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    return XposedHelpers.callMethod(this, methodName, *args)
}

/**
 * 扩展函数 调用对象与形参表最佳匹配的方法 并将返回值转换为T?类型
 * @param methodName 方法名
 * @param args 形参
 * @return 函数调用时的返回值
 * @throws IllegalArgumentException 当对象是一个Class时
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeMethodAutoAs(
    methodName: String,
    vararg args: Any?
): T? {
    if (this is Class<*>) throw IllegalArgumentException("Do not use it on a class!")
    return XposedHelpers.callMethod(this, methodName, *args) as T?
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
            m = this.getMethodByClassOrObject(methodName, true, returnType)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this)
        }
    } else {
        try {
            m = this.getMethodByClassOrObject(methodName, true, returnType, argTypes)
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
 * 扩展函数 调用类的静态方法 并且将返回值转换为T?类型
 * @param methodName 方法名
 * @param args 形参表 可空
 * @param argTypes 形参类型 可空
 * @param returnType 返回值类型 为null时无视返回值类型
 * @return 函数调用后的返回值
 * @throws IllegalArgumentException 当args的长度与argTypes的长度不符时
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.invokeStaticMethodAs(
    methodName: String,
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray(),
    returnType: Class<*>? = null
): T? {
    return this.invokeStaticMethod(methodName, args, argTypes, returnType) as T?
}

/**
 * 扩展函数 调用类中与形参表最佳匹配的静态方法
 * @param methodName 方法名
 * @param args 形参
 * @return 函数调用时的返回值
 */
fun Class<*>.invokeStaticMethodAuto(
    methodName: String,
    vararg args: Any?
): Any? {
    return XposedHelpers.callStaticMethod(this, methodName, *args)
}

/**
 * 扩展函数 调用类中与形参表最佳匹配的静态方法 并将返回值转换为T?类型
 * @param methodName 方法名
 * @param args 形参
 * @return 函数调用时的返回值
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.invokeStaticMethodAutoAs(
    methodName: String,
    vararg args: Any?
): Any? {
    return XposedHelpers.callStaticMethod(this, methodName, *args) as T?
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
        constructor.isAccessible = true
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
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.newInstanceAs(
    args: Array<out Any?> = emptyArray(),
    argTypes: Array<out Class<*>> = emptyArray()
): T? {
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
 * 扩展函数 调用原方法 并将返回值转换为T?类型
 * @param obj 被调用对象
 * @param args 形参表 为null时则为无参
 * @return 原方法调用后的返回值
 */
@Suppress("UNCHECKED_CAST")
fun <T> Method.invokedByOriginalAs(
    obj: Any?, args: Array<Any?>? = null
): T? {
    return XposedBridge.invokeOriginalMethod(this, obj, args) as T?
}

/**
 * 扩展函数 调用方法 并将返回值转换为T?类型
 * @param obj 被调用对象
 * @param args 形参表
 */
@Suppress("UNCHECKED_CAST")
fun <T> Method.invokeAs(obj: Any?, vararg args: Any?): T? {
    this.isAccessible = true
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
 * 扩展属性 判断是否为Interface
 */
val Member.isInterface: Boolean
    get() = Modifier.isInterface(this.modifiers)

/**
 * 扩展属性 判断是否为Native
 */
val Member.isNative: Boolean
    get() = Modifier.isNative(this.modifiers)

/**
 * 扩展属性 判断是否为Synchronized
 */
val Member.isSynchronized: Boolean
    get() = Modifier.isSynchronized(this.modifiers)

/**
 * 扩展属性 判断是否为Abstract
 */
val Member.isAbstract: Boolean
    get() = Modifier.isAbstract(this.modifiers)

/**
 * 扩展属性 判断是否为Transient
 */
val Member.isTransient: Boolean
    get() = Modifier.isTransient(this.modifiers)

/**
 * 扩展属性 判断是否为Volatile
 */
val Member.isVolatile: Boolean
    get() = Modifier.isVolatile(this.modifiers)

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

/**
 * 通过Signature获取方法
 * @param sig signature
 * @param clzLoader 类加载器
 * @return 找到的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun getMethodBySig(sig: String, clzLoader: ClassLoader = InitFields.ezXClassLoader): Method {
    return DexDescriptor.newMethodDesc(sig).getMethod(clzLoader).also { it.isAccessible = true }
}

/**
 * 通过Signature获取属性
 * @param sig signature
 * @param clzLoader 类加载器
 * @return 找到的属性
 * @throws NoSuchFieldError 未找到属性
 */
fun getFieldBySig(sig: String, clzLoader: ClassLoader = InitFields.ezXClassLoader): Field {
    return DexDescriptor.newFieldDesc(sig).getField(clzLoader).also { it.isAccessible = true }
}

/**
 * 扩展函数 通过Signature获取方法
 * @param sig signature
 * @return 找到的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun ClassLoader.getMethodBySig(sig: String): Method {
    return getMethodBySig(sig, this)
}

/**
 * 扩展函数 通过Signature获取属性
 * @param sig signature
 * @return 找到的属性
 * @throws NoSuchFieldError 未找到属性
 */
fun ClassLoader.getFieldBySig(sig: String): Field {
    return getFieldBySig(sig, this)
}

/**
 * 扩展函数 获取对象 并转换为T?类型
 * @param obj 对象
 * @return 成功时返回获取到的对象 失败时返回null
 */
@Suppress("UNCHECKED_CAST")
fun <T> Field.getAs(obj: Any?): T? {
    this.isAccessible = true
    return this.get(obj) as T?
}

/**
 * 扩展函数 获取静态对象
 * @return 成功时返回获取到的对象 失败时返回null
 */
fun Field.getStatic(): Any? {
    this.isAccessible = true
    return this.get(null)
}

/**
 * 扩展函数 获取静态对象 并转换为T?类型
 * @return 成功时返回获取到的对象 失败时返回null
 */
@Suppress("UNCHECKED_CAST")
fun <T> Field.getStaticAs(): T? {
    this.isAccessible = true
    return this.get(null) as T?
}

/**
 * 扩展函数 获取非空对象
 * @param obj 对象
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
fun Field.getNonNull(obj: Any?): Any {
    this.isAccessible = true
    return this.get(obj)!!
}

/**
 * 扩展函数 获取非空对象 并转换为T类型
 * @param obj 对象
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
@Suppress("UNCHECKED_CAST")
fun <T> Field.getNonNullAs(obj: Any?): T {
    this.isAccessible = true
    return this.get(obj) as T
}

/**
 * 扩展函数 获取静态非空对象
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
fun Field.getStaticNonNull(): Any {
    this.isAccessible = true
    return this.get(null)!!
}

/**
 * 扩展函数 获取静态非空对象 并转换为T类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 */
@Suppress("UNCHECKED_CAST")
fun <T> Field.getStaticNonNullAs(): T {
    this.isAccessible = true
    return this.get(null)!! as T
}