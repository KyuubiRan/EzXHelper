package com.github.kyuubiran.ezxhelper.annotations

/**
 * This annotation is used to mark that the annotated element is only available in Kotlin
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
internal annotation class KotlinOnly
