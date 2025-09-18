package io.github.kyuubiran.ezxhelper.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.lang.reflect.Constructor
import java.lang.reflect.Method

object EzXposed {
    private lateinit var base: XposedInterface

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
     * Instantiates a new Xposed module in your [XposedModule] constructor.
     *
     * 在你的 [XposedModule] 构造函数中初始化。
     *
     * @see XposedModule
     */
    @JvmStatic
    fun initXposedModule(base: XposedInterface) {
        this.base = base
        this.modulePath = base.applicationInfo.sourceDir
    }

    /**
     * Initialize the application context.
     * Recommended to be called in `Application.onCreate`.
     * Note: This will also initialize module resources if they haven't been loaded yet.
     *
     * 初始化应用程序上下文。
     * 建议在 `Application.onCreate` 中调用。
     * 注意：如果模块资源尚未加载，此操作也会完成其初始化。
     *
     * @param context context
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
        if (!::moduleRes.isInitialized) {
            moduleRes = context.packageManager.getResourcesForApplication(base.applicationInfo)
        }
    }

    @SuppressLint("PrivateApi")
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

    @JvmStatic
    fun hook(method: Method, priority: Int, hooker: Class<out XposedInterface.Hooker>): XposedInterface.MethodUnhooker<Method> {
        return base.hook(method, priority, hooker)
    }

    @JvmStatic
    fun <T> hook(constructor: Constructor<T>, priority: Int, hooker: Class<out XposedInterface.Hooker>): XposedInterface.MethodUnhooker<Constructor<T>> {
        return base.hook(constructor, priority, hooker)
    }
}
