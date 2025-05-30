@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyuubiran.ezxhelper.core.finders

import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isAbstract
import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isFinal
import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions.isPublic
import io.github.kyuubiran.ezxhelper.core.finders.base.BaseFinder

class ClassFinder private constructor(seq: Sequence<Class<*>>) : BaseFinder<Class<*>, ClassFinder>(seq) {

    override val name: String
        get() = "ClassFinder"

    companion object {

        @JvmStatic
        fun fromArray(array: Array<Class<*>>) = ClassFinder(array.asSequence())

        @JvmStatic
        fun of(vararg array: Class<*>) = ClassFinder(array.asSequence())

        @JvmStatic
        fun of(classLoader: ClassLoader = ClassLoaderProvider.safeClassLoader, vararg className: String) {
            val classes = className.map { Class.forName(it, false, classLoader) }
            ClassFinder(classes.asSequence())
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Class<*>>) = ClassFinder(iterable.asSequence())

        @JvmStatic
        fun fromSequence(seq: Sequence<Class<*>>) = ClassFinder(seq)
    }

    fun filterPackage(packageName: String) = filter { name.startsWith(packageName) }


    fun filterHasFieldType(type: Class<*>) = filter { fields.any { field -> field.type == type } }

    fun filterHasFieldTypeAndCount(type: Class<*>, count: Int) = filter { fields.count { field -> field.type == type } == count }

    @JvmOverloads
    fun filterHasFieldTypeAndCountIn(type: Class<*>, min: Int = 1, max: Int = Int.MAX_VALUE) = filter {
        val count = fields.count { field -> field.type == type }
        count in min..max
    }

    fun filterHasFieldTypeAndCountIn(type: Class<*>, range: IntRange) = filter {
        val count = fields.count { field -> field.type == type }
        count in range
    }

    fun filterHasFieldTypeName(typeName: String) = filter { fields.any { field -> field.type.name == typeName } }

    fun filterHasFieldName(fieldName: String) = filter { fields.any { field -> field.name == fieldName } }

    fun filterHasMethodName(methodName: String) = filter { methods.any { method -> method.name == methodName } }

    fun filterHasMethodReturnType(returnType: Class<*>) = filter { methods.any { method -> method.returnType == returnType } }

    fun filterHasMethodSignature(returnType: Class<*>, vararg paramTypes: Class<*>) = filter {
        methods.any { method -> method.returnType == returnType && method.parameterTypes.contentEquals(paramTypes) }
    }

    fun filterHasConstructorSignature(vararg paramTypes: Class<*>) = filter {
        constructors.any { constructor -> constructor.parameterTypes.contentEquals(paramTypes) }
    }

    fun filterImplementInterfaces(vararg interfaces: Class<*>) = filter {
        interfaces.all { interfaceClass -> interfaceClass.isAssignableFrom(this) }
    }

    fun filterHasConstructorCount(cnt: Int) = filter { constructors.count() == cnt }

    fun filterHasConstructorCountIn(range: IntRange) = filter { constructors.count() in range }

    @JvmOverloads
    fun filterHasConstructorCountIn(min: Int = 1, max: Int = Int.MAX_VALUE) = filter { constructors.count() in min..max }

    fun filterIsSubclassOf(superclass: Class<*>) = filter { superclass.isAssignableFrom(this) }

    fun filterIsAbstract() = filter { isAbstract }

    fun filterIsNotAbstract() = filter { !isAbstract }

    fun filterIsInterface() = filter { isInterface }

    fun filterIsNotInterface() = filter { !isInterface }

    fun filterIsEnum() = filter { isEnum }

    fun filterIsNotEnum() = filter { !isEnum }

    fun filterIsAnnotation() = filter { isAnnotation }

    fun filterIsNotAnnotation() = filter { !isAnnotation }

    fun filterIsPublic() = filter { isPublic }

    fun filterIsNotPublic() = filter { !isPublic }

    fun filterIsFinal() = filter { isFinal }

    fun filterIsNotFinal() = filter { !isFinal }

    fun filterIsSynthetic() = filter { isSynthetic }

    fun filterIsNotSynthetic() = filter { !isSynthetic }

    fun filterIsAnonymous() = filter { isAnonymousClass }

    fun filterIsNotAnonymous() = filter { !isAnonymousClass }

    fun filterIsLocal() = filter { isLocalClass }

    fun filterIsNotLocal() = filter { !isLocalClass }

    fun filterIsMember() = filter { isMemberClass }

    fun filterIsNotMember() = filter { !isMemberClass }

    fun filterIsPrimitive() = filter { isPrimitive }

    fun filterIsNotPrimitive() = filter { !isPrimitive }

    fun filterIsArray() = filter { isArray }

    fun filterIsNotArray() = filter { !isArray }

    fun filterIsAnnotationPresent(annotation: Class<out Annotation>) = filter { isAnnotationPresent(annotation) }

    fun filterIsNotAnnotationPresent(annotation: Class<out Annotation>) = filter { !isAnnotationPresent(annotation) }

    override fun newFinder(sequence: Sequence<Class<*>>): ClassFinder = ClassFinder(sequence)
}
