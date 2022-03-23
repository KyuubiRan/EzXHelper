package com.github.kyuubiran.ezxhelper.utils

import com.github.kyuubiran.ezxhelper.init.InitFields
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.*

//region LoadClass

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
 * 尝试加载一个类 如果失败则返回null
 * @param clzName 类名
 * @param clzLoader 类加载器
 * @return 被加载的类
 */
fun loadClassOrNull(
    clzName: String,
    clzLoader: ClassLoader = InitFields.ezXClassLoader
): Class<*>? {
    if (clzName.isEmpty()) throw  IllegalArgumentException("Class name must not be null or empty!")
    return XposedHelpers.findClassIfExists(clzName, clzLoader)
}

/**
 * 扩展函数 加载数组中的所有类
 * @param clzLoader 类加载器
 * @return 类数组
 */
fun Array<String>.loadAllClasses(clzLoader: ClassLoader = InitFields.ezXClassLoader): Array<Class<*>> {
    return Array(this.size) { i -> loadClass(this[i], clzLoader) }
}

fun Iterable<String>.loadAllClasses(clzLoader: ClassLoader = InitFields.ezXClassLoader): List<Class<*>> {
    return this.map { loadClass(it, clzLoader) }
}

/**
 * 扩展函数 尝试加载数组中的所有类
 * @param clzLoader 类加载器
 * @return 加载成功的类数组
 */
fun Array<String>.loadClassesIfExists(clzLoader: ClassLoader = InitFields.ezXClassLoader): Array<Class<*>> {
    return this.mapNotNull { loadClassOrNull(it, clzLoader) }.toTypedArray()
}

fun Iterable<String>.loadClassesIfExists(clzLoader: ClassLoader = InitFields.ezXClassLoader): List<Class<*>> {
    return this.mapNotNull { loadClassOrNull(it, clzLoader) }
}

//endregion

//region Argdef

@JvmInline
value class Args(val args: Array<out Any?>)

@JvmInline
value class ArgTypes(val argTypes: Array<out Class<*>>)

fun args(vararg args: Any?) = Args(args)

fun argTypes(vararg argTypes: Class<*>) = ArgTypes(argTypes)

//endregion

//region MethodReflect

/**
 * 获取类的所有方法
 * @param clzName 类名
 * @param clzLoader 类加载器
 * @return 方法数组
 * @throws IllegalArgumentException 类名为空
 */
fun getDeclaredMethods(
    clzName: String,
    clzLoader: ClassLoader = InitFields.ezXClassLoader
): Array<Method> {
    if (clzName.isEmpty()) throw IllegalArgumentException("Class name must not be null or empty!")
    return loadClass(clzName, clzLoader).declaredMethods
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
 * @param clzLoader 类加载器
 * @return 属性数组
 * @throws IllegalArgumentException 类名为空
 */
fun getFields(clzName: String, clzLoader: ClassLoader = InitFields.ezXClassLoader): Array<Field> {
    if (clzName.isEmpty()) throw  IllegalArgumentException("Class name must not be null or empty!")
    return loadClass(clzName, clzLoader).declaredFields
}

/**
 * 扩展函数 通过类或者对象获取单个方法
 * @param methodName 方法名
 * @param isStatic 是否为静态方法
 * @param returnType 方法返回值 填入null为无视返回值
 * @param argTypes 方法参数类型
 * @return 符合条件的方法
 * @throws IllegalArgumentException 方法名为空
 * @throws NoSuchMethodException 未找到方法
 */
fun Any.getMethodByClassOrObject(
    methodName: String,
    returnType: Class<*>? = null,
    isStatic: Boolean = false,
    argTypes: ArgTypes = argTypes()
): Method {
    if (methodName.isEmpty()) throw IllegalArgumentException("Method name must not be null or empty!")
    var c = if (this is Class<*>) this else this.javaClass
    do {
        c.declaredMethods.toList().stream()
            .filter { it.name == methodName }
            .filter { it.parameterTypes.size == argTypes.argTypes.size }
            .apply { if (returnType != null) filter { returnType == it.returnType } }
            .filter { it.parameterTypes.indices.all { i -> it.parameterTypes[i] == argTypes.argTypes[i] } }
            .filter { it.isStatic == isStatic }
            .runCatching { findFirst().get().let { it.isAccessible = true;return it } }
    } while (c.superclass?.also { c = it } != null)
    throw NoSuchMethodException()
}

/**
 * 扩展函数 通过类获取单个静态方法
 * @param methodName 方法名
 * @param returnType 方法返回值 填入null为无视返回值
 * @param argTypes 方法参数类型
 * @throws IllegalArgumentException 方法名为空
 */
fun Class<*>.getStaticMethodByClass(
    methodName: String,
    returnType: Class<*>? = null,
    argTypes: ArgTypes = argTypes()
): Method {
    if (methodName.isEmpty()) throw IllegalArgumentException("Method name must not be null or empty!")
    return this.getMethodByClassOrObject(methodName, returnType, true, argTypes = argTypes)
}

typealias MethodCondition = Method.() -> Boolean

/**
 * 通过条件查找类中的方法
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法
 * @throws NoSuchMethodException
 */
fun findMethod(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: MethodCondition
): Method {
    return findMethodOrNull(clz, findSuper, condition) ?: throw NoSuchMethodException()
}

/**
 * 通过条件查找类中的方法
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法 未找到时返回null
 */
fun findMethodOrNull(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: MethodCondition
): Method? {
    var c = clz
    c.declaredMethods.firstOrNull { it.condition() }
        ?.let { it.isAccessible = true;return it }

    if (findSuper) {
        while (c.superclass?.also { c = it } != null) {
            c.declaredMethods
                .firstOrNull { it.condition() }
                ?.let { it.isAccessible = true;return it }
        }
    }
    return null
}

/**
 * 通过条件查找方法
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun findMethod(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): Method {
    return findMethod(loadClass(clzName, classLoader), findSuper, condition)
}

/**
 * 通过条件查找类中的方法
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法 未找到时返回null
 */
fun findMethodOrNull(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): Method? {
    return findMethodOrNull(loadClass(clzName, classLoader), findSuper, condition)
}

/**
 *  扩展函数 通过条件查找方法
 *  @param condition 方法的条件
 *  @return 符合条件的方法
 *  @throws NoSuchMethodException 未找到方法
 */
fun Array<Method>.findMethod(condition: MethodCondition): Method {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
        ?: throw NoSuchMethodException()
}

fun Iterable<Method>.findMethod(condition: MethodCondition): Method {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
        ?: throw NoSuchMethodException()
}

/**
 *  扩展函数 通过条件查找方法
 *  @param condition 方法的条件
 *  @return 符合条件的方法 未找到时返回null
 */
fun Array<Method>.findMethodOrNull(condition: MethodCondition): Method? {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

fun Iterable<Method>.findMethodOrNull(condition: MethodCondition): Method? {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

/**
 * 扩展函数 通过条件查找方法 每个类只搜索一个方法
 * @param findSuper 是否查找父类
 * @param condition 方法条件
 * @return 方法数组
 */
fun Array<Class<*>>.findMethods(
    findSuper: Boolean = false,
    condition: MethodCondition
): Array<Method> {
    val arr = ArrayList<Method>()
    this.forEach { clz ->
        arr.tryAdd { findMethod(clz, findSuper, condition) }
    }
    return arr.toTypedArray()
}

fun Iterable<Class<*>>.findMethods(
    findSuper: Boolean = false,
    condition: MethodCondition
): List<Method> {
    val arr = ArrayList<Method>()
    this.forEach { clz ->
        arr.tryAdd { findMethod(clz, findSuper, condition) }
    }
    return arr
}

/**
 * 扩展函数 加载数组中的类并且通过条件查找方法 每个类只搜索一个方法
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 方法条件
 * @return 方法数组
 */
fun Array<String>.loadAndFindMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): Array<Method> {
    return this.loadAllClasses(classLoader).findMethods(findSuper, condition)
}

fun Iterable<String>.loadAndFindMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): List<Method> {
    return this.loadAllClasses(classLoader).findMethods(findSuper, condition)
}

// Method condition pair
infix fun String.mcp(condition: MethodCondition) = this to condition
infix fun Class<*>.mcp(condition: MethodCondition) = this to condition

/**
 * 扩展函数 通过条件查找数组中对应的方法 每个类只搜索一个方法
 * @param findSuper 是否查找父类
 * @return 方法数组
 */
fun Array<Pair<Class<*>, MethodCondition>>.findMethods(
    findSuper: Boolean = false
): Array<Method> {
    return this.map { (k, v) -> findMethod(k, findSuper, v) }.toTypedArray()
}

fun Iterable<Pair<Class<*>, MethodCondition>>.findMethods(
    findSuper: Boolean = false
): List<Method> {
    return this.map { (k, v) -> findMethod(k, findSuper, v) }
}

/**
 * 扩展函数 加载数组中的类并且通过条件查找方法 每个类只搜索一个方法
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @return 方法数组
 */
fun Array<Pair<String, MethodCondition>>.loadAndFindMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false
): Array<Method> {
    return this.map { (k, v) -> findMethod(loadClass(k, classLoader), findSuper, v) }.toTypedArray()
}

fun Iterable<Pair<String, MethodCondition>>.loadAndFindMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false
): List<Method> {
    return this.map { (k, v) -> findMethod(loadClass(k, classLoader), findSuper, v) }
}

/**
 * 扩展函数 通过条件搜索所有方法
 * @param findSuper 是否查找父类
 * @param condition 方法条件
 * @return 方法数组
 */
fun Array<Class<*>>.findAllMethods(
    findSuper: Boolean = false,
    condition: MethodCondition
): Array<Method> {
    return this.flatMap { c -> findAllMethods(c, findSuper, condition).toList() }.toTypedArray()
}

fun Iterable<Class<*>>.findAllMethods(
    findSuper: Boolean = false,
    condition: MethodCondition
): List<Method> {
    return this.flatMap { c -> findAllMethods(c, findSuper, condition).toList() }
}

/**
 * 扩展函数 加载数组中的类并且通过条件查找方法
 * @param findSuper 是否查找父类
 * @return 方法数组
 */
fun Array<Pair<Class<*>, MethodCondition>>.findAllMethods(
    findSuper: Boolean = false
): Array<Method> {
    return arrayListOf<Method>()
        .apply { this@findAllMethods.forEach { (k, v) -> addAll(findAllMethods(k, findSuper, v)) } }
        .toTypedArray()
}

fun Iterable<Pair<Class<*>, MethodCondition>>.findAllMethods(
    findSuper: Boolean = false
): List<Method> {
    return arrayListOf<Method>()
        .apply { this@findAllMethods.forEach { (k, v) -> addAll(findAllMethods(k, findSuper, v)) } }
}

/**
 * 扩展函数 加载数组中的类并且通过条件查找方法
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @return 方法数组
 */
fun Array<Pair<String, MethodCondition>>.loadAndFindAllMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false
): Array<Method> {
    return this.map { (k, v) -> loadClass(k, classLoader) to v }.toTypedArray()
        .findAllMethods(findSuper)
}

fun Iterable<Pair<String, MethodCondition>>.loadAndFindAllMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false
): List<Method> {
    return this.map { (k, v) -> loadClass(k, classLoader) to v }.findAllMethods(findSuper)
}

/**
 * 扩展函数 加载数组中的类并且通过条件查找所有方法
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 方法条件
 * @return 方法数组
 */
fun Array<String>.loadAndFindAllMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): Array<Method> {
    return this.loadAllClasses(classLoader).findAllMethods(findSuper, condition)
}

fun Iterable<String>.loadAndFindAllMethods(
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): List<Method> {
    return this.loadAllClasses(classLoader).findAllMethods(findSuper, condition)
}

typealias ConstructorCondition = Constructor<*>.() -> Boolean

/**
 *  扩展函数 通过条件查找构造方法
 *  @param condition 构造方法的条件
 *  @return 符合条件的构造方法
 *  @throws NoSuchMethodException 未找到构造方法
 */
fun Array<Constructor<*>>.findConstructor(condition: ConstructorCondition): Constructor<*> {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
        ?: throw NoSuchMethodException()
}

fun Iterable<Constructor<*>>.findConstructor(condition: ConstructorCondition): Constructor<*> {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
        ?: throw NoSuchMethodException()
}

/**
 *  扩展函数 通过条件查找构造方法
 *  @param condition 构造方法的条件
 *  @return 符合条件的构造方法 未找到时返回null
 */
fun Array<Constructor<*>>.findConstructorOrNull(condition: ConstructorCondition): Constructor<*>? {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

fun Iterable<Constructor<*>>.findConstructorOrNull(condition: ConstructorCondition): Constructor<*>? {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

/**
 * 通过条件查找构造方法
 * @param clz 类
 * @param condition 条件
 * @return 符合条件的构造方法
 * @throws NoSuchMethodException 未找到构造方法
 */
fun findConstructor(
    clz: Class<*>,
    condition: ConstructorCondition
): Constructor<*> {
    return clz.declaredConstructors.findConstructor(condition)
}

/**
 * 通过条件查找构造方法
 * @param clz 类
 * @param condition 条件
 * @return 符合条件的构造方法 未找到时返回null
 */
fun findConstructorOrNull(
    clz: Class<*>,
    condition: ConstructorCondition
): Constructor<*>? {
    return clz.declaredConstructors.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

/**
 * 通过条件查找构造方法
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param condition 条件
 * @return 符合条件的构造方法
 * @throws NoSuchMethodException 未找到构造方法
 */
fun findConstructor(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    condition: ConstructorCondition
): Constructor<*> {
    return loadClass(clzName, classLoader).declaredConstructors.findConstructor(condition)
}

/**
 * 通过条件查找构造方法
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param condition 条件
 * @return 符合条件的构造方法 未找到时返回null
 */
fun findConstructorOrNull(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    condition: ConstructorCondition
): Constructor<*>? {
    return loadClass(clzName, classLoader).declaredConstructors.findConstructorOrNull(condition)
}

/**
 * 扩展函数 通过遍历方法数组 返回符合条件的方法数组
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun Array<Method>.findAllMethods(condition: MethodCondition): Array<Method> {
    return this.filter { it.condition() }.onEach { it.isAccessible = true }.toTypedArray()
}

fun Iterable<Method>.findAllMethods(condition: MethodCondition): List<Method> {
    return this.filter { it.condition() }.onEach { it.isAccessible = true }.toList()
}

/**
 * 通过条件获取方法数组
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun findAllMethods(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: MethodCondition
): Array<Method> {
    var c = clz
    val arr = ArrayList<Method>()
    arr.addAll(c.declaredMethods.findAllMethods(condition))
    if (findSuper) {
        while (c.superclass?.also { c = it } != null) {
            arr.addAll(c.declaredMethods.findAllMethods(condition))
        }
    }
    return arr.toTypedArray()
}

/**
 * 通过条件获取方法数组
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun findAllMethods(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): Array<Method> {
    return findAllMethods(loadClass(clzName, classLoader), findSuper, condition)
}

//endregion

//region FieldReflect

/**
 * 通过条件查找类中的属性
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的属性
 * @throws NoSuchFieldException
 */
fun findField(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: FieldCondition
): Field {
    return findFieldOrNull(clz, findSuper, condition) ?: throw NoSuchFieldException()
}

/**
 * 通过条件查找类中的属性
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的属性 未找到时返回null
 */
fun findFieldOrNull(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: FieldCondition
): Field? {
    var c = clz
    c.declaredFields.firstOrNull { it.condition() }?.let {
        it.isAccessible = true;return it
    }
    if (findSuper) {
        while (c.superclass?.also { c = it } != null) {
            c.declaredFields.firstOrNull { it.condition() }
                ?.let { it.isAccessible = true;return it }
        }
    }
    return null
}

/**
 * 通过条件查找类中的属性
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的属性
 * @throws NoSuchFieldException 未找到属性
 */
fun findField(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: FieldCondition
): Field {
    return findField(loadClass(clzName, classLoader), findSuper, condition)
}

/**
 * 通过条件查找类中的属性
 * @param clzName 类名
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的属性 未找到时返回null
 */
fun findFieldOrNull(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: FieldCondition
): Field? {
    return findFieldOrNull(loadClass(clzName, classLoader), findSuper, condition)
}

/**
 * 扩展函数 通过条件查找属性
 * @param condition 条件
 * @return 符合条件的属性
 * @throws NoSuchFieldException 未找到属性
 */
fun Array<Field>.findField(condition: FieldCondition): Field {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
        ?: throw NoSuchFieldException()
}

fun Iterable<Field>.findField(condition: FieldCondition): Field {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
        ?: throw NoSuchFieldException()
}

/**
 * 扩展函数 通过条件查找属性
 * @param condition 条件
 * @return 符合条件的属性 未找到时返回null
 */
fun Array<Field>.findFieldOrNull(condition: FieldCondition): Field? {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

fun Iterable<Field>.findFieldOrNull(condition: FieldCondition): Field? {
    return this.firstOrNull { it.condition() }?.also { it.isAccessible = true }
}

/**
 * 扩展函数 通过遍历属性数组 返回符合条件的属性数组
 * @param condition 条件
 * @return 符合条件的属性数组
 */
fun Array<Field>.findAllFields(condition: FieldCondition): Array<Field> {
    return this.filter { it.condition() }.onEach { it.isAccessible = true }.toTypedArray()
}

fun Iterable<Field>.findAllFields(condition: FieldCondition): List<Field> {
    return this.filter { it.condition() }.map { it.isAccessible = true;it }
}

/**
 * 通过条件获取属性数组
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的属性数组
 */
fun findAllFields(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: FieldCondition
): Array<Field> {
    var c = clz
    val arr = ArrayList<Field>()
    arr.addAll(c.declaredFields.findAllFields(condition))
    if (findSuper) {
        while (c.superclass?.also { c = it } != null) {
            arr.addAll(c.declaredFields.findAllFields(condition))
        }
    }
    return arr.toTypedArray()
}

/**
 * 通过条件获取属性数组
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的属性数组
 */
fun findAllFields(
    clzName: String,
    classLoader: ClassLoader = InitFields.ezXClassLoader,
    findSuper: Boolean = false,
    condition: FieldCondition
): Array<Field> {
    return findAllFields(loadClass(clzName, classLoader), findSuper, condition)
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
    var c: Class<*> = if (this is Class<*>) this else this.javaClass
    do {
        c.declaredFields
            .filter { isStatic == it.isStatic }
            .firstOrNull { (fieldType == null || it.type == fieldType) && (it.name == fieldName) }
            ?.let { it.isAccessible = true;return it }
    } while (c.superclass?.also { c = it } != null)
    throw NoSuchFieldException(fieldName)
}

/**
 * 扩展函数 通过类型获取属性
 * @param type 类型
 * @param isStatic 是否静态
 * @return 符合条件的属性
 * @throws NoSuchFieldException 未找到属性
 */
fun Any.getFieldByType(type: Class<*>, isStatic: Boolean = false): Field {
    var c: Class<*> = if (this is Class<*>) this else this.javaClass
    do {
        c.declaredFields
            .filter { isStatic == it.isStatic }
            .firstOrNull { it.type == type }
            ?.let { it.isAccessible = true;return it }
    } while (c.superclass?.also { c = it } != null)
    throw NoSuchFieldException()
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
    return this.get(obj)!! as T
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

//endregion

//region ObjectReflect

typealias ObjectCondition = Any?.() -> Boolean

/**
 * 强烈不推荐!!非常慢!!
 *
 * 扩展函数 遍历对象中的属性并返回符合条件的对象
 * @param condition 条件
 * @return 成功时返回找到的对象 失败时返回null
 */
fun Any.findObject(condition: ObjectCondition): Any? {
    for (f in this::class.java.declaredFields) {
        f.isAccessible = true
        f.get(this).let {
            if (it.condition()) return it
        }
    }
    return null
}

typealias FieldCondition = Field.() -> Boolean

/**
 * 强烈不推荐!!非常慢!!
 *
 * 扩展函数 遍历对象中的属性并返回符合条件的对象
 * @param fieldCond 属性条件
 * @param objCond 对象条件
 * @return 成功时返回找到的对象 失败时返回null
 */
fun Any.findObject(
    fieldCond: FieldCondition,
    objCond: ObjectCondition
): Any? {
    for (f in this::class.java.declaredFields) {
        if (f.fieldCond()) {
            f.isAccessible = true
            f.get(this).let {
                if (it.objCond()) return it
            }
        }
    }
    return null
}

/**
 * 强烈不推荐!!非常慢!!
 *
 * 扩展函数 遍历类中的静态属性并返回符合条件的静态对象
 * @param condition 条件
 * @return 成功时返回找到的静态对象 失败时返回null
 */
fun Class<*>.findStaticObject(condition: ObjectCondition): Any? {
    for (f in this.declaredFields) {
        if (!f.isStatic) continue
        f.isAccessible = true
        f.get(null).let {
            if (it.condition()) return it
        }
    }
    return null
}

/**
 * 强烈不推荐!!非常慢!!
 *
 * 扩展函数 遍历类中的静态属性并返回符合条件的静态对象
 * @param fieldCond 属性条件
 * @param objCond 对象条件
 * @return 成功时返回找到的静态对象 失败时返回null
 */
fun Any.findStaticObject(
    fieldCond: FieldCondition,
    objCond: ObjectCondition
): Any? {
    for (f in this::class.java.declaredFields) {
        if (!f.isStatic) continue
        if (f.fieldCond()) {
            f.isAccessible = true
            f.get(this).let {
                if (it.objCond()) return it
            }
        }
    }
    return null
}

/**
 * 扩展函数 获取实例化对象中的对象
 * @param objName 对象名称
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
fun Any.getObjectOrNull(objName: String, type: Class<*>? = null): Any? {
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
 * @param objName 对象名称
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时抛出异常
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 目标对象名为空
 */
fun Any.getObject(objName: String, type: Class<*>? = null): Any {
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
 * @param field 属性
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.getObjectOrNull(field: Field): Any? {
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
 * @param type 类型
 * @return 成功时返回获取到的对象 失败时返回null
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.getObjectOrNullByType(type: Class<*>): Any? {
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
 * @param objName 需要设置的对象名称
 * @param value 值
 * @param fieldType 对象类型
 * @throws IllegalArgumentException 对类调用此函数
 * @throws IllegalArgumentException 对象名为空
 */
fun Any.putObject(objName: String, value: Any?, fieldType: Class<*>? = null) {
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
 * @param field 属性
 * @param value 值
 * @throws IllegalArgumentException 对类调用此函数
 */
fun Any.putObject(field: Field, value: Any?) {
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
        val f = try {
            this.getStaticFiledByClass(objName, fieldType)
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

//endregion

//region MethodInvoke

/**
 * 扩展函数 调用对象中符合条件的方法
 * @param args 参数
 * @param condition 条件
 * @return 方法的返回值
 * @throws NoSuchMethodException 未找到方法
 */
fun Any.invokeMethod(vararg args: Any?, condition: MethodCondition): Any? {
    this::class.java.declaredMethods.firstOrNull { it.condition() }
        ?.let { it.isAccessible = true;return it(this, *args) }
    throw NoSuchMethodException()
}

/**
 * 扩展函数 调用类中符合条件的静态方法
 * @param args 参数表
 * @param condition 条件
 * @return 方法的返回值
 * @throws NoSuchMethodException 未找到方法
 */
fun Class<*>.invokeStaticMethod(
    vararg args: Any?,
    condition: MethodCondition
): Any? {
    this::class.java.declaredMethods.firstOrNull { it.isStatic && it.condition() }
        ?.let { it.isAccessible = true;return it(this, *args) }
    throw NoSuchMethodException()
}

/**
 * 扩展函数 调用对象的方法
 *
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
    args: Args = args(),
    argTypes: ArgTypes = argTypes(),
    returnType: Class<*>? = null
): Any? {
    if (methodName.isEmpty()) throw IllegalArgumentException("Object name must not be null or empty!")
    if (args.args.size != argTypes.argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    val m: Method
    if (args.args.isEmpty()) {
        try {
            m = this.getMethodByClassOrObject(methodName, returnType, false)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this)
        }
    } else {
        try {
            m = this.getMethodByClassOrObject(methodName, returnType, false, argTypes = argTypes)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this, *args.args)
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
    args: Args = args(),
    argTypes: ArgTypes = argTypes(),
    returnType: Class<*>? = null
): T? {
    return this.invokeMethod(methodName, args, argTypes, returnType) as T?
}

/**
 * 扩展函数 调用对象与形参表最佳匹配的方法
 * @param methodName 方法名
 * @param args 形参
 * @return 函数调用时的返回值
 */
fun Any.invokeMethodAuto(
    methodName: String,
    vararg args: Any?
): Any? {
    return XposedHelpers.callMethod(this, methodName, *args)
}

/**
 * 扩展函数 调用对象与形参表最佳匹配的方法 并将返回值转换为T?类型
 * @param methodName 方法名
 * @param args 形参
 * @return 函数调用时的返回值
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeMethodAutoAs(
    methodName: String,
    vararg args: Any?
): T? {
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
    args: Args = args(),
    argTypes: ArgTypes = argTypes(),
    returnType: Class<*>? = null
): Any? {
    if (args.args.size != argTypes.argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    val m: Method
    if (args.args.isEmpty()) {
        try {
            m = this.getMethodByClassOrObject(methodName, returnType, true)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this)
        }
    } else {
        try {
            m = this.getMethodByClassOrObject(methodName, returnType, true, argTypes = argTypes)
        } catch (e: NoSuchMethodException) {
            return null
        }
        m.let {
            it.isAccessible = true
            return it.invoke(this, *args.args)
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
    args: Args = args(),
    argTypes: ArgTypes = argTypes(),
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
): T? {
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
    args: Args = args(),
    argTypes: ArgTypes = argTypes()
): Any? {
    if (args.args.size != argTypes.argTypes.size) throw IllegalArgumentException("Method args size must equals argTypes size!")
    return try {
        val constructor: Constructor<*> =
            if (argTypes.argTypes.isNotEmpty())
                this.getDeclaredConstructor(*argTypes.argTypes)
            else
                this.getDeclaredConstructor()
        constructor.isAccessible = true

        if (args.args.isEmpty())
            constructor.newInstance()
        else
            constructor.newInstance(*args.args)
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
    args: Args = args(),
    argTypes: ArgTypes = argTypes()
): T? {
    return this.newInstance(args, argTypes) as T?
}

/**
 * 扩展函数 调用原方法
 * @param obj 被调用对象
 * @param args 形参表 为null时则为无参
 * @return 原方法调用后的返回值
 */
fun Method.invokedOriginal(obj: Any?, args: Array<Any?>? = null): Any? {
    return XposedBridge.invokeOriginalMethod(this, obj, args)
}

/**
 * 扩展函数 调用原方法 并将返回值转换为T?类型
 * @param obj 被调用对象
 * @param args 形参表 为null时则为无参
 * @return 原方法调用后的返回值
 */
@Suppress("UNCHECKED_CAST")
fun <T> Method.invokedOriginalAs(
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
    return this.invoke(obj, *args) as T?
}

//endregion

//region MemberExtension

/**
 * 扩展属性 判断是否为Static
 */
val Member.isStatic: Boolean
    inline get() = Modifier.isStatic(this.modifiers)
val Member.isNotStatic: Boolean
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
 * 扩展属性 判断是否为Final
 */
val Member.isFinal: Boolean
    inline get() = Modifier.isFinal(this.modifiers)
val Member.isNotFinal: Boolean
    inline get() = !this.isFinal

val Class<*>.isFinal: Boolean
    inline get() = Modifier.isFinal(this.modifiers)
val Class<*>.isNotFinal: Boolean
    inline get() = !this.isFinal

/**
 * 扩展属性 判断是否为Interface
 */
val Member.isInterface: Boolean
    inline get() = Modifier.isInterface(this.modifiers)
val Member.isNotInterface: Boolean
    inline get() = !this.isInterface

/**
 * 扩展属性 判断是否为Native
 */
val Member.isNative: Boolean
    inline get() = Modifier.isNative(this.modifiers)
val Member.isNotNative: Boolean
    inline get() = !this.isNative

/**
 * 扩展属性 判断是否为Synchronized
 */
val Member.isSynchronized: Boolean
    inline get() = Modifier.isSynchronized(this.modifiers)
val Member.isNotSynchronized: Boolean
    inline get() = !this.isSynchronized

/**
 * 扩展属性 判断是否为Abstract
 */
val Member.isAbstract: Boolean
    inline get() = Modifier.isAbstract(this.modifiers)
val Member.isNotAbstract: Boolean
    inline get() = !this.isAbstract

val Class<*>.isAbstract: Boolean
    inline get() = Modifier.isAbstract(this.modifiers)
val Class<*>.isNotAbstract: Boolean
    inline get() = !this.isAbstract

/**
 * 扩展属性 判断是否为Transient
 */
val Member.isTransient: Boolean
    inline get() = Modifier.isTransient(this.modifiers)
val Member.isNotTransient: Boolean
    inline get() = !this.isTransient

/**
 * 扩展属性 判断是否为Volatile
 */
val Member.isVolatile: Boolean
    inline get() = Modifier.isVolatile(this.modifiers)
val Member.isNotVolatile: Boolean
    inline get() = !this.isVolatile

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
val Method.emptyParam: Boolean
    inline get() = this.paramCount == 0
val Method.notEmptyParam: Boolean
    inline get() = this.paramCount != 0

/**
 * 扩展属性 判断构造方法的参数是否为空
 */
val Constructor<*>.emptyParam: Boolean
    inline get() = this.paramCount == 0
val Constructor<*>.notEmptyParam: Boolean
    inline get() = this.paramCount != 0

//endregion

//region Descriptor

/**
 * 通过Descriptor获取方法
 * @param desc Descriptor
 * @param clzLoader 类加载器
 * @return 找到的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun getMethodByDesc(
    desc: String,
    clzLoader: ClassLoader = InitFields.ezXClassLoader
): Method {
    return DexDescriptor.newMethodDesc(desc).getMethod(clzLoader).also { it.isAccessible = true }
}

/**
 * 通过Descriptor获取属性
 * @param desc Descriptor
 * @param clzLoader 类加载器
 * @return 找到的属性
 * @throws NoSuchFieldException 未找到属性
 */
fun getFieldByDesc(desc: String, clzLoader: ClassLoader = InitFields.ezXClassLoader): Field {
    return DexDescriptor.newFieldDesc(desc).getField(clzLoader).also { it.isAccessible = true }
}

/**
 * 扩展函数 通过Descriptor获取方法
 * @param desc Descriptor
 * @return 找到的方法
 * @throws NoSuchMethodException 未找到方法
 */
fun ClassLoader.getMethodByDesc(desc: String): Method {
    return getMethodByDesc(desc, this)
}

/**
 * 扩展函数 通过Descriptor获取属性
 * @param desc Descriptor
 * @return 找到的属性
 * @throws NoSuchFieldException 未找到属性
 */
fun ClassLoader.getFieldByDesc(desc: String): Field {
    return getFieldByDesc(desc, this)
}

//endregion
