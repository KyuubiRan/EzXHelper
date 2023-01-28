package com.example.sample

import com.github.kyuubiran.ezxhelper.ObjectUtils

fun getObjectOrNull() {
    val obj = object {
        val field = "Hello World"
    }
    val field = ObjectUtils.getObjectOrNull(obj, "field")
    println(field)
}


