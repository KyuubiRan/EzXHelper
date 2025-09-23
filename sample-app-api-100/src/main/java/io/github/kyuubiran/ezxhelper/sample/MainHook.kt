package io.github.kyuubiran.ezxhelper.sample

import android.util.Log
import io.github.kyuubiran.ezxhelper.sample.hooks.BaseHook
import io.github.kyuubiran.ezxhelper.sample.hooks.ExampleJavaHook
import io.github.kyuubiran.ezxhelper.sample.hooks.ExampleKotlinHook
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

const val TargetApp = "com.example.myapplication"

class MainHook(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    init {
        EzXposed.initXposedModule(base)
        log("init")
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        if (param.packageName != TargetApp)
            return

        EzXposed.initOnPackageLoaded(param)
        log("MainHook at " + param.packageName)

        initHooks(ExampleKotlinHook, ExampleJavaHook.INSTANCE)
    }

    fun initHooks(vararg hooks: BaseHook) {
        for (h in hooks) {
            try {
                if (h.isInit) continue
                h.init()
                h.isInit = true
            } catch (e: Exception) {
                Log.e("HookEntry", "Failed to initialize hook: ${h.name}", e)
            }
        }
    }
}
