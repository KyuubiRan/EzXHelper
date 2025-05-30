package io.github.kyuubiran.ezxhelper.core.utils

import io.github.kyuubiran.ezxhelper.core.extensions.MemberExtensions
import java.lang.reflect.Modifier

object MemberUtils {

    val MEMBER_MODIFIER_STRING_MAP = mapOf(
        Modifier.PRIVATE to "Private",
        Modifier.PROTECTED to "Protected",
        Modifier.PUBLIC to "Public",
        Modifier.STATIC to "Static",
        Modifier.FINAL to "Final",
        Modifier.SYNCHRONIZED to "Synchronized",
        Modifier.VOLATILE to "Volatile",
        Modifier.TRANSIENT to "Transient",
        Modifier.NATIVE to "Native",
        Modifier.INTERFACE to "Interface",
        Modifier.ABSTRACT to "Abstract",
        Modifier.STRICT to "Strict",
        MemberExtensions.VARARGS to "Varargs"
    )

    fun getMemberModifiersString(modifiers: Int, showBits: Boolean = false): String = buildString {
        if (modifiers == 0) {
            append("None")
            if (showBits) append(String.format("(0x%08X)", modifiers))
            return@buildString
        }

        var first = true

        MEMBER_MODIFIER_STRING_MAP.forEach { (k, v) ->
            if (modifiers and k != 0) {
                if (first) {
                    first = false
                } else {
                    append(" | ")
                }
                append(v)
                if (showBits) append(String.format("(0x%08X)", v))
            }
        }

        if (isEmpty()) {
            append("Unknown")
            if (showBits) append(String.format("(0x%08X)", modifiers))
        }

        trimEnd()
    }
}