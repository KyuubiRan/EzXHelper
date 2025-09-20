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

    fun getFrameworkName(): String = base.frameworkName

    fun getFrameworkVersion(): String = base.frameworkVersion

    fun getFrameworkVersionCode(): Long = base.frameworkVersionCode

    fun getFrameworkPrivilege(): Int = base.frameworkPrivilege

    fun hook(method: Method, priority: Int, hooker: Class<out XposedInterface.Hooker>): XposedInterface.MethodUnhooker<Method> {
        return base.hook(method, priority, hooker)
    }

    fun <T> hook(constructor: Constructor<T>, priority: Int, hooker: Class<out XposedInterface.Hooker>): XposedInterface.MethodUnhooker<Constructor<T>> {
        return base.hook(constructor, priority, hooker)
    }

    fun <T> hookClassInitializer(
        clazz: Class<T>,
        priority: Int,
        hooker: Class<out XposedInterface.Hooker>
    ): XposedInterface.MethodUnhooker<Constructor<T>> {
        return base.hookClassInitializer(clazz, priority, hooker)
    }

    fun deoptimize(member: Method): Boolean {
        return base.deoptimize(member)
    }

    fun <T> deoptimize(member: Constructor<T>): Boolean {
        return base.deoptimize(member)
    }

    fun invokeOrigin(method: Method, thisObject: Any?, vararg args: Any?): Any? {
        return base.invokeOrigin(method, thisObject, *args)
    }

    fun <T: Any> invokeOrigin(constructor: Constructor<T>, thisObject: T, vararg args: Any?) {
        base.invokeOrigin(constructor, thisObject, *args)
    }

    fun invokeSpecial(method: Method, thisObject: Any, vararg args: Any?): Any? {
        return base.invokeSpecial(method, thisObject, *args)
    }

    fun <T: Any> invokeSpecial(constructor: Constructor<T>, thisObject: T, vararg args: Any?) {
        base.invokeSpecial(constructor, thisObject, *args)
    }

    fun <T> newInstanceOrigin(constructor: Constructor<T>, vararg args: Any?): T {
        return base.newInstanceOrigin(constructor, *args)
    }

    fun <T, U> newInstanceSpecial(constructor: Constructor<T>, subClass: Class<U>, vararg args: Any?): U {
        return base.newInstanceSpecial(constructor, subClass, *args)
    }

    fun log(msg: String) {
        base.log(msg)
    }

    fun log(msg: String, thr: Throwable) {
        base.log(msg, thr)
    }

    fun parseDex(dexData: ByteBuffer, includeAnnotations: Boolean): DexParser? = base.parseDex(dexData, includeAnnotations)

    fun getRemotePreferences(group: String): SharedPreferences = base.getRemotePreferences(group)

    fun getApplicationInfo(): ApplicationInfo = base.applicationInfo

    fun listRemoteFiles(): Array<String> = base.listRemoteFiles()

    fun openRemoteFile(name: String): ParcelFileDescriptor = base.openRemoteFile(name)
}
