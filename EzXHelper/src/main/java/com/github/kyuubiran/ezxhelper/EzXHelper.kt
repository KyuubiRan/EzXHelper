@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper

import android.app.AndroidAppHelper
import android.content.Context
import android.content.res.Resources
import android.content.res.XModuleResources
import com.github.kyuubiran.ezxhelper.utils.*
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

object EzXHelper {
    /**
     * 宿主全局AppContext
     */
    lateinit var appContext: Context

    /**
     * 宿主全局AppContext是否初始化
     */
    val isAppContextInited: Boolean
        get() = this::appContext.isInitialized

    /**
     * 调用本库加载类函数时使用的类加载器
     */
    lateinit var classLoader: ClassLoader

    val safeClassLoader: ClassLoader
        get() = if (isClassLoaderInited) classLoader else ClassLoader.getSystemClassLoader()

    /**
     * 类加载器是否初始化
     */
    val isClassLoaderInited: Boolean
        get() = this::classLoader.isInitialized

    /**
     * 模块路径
     */
    lateinit var modulePath: String

    /**
     * 模块路径是否初始化
     */
    val isModulePathInited: Boolean
        get() = this::modulePath.isInitialized

    /**
     * 模块资源
     */
    lateinit var moduleRes: Resources

    /**
     * 模块资源是否初始化
     */
    val isModuleResInited: Boolean
        get() = this::moduleRes.isInitialized

    /**
     * 宿主包名
     */
    lateinit var hostPackageName: String

    /**
     * 宿主包名初始化
     */
    val isHostPackageNameInited: Boolean
        get() = this::hostPackageName.isInitialized

    /**
     * 使用本库必须执行的初始化
     * 应在handleLoadPackage方法内第一个调用
     * @see IXposedHookLoadPackage.handleLoadPackage
     * @see XC_LoadPackage.LoadPackageParam
     */
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        classLoader = lpparam.classLoader
        hostPackageName = lpparam.packageName
    }

    /**
     * 初始化Zygote 以便使用模块路径 和 模块资源
     * @see IXposedHookZygoteInit.initZygote
     */
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = XModuleResources.createInstance(modulePath, null)
    }

    /**
     * 初始化全局ApplicationContext
     * @param context ctx
     * @param addPath 是否往ctx中添加模块资源路径
     * @param initModuleResources 是否初始化moduleRes
     */
    fun initAppContext(
        context: Context = AndroidAppHelper.currentApplication(),
        addPath: Boolean = false,
        initModuleResources: Boolean = false
    ) {
        appContext = context
        if (addPath) addModuleAssetPath(appContext)
        if (initModuleResources) moduleRes = context.resources
    }

    /**
     * 设置打印日志的标签
     */
    fun setLogTag(tag: String) {
        Log.currentLogger.logTag = tag
    }

    /**
     * 设置Log.toast的Tag
     * 如果不设置会使用日志TAG
     * @see Log.toast
     */
    fun setToastTag(tag: String) {
        Log.currentLogger.toastTag = tag
    }

    /**
     * 将模块的资源路径添加到Context.resources内 允许直接以R.xx.xxx获取资源
     *
     * 要求:
     *
     * 1.在项目的build.gradle中修改资源id(不与宿主冲突即可) 如下:
     *
     * Kotlin Gradle DSL:
     * androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
     *
     * Groovy:
     * aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
     *
     * 2.执行过EzXHelperInit.initZygote
     *
     * 3.在使用资源前调用
     *
     * eg:在Activity中:
     * init { addModuleAssetPath(this) }
     *
     * @see initZygote
     *
     */
    fun addModuleAssetPath(context: Context) {
        addModuleAssetPath(context.resources)
    }

    fun addModuleAssetPath(resources: Resources) {
        val m = resources.javaClass.getDeclaredMethod("addAssetPath", String::class.java).also { it.isAccessible = true }
        m.invoke(resources, modulePath)
    }
}
