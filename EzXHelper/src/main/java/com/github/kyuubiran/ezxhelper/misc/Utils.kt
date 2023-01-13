@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.misc

import android.annotation.SuppressLint
import android.app.Activity
import dalvik.system.BaseDexClassLoader
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
            val f = it.javaClass.getDeclaredField("pathList").also { f -> f.isAccessible = true }
            f.get(it)
        }?.let {
            val f = it.javaClass.getDeclaredField("dexElements").also { f -> f.isAccessible = true }
            f.get(it) as Array<Any>?
        }?.flatMap {
            val f = it.javaClass.getDeclaredField("dexFile").also { f -> f.isAccessible = true }
            val df = f.get(it) ?: return@flatMap emptyList<String>()
            val m = df.javaClass.getDeclaredMethod("entries").also { m -> m.isAccessible = true }
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
}
