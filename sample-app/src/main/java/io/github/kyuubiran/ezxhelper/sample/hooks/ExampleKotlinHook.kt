package io.github.kyuubiran.ezxhelper.sample.hooks

import android.app.Application
import android.util.Log
import io.github.kyuubiran.ezxhelper.core.finders.MethodFinder
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook

object ExampleKotlinHook : BaseHook() {

    override fun init() {

        val mf = MethodFinder.fromClass(Application::class)

        val onCreate = mf.filterByName("onCreate")
            .filterEmptyParam()
            .first()

        onCreate.createHook {
            before {
                Log.i(name, "Hello, Kotlin before hook!")
            }

            after {
                Log.i(name, "Hello, Kotlin after hook!")
            }
        }

    }

    override val name: String = "ExampleKotlinHook"
}