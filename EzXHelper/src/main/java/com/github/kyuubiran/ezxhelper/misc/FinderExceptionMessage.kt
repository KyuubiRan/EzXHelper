@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.misc

import com.github.kyuubiran.ezxhelper.interfaces.INamed

class FinderExceptionMessage {
    val exceptionMessageBuilder = StringBuilder()
    val msg: String
        get() = exceptionMessageBuilder.append("Stacktrace:").toString()

    fun <NamedFinder : INamed> ctor(finder: NamedFinder, msg: String) {
        exceptionMessageBuilder.apply {
            // fix r8 cause name lost
            append("[${finder.name}] ")
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