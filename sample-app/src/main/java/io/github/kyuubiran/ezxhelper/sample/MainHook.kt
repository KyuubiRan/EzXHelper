package io.github.kyuubiran.ezxhelper.sample

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.kyuubiran.ezxhelper.sample.hooks.BaseHook
import io.github.kyuubiran.ezxhelper.sample.hooks.ExampleJavaHook
import io.github.kyuubiran.ezxhelper.sample.hooks.ExampleKotlinHook
import io.github.kyuubiran.ezxhelper.xposed.EzXposed

const val TargetApp = "com.sample.app"

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != TargetApp)
            return

        EzXposed.initHandleLoadPackage(lpparam)
        initHooks(ExampleKotlinHook, ExampleJavaHook.INSTANCE)
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXposed.initZygote(startupParam)
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