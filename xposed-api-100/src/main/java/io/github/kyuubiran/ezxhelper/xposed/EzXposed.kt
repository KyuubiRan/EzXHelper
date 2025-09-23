package io.github.kyuubiran.ezxhelper.xposed

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.kyuubiran.ezxhelper.xposed.common.ModuleResources
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object EzXposed {
    internal lateinit var base: XposedInterface
        private set

    private var _appContext: Context? = null

    /**
     * Get application context.
     * Note: Accessing this for the first time will also initialize module resources.
     *
     * 获取 application context。
     * 注意：首次访问此属性也会初始化模块资源。
     *
     * @throws NullPointerException if you get the appContext too early.
     */
    @JvmStatic
    val appContext: Context
        @SuppressLint("PrivateApi")
        @Synchronized get() {
            if (_appContext == null) {
                _appContext = getCurrentApplicationContext()
                if (_appContext == null) {
                    throw NullPointerException("Cannot get application context, did application call Application.onCreate?")
                }
            }

            return _appContext!!
        }

    @JvmStatic
    lateinit var hookedPackageName: String
        private set

    @JvmStatic
    lateinit var modulePath: String
        private set

    @JvmStatic
    lateinit var moduleRes: Resources
        private set

    /**
     * Instantiates a new Xposed module in your [XposedModule] constructor.
     *
     * 在你的 [XposedModule] 构造函数中初始化。
     *
     * @see XposedModule
     */
    @JvmStatic
    fun initXposedModule(base: XposedInterface) {
        this.base = base
    }

    /**
     * You need to invoke this function at first in [XposedModule.onPackageLoaded].
     *
     * 你需要在 [XposedModule.onPackageLoaded] 中首先调用此函数。
     *
     * @see XposedModule.onPackageLoaded
     * @see XposedModuleInterface.PackageLoadedParam
     */
    @JvmStatic
    fun initOnPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        EzXReflection.init(param.classLoader)
        hookedPackageName = param.packageName
    }

    /**
     * You need to invoke this function at first in [XposedModule.onSystemServerLoaded].
     *
     * 你需要在 [XposedModule.onSystemServerLoaded] 中首先调用此函数。
     *
     * @see XposedModule.onSystemServerLoaded
     * @see XposedModuleInterface.SystemServerLoadedParam
     */
    @JvmStatic
    fun initOnSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        EzXReflection.init(param.classLoader)
    }

    /**
     * Resolve the module APK path and prepare module-scoped Resources for immediate R access.
     * Call after initXposedModule so the base interface is already captured.
     * Optionally pass the original target Resources so locale, density, and theme mirror the hooked app.
     *
     * 解析模块 APK 路径并准备模块级 Resources，方便直接访问模块 R 资源。
     * 可选地传入目标应用的 Resources，以沿用其语言、分辨率等配置；需在 initXposedModule 之后调用。
     */
    @JvmStatic
    fun initModuleResources(origRes: Resources? = null) {
        this.modulePath = base.applicationInfo.sourceDir
        this.moduleRes = ModuleResources.create(modulePath, origRes)
    }

    /**
     * Initialize the application context.
     *
     * Recommended invoke this after [Application.onCreate].
     *
     * 初始化应用程序上下文。
     *
     * 推荐在 [Application.onCreate] 之后调用此方法。
     *
     * @param context context
     * @param injectResources add module resources path to target [Context.resources]
     *                       | 是否将模块资源路径添加到目标 Context.resources
     * @throws NullPointerException if context is null | 若 context 为空则抛出异常
     */
    @JvmStatic
    fun initAppContext(
            context: Context? = getCurrentApplicationContext(),
            injectResources: Boolean = false,
        ) {
        if (context == null) {
            throw NullPointerException("Cannot initialize application context, context is null.")
        }
        _appContext = context
        if (injectResources) addModuleAssetPath(_appContext!!)
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun getCurrentApplicationContext(): Context? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getDeclaredMethod("currentApplication")
            currentApplicationMethod.invoke(null) as? Context
        } catch (e: Exception) {
            throw IllegalStateException("Failed to get application context", e)
        }
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
     * 2.Make sure EzXposed is initialized.
     *
     * 3.Invoked this function before use
     *
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
     * 2. 确保 EzXposed 已经初始化。
     *
     * 3. 在使用之前调用此函数
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
