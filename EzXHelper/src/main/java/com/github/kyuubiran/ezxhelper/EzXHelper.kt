@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.kyuubiran.ezxhelper

import android.app.AndroidAppHelper
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XModuleResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

object EzXHelper {
    /**
     * Hooked application context.
     */
    @JvmStatic
    lateinit var appContext: Context

    @JvmStatic
    val isAppContextInited: Boolean
        get() = this::appContext.isInitialized

    /**
     * Class loader for doing reflection.
     * Will auto initialized when [initHandleLoadPackage] invoked.
     */
    @JvmStatic
    lateinit var classLoader: ClassLoader

    /**
     * Safe class loader for doing reflection, will use system class loader instead if [classLoader] is not initialized.
     */
    @JvmStatic
    val safeClassLoader: ClassLoader
        get() = if (isClassLoaderInited) classLoader else ClassLoader.getSystemClassLoader()

    @JvmStatic
    val isClassLoaderInited: Boolean
        get() = this::classLoader.isInitialized

    /**
     * Module path.
     * Will auto initialized when [initZygote] invoked.
     */
    @JvmStatic
    lateinit var modulePath: String

    @JvmStatic
    val isModulePathInited: Boolean
        get() = this::modulePath.isInitialized

    /**
     * Module resources.
     * Will auto initialized when [initZygote] invoked.
     */
    @JvmStatic
    lateinit var moduleRes: Resources

    @JvmStatic
    val isModuleResInited: Boolean
        get() = this::moduleRes.isInitialized

    /**
     * Package name of hooked application.
     * Will auto initialized when [initHandleLoadPackage] invoked.
     */
    @JvmStatic
    lateinit var hostPackageName: String

    @JvmStatic
    val isHostPackageNameInited: Boolean
        get() = this::hostPackageName.isInitialized

    /**
     * You need invoke this function at first in [IXposedHookLoadPackage.handleLoadPackage].
     * @see IXposedHookLoadPackage.handleLoadPackage
     * @see XC_LoadPackage.LoadPackageParam
     */
    @JvmStatic
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        classLoader = lpparam.classLoader
        hostPackageName = lpparam.packageName
    }

    /**
     * You need invoke this function at first in [IXposedHookZygoteInit.initZygote].
     * If you want to use module resources.
     * @see IXposedHookZygoteInit.initZygote
     */
    @JvmStatic
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = XModuleResources.createInstance(modulePath, null)
    }

    /**
     * Initialize the application context.
     * Recommended invoke this after [Application.onCreate].
     * @param context context
     * @param addPath add module resources path to context)
     */
    @Suppress("KDocUnresolvedReference")
    @JvmStatic
    fun initAppContext(
        context: Context = AndroidAppHelper.currentApplication(),
        addPath: Boolean = false,
    ) {
        appContext = context
        if (addPath) addModuleAssetPath(appContext)
    }

    /**
     * Set current logger tag.
     */
    @JvmStatic
    fun setLogTag(tag: String) {
        Log.currentLogger.logTag = tag
    }

    /**
     * Set current logger toast tag.
     * If not set will not show the prefix.
     * @see Log.toast
     */
    @JvmStatic
    fun setToastTag(tag: String) {
        Log.currentLogger.toastTag = tag
    }

    /**
     * Add module path to target Context.resources. Allow directly use module resources with R.xx.xxx.
     *
     * If you want to use this, please do:
     *
     * 1.Modify resources id(don't same as hooked application or other xposed module) in the build.gradle(.kts):
     *
     * Kotlin Gradle DSL:
     *
     *     androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
     *
     * Groovy:
     *
     *     aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
     *
     * `0x64` is the resource id, you can change it to any value you want.(recommended [0x30 to 0x6F])
     *
     * 2.Make sure you invoked [initZygote]
     *
     * 3.Invoked this function before use,
     *
     * e.g. In the [android.app.Activity]
     *
     *     init {
     *         addModuleAssetPath(this)
     *         Log.toast(getString(R.string.hello_world))
     *     }
     *
     * @see initZygote
     *
     */
    @JvmStatic
    fun addModuleAssetPath(context: Context) {
        addModuleAssetPath(context.resources)
    }

    private val mAddAddAssertPath by lazy {
        @Suppress("DiscouragedPrivateApi")
        AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).also { it.isAccessible = true }
    }

    /**
     * @see [addModuleAssetPath]
     */
    @JvmStatic
    fun addModuleAssetPath(resources: Resources) {
        mAddAddAssertPath.invoke(resources.assets, modulePath)
    }
}
