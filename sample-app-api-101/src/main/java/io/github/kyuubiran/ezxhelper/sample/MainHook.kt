package io.github.kyuubiran.ezxhelper.sample

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.github.kyuubiran.ezxhelper.sample.hooks.BaseHook
import io.github.kyuubiran.ezxhelper.sample.hooks.ExampleJavaHook
import io.github.kyuubiran.ezxhelper.sample.hooks.ExampleKotlinHook
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

const val TargetApp = "com.example.myapplication"

class MainHook : XposedModule() {
    private fun logI(msg: String) {
        Log.i("MainHook", msg)
    }

    override fun onModuleLoaded(param: ModuleLoadedParam) {
        EzXposed.initOnModuleLoaded(this, param)
        logI("init")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPackageLoaded(param: PackageLoadedParam) {
        if (param.packageName != TargetApp)
            return

        EzXposed.initOnPackageLoaded(param)
        logI("MainHook package loaded at " + param.packageName)
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != TargetApp)
            return

        EzXposed.initOnPackageReady(param)
        logI("MainHook package ready at " + param.packageName)

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
