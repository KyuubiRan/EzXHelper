@file:Suppress("unused")

package com.github.kyuubiran.ezxhelper.misc

class FinderExceptionMessage {
    val exceptionMessageBuilder = StringBuilder()
    val msg: String
        get() = exceptionMessageBuilder.append("Stacktrace:").toString()

    inline fun <reified Self> ctor(msg: String) {
        exceptionMessageBuilder.apply {
            append("[${Self::class.java.simpleName}] ")
            append(msg)
            append("\nConditions:\n")
        }
    }

    fun append(msg: String, newLine: Boolean = true) {
        exceptionMessageBuilder.append(msg).apply { if (newLine) append("\n") }
    }

    fun condition(msg: String) {
        exceptionMessageBuilder.append("\t$msg\n")
    }

}