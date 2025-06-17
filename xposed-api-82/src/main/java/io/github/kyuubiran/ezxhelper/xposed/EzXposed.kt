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

    private var _appContext: Context? = null

    /**
     * Get application context
     *
     * Notice: May cause NullPointerException if the [AndroidAppHelper.currentApplication] return null
     *
     * because of you get the appContext too early
     *
     * 获取 application context
     *
     * 请注意，如果 [AndroidAppHelper.currentApplication] 返回 null，可能会导致 NullPointerException
     *
     * 因为你在应用程序的 onCreate 方法之前获取了 appContext
     *
     */
    @JvmStatic
    val appContext: Context
        @Synchronized get() {
            if (_appContext == null) {
                _appContext = AndroidAppHelper.currentApplication()
                if (_appContext == null) {
                    throw NullPointerException("Cannot get application context, did application call Application.onCreate?")
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
     * You need invoke this function at first in [IXposedHookLoadPackage.handleLoadPackage]
     *
     * 你需要在 [IXposedHookLoadPackage.handleLoadPackage] 中首先调用此函数
     *
     * @see IXposedHookLoadPackage.handleLoadPackage
     * @see XC_LoadPackage.LoadPackageParam
     */
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXReflection.init(lpparam.classLoader)
        hookedPackageName = lpparam.packageName
    }

    /**
     * You need invoke this function at first in [IXposedHookZygoteInit.initZygote]
     *
     * If you want to use module resources
     *
     * 如果你想使用模块资源，你需要在 [IXposedHookZygoteInit.initZygote] 中首先调用此函数
     *
     * @see IXposedHookZygoteInit.initZygote
     */
    @JvmStatic
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = XModuleResources.createInstance(modulePath, null)
    }

    /**
     * Initialize the application context
     *
     * Recommended invoke this after [Application.onCreate]
     *
     * 在 [Application.onCreate] 之后调用此方法来初始化应用程序 [Context]
     *
     * 推荐在 [Application.onCreate] 之后调用此方法来初始化应用程序 [Context]
     *
     * @param context context
     * @param injectResources add module resources path to context | 是否将模块资源路径添加到目标 Context.resources
     * @throws NullPointerException if context is null. |  如果 context 为 null，则抛出 [NullPointerException]
     */
    @Suppress("KDocUnresolvedReference")
    @JvmStatic
    fun initAppContext(
        context: Context? = AndroidAppHelper.currentApplication(),
        injectResources: Boolean = false,
    ) {
        if (context == null) {
            throw NullPointerException("Cannot initialize application context, context is null.")
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
     * 3.Invoked this function before use
     *
     * 添加模块资源路径到目标 Context.resources。允许直接使用模块资源，如 R.xx.xxx。
     *
     * 如果你想使用这个功能，请按照以下步骤操作：
     *
     * 1. 在 build.gradle(.kts) 中修改资源 ID（不要与被 Hook 的应用程序或其他 Xposed 模块相同）：
     *
     *   Kotlin Gradle DSL:
     *
     *     androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
     *
     *   Groovy:
     *
     *      aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
     *
     * `0x64` 是资源 ID，你可以将其更改为任何你想要的值（推荐使用 [0x30 到 0x6F] 范围内的值）。
     *
     * 2. 确保你已经调用了 [initZygote]。
     *
     * 3. 在使用之前调用此函数
     *
     * e.g. [android.app.Activity]
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