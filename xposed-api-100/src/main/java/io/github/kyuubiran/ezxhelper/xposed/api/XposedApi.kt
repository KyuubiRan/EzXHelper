@file:Suppress("unused")
package io.github.kyuubiran.ezxhelper.xposed.api

import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.ParcelFileDescriptor
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.utils.DexParser
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.nio.ByteBuffer

object XposedApi {
    private val base get() = EzXposed.base

    @JvmStatic
    fun getFrameworkName(): String = base.frameworkName

    @JvmStatic
    fun getFrameworkVersion(): String = base.frameworkVersion

    @JvmStatic
    fun getFrameworkVersionCode(): Long = base.frameworkVersionCode

    @JvmStatic
    fun getFrameworkPrivilege(): Int = base.frameworkPrivilege

    @JvmStatic
    fun hook(method: Method, priority: Int, hooker: Class<out XposedInterface.Hooker>): XposedInterface.MethodUnhooker<Method> {
        return base.hook(method, priority, hooker)
    }

    @JvmStatic
    fun <T> hook(constructor: Constructor<T>, priority: Int, hooker: Class<out XposedInterface.Hooker>): XposedInterface.MethodUnhooker<Constructor<T>> {
        return base.hook(constructor, priority, hooker)
    }

    @JvmStatic
    fun <T> hookClassInitializer(
        clazz: Class<T>,
        priority: Int,
        hooker: Class<out XposedInterface.Hooker>
    ): XposedInterface.MethodUnhooker<Constructor<T>> {
        return base.hookClassInitializer(clazz, priority, hooker)
    }

    @JvmStatic
    fun deoptimize(member: Method): Boolean {
        return base.deoptimize(member)
    }

    @JvmStatic
    fun <T> deoptimize(member: Constructor<T>): Boolean {
        return base.deoptimize(member)
    }

    @JvmStatic
    fun invokeOrigin(method: Method, thisObject: Any?, vararg args: Any?): Any? {
        return base.invokeOrigin(method, thisObject, *args)
    }

    @JvmStatic
    fun <T: Any> invokeOrigin(constructor: Constructor<T>, thisObject: T, vararg args: Any?) {
        base.invokeOrigin(constructor, thisObject, *args)
    }

    @JvmStatic
    fun invokeSpecial(method: Method, thisObject: Any, vararg args: Any?): Any? {
        return base.invokeSpecial(method, thisObject, *args)
    }

    @JvmStatic
    fun <T: Any> invokeSpecial(constructor: Constructor<T>, thisObject: T, vararg args: Any?) {
        base.invokeSpecial(constructor, thisObject, *args)
    }

    @JvmStatic
    fun <T> newInstanceOrigin(constructor: Constructor<T>, vararg args: Any?): T {
        return base.newInstanceOrigin(constructor, *args)
    }

    @JvmStatic
    fun <T, U> newInstanceSpecial(constructor: Constructor<T>, subClass: Class<U>, vararg args: Any?): U {
        return base.newInstanceSpecial(constructor, subClass, *args)
    }

    @JvmStatic
    fun log(msg: String) {
        base.log(msg)
    }

    @JvmStatic
    fun log(msg: String, thr: Throwable) {
        base.log(msg, thr)
    }

    @JvmStatic
    fun parseDex(dexData: ByteBuffer, includeAnnotations: Boolean): DexParser? = base.parseDex(dexData, includeAnnotations)

    @JvmStatic
    fun getRemotePreferences(group: String): SharedPreferences = base.getRemotePreferences(group)

    @JvmStatic
    fun getApplicationInfo(): ApplicationInfo = base.applicationInfo

    @JvmStatic
    fun listRemoteFiles(): Array<String> = base.listRemoteFiles()

    @JvmStatic
    fun openRemoteFile(name: String): ParcelFileDescriptor = base.openRemoteFile(name)
}
