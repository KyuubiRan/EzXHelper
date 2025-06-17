package io.github.kyuubiran.ezxhelper.core.misc

@JvmInline
value class ParamTypes(val types: Array<out Class<*>?>)

fun paramTypes(vararg types: Class<*>?) = ParamTypes(types)

@JvmInline
value class Params(val params: Array<out Any?>)

fun params(vararg params: Any?) = Params(params)