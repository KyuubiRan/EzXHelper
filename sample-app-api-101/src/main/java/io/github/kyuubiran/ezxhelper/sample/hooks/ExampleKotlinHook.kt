package io.github.kyuubiran.ezxhelper.sample.hooks

import android.app.Application
import android.content.Context
import android.util.Log
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.hook

object ExampleKotlinHook : BaseHook() {
    private fun logI(msg: String) {
        Log.i("ExampleKotlinHook", msg)
    }


    override fun init() {

        val mf = MethodFinder.fromClass(Application::class)

        val onCreate = mf.filterByName("onCreate")
            .filterEmptyParam()
            .first()

        onCreate.createHook {
            before {
                logI("Hello, Kotlin before hook!")
            }

            after {
                logI("Hello, Kotlin after hook!")
            }
        }

        val attachBaseContext = mf.filterByName("attachBaseContext")
            .filterByParamTypes(Context::class.java)
            .first()

        attachBaseContext.hook { chain ->
            val args = chain.args.toTypedArray()
            args[0] = chain.getArg(0) as Context
            logI("Hello, Kotlin chain hook!")
            chain.proceed(args)
        }

    }

    override val name: String = "ExampleKotlinHook"
}
