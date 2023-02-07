package com.github.kyuubiran.ezxhelper

@JvmInline
value class ParamTypes(val types: Array<out Class<*>?>)

fun paramTypes(vararg types: Class<*>?) = ParamTypes(types)

@JvmInline
value class Params(val params: Array<out Any?>)

fun params(vararg params: Any?) = Params(params)