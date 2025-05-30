package io.github.kyuubiran.ezxhelper.xposed

import android.app.AndroidAppHelper
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XModuleResources
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.kyuubiran.ezxhelper.core.EzXReflection

object EzXposed {

    /**
     * Built-in log tag
     */
    var builtinLogTag: String = "EzXHelper"

    private var _appContext: Context? = null

    /**
     * Get application context.
     * Notice: May cause NullPointerException if the [AndroidAppHelper.currentApplication] return null
     * because of you get the appContext too early.
     */
    @JvmStatic
    val appContext: Context
        @Synchronized get() {
            if (_appContext == null) {
                _appContext = AndroidAppHelper.currentApplication()
                if (_appContext == null) {
                    Log.e(builtinLogTag, "Cannot get application context, did application call Application.onCreate?")
                }
            }

            return _appContext!!
        }

    lateinit var hookedPackageName: String
        private set

    lateinit var modulePath: String
        private set

    lateinit var moduleRes: XModuleResources
        private set


    /**
     * You need invoke this function at first in [IXposedHookLoadPackage.handleLoadPackage].
     * @see IXposedHookLoadPackage.handleLoadPackage
     * @see XC_LoadPackage.LoadPackageParam
     */
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXReflection.init(lpparam.classLoader)
        hookedPackageName = lpparam.packageName
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
     * @param injectResources add module resources path to context)
     */
    @Suppress("KDocUnresolvedReference")
    @JvmStatic
    fun initAppContext(
        context: Context? = AndroidAppHelper.currentApplication(),
        injectResources: Boolean = false,
    ) {
        if (context == null) {
            Log.w(builtinLogTag, "Cannot initialize application context, context is null.")
            return
        }
        _appContext = context
        if (injectResources) addModuleAssetPath(_appContext!!)
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
        @Suppress("DiscouragedPrivateApi", "PrivateApi")
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