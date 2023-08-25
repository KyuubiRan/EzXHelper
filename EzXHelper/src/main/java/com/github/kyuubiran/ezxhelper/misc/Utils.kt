@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.misc

import android.annotation.SuppressLint
import android.app.Activity
import com.github.kyuubiran.ezxhelper.MemberExtensions
import dalvik.system.BaseDexClassLoader
import java.lang.reflect.Modifier
import java.util.*
import kotlin.system.exitProcess

object Utils {
    /**
     * From BiliRoaming
     * @see `https://github.com/yujincheng08/BiliRoaming`
     */
    @JvmStatic
    inline fun ClassLoader.findDexClassLoader(crossinline delegator: (BaseDexClassLoader) -> BaseDexClassLoader = { x -> x }): BaseDexClassLoader? {
        var classLoader = this
        while (classLoader !is BaseDexClassLoader) {
            if (classLoader.parent != null) classLoader = classLoader.parent
            else return null
        }
        return delegator(classLoader)
    }

    /**
     * From BiliRoaming
     * Get the all classes list in the class loader
     * @see `https://github.com/yujincheng08/BiliRoaming`
     */
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("DiscouragedPrivateApi")
    @JvmStatic
    fun ClassLoader.getAllClassesList(delegator: (BaseDexClassLoader) -> BaseDexClassLoader = { loader -> loader }): List<String> =
        findDexClassLoader(delegator)?.let {
            val f = BaseDexClassLoader::class.java.getDeclaredField("pathList").also { f -> f.isAccessible = true }
            f.get(it)
        }?.let {
            val f = it::class.java.getDeclaredField("dexElements").also { f -> f.isAccessible = true }
            f.get(it) as Array<Any>?
        }?.flatMap {
            val f = it::class.java.getDeclaredField("dexFile").also { f -> f.isAccessible = true }
            val df = f.get(it) ?: return@flatMap emptyList<String>()
            val m = df::class.java.getDeclaredMethod("entries").also { m -> m.isAccessible = true }
            (m.invoke(df) as Enumeration<String>?)?.toList().orEmpty()
        }.orEmpty()

    /**
     * Restart the host application.
     * @param activity host activity.
     */
    @JvmStatic
    fun restartHostApp(activity: Activity) {
        val pm = activity.packageManager
        val intent = pm.getLaunchIntentForPackage(activity.packageName)
        activity.finishAffinity()
        activity.startActivity(intent)
        exitProcess(0)
    }

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
