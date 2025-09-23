package io.github.kyuubiran.ezxhelper.sample.hooks

import android.app.Application
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder
import io.github.kyuubiran.ezxhelper.xposed.api.XposedApi.log
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook

object ExampleKotlinHook : BaseHook() {

    override fun init() {

        val mf = MethodFinder.fromClass(Application::class)

        val onCreate = mf.filterByName("onCreate")
            .filterEmptyParam()
            .first()

        onCreate.createHook {
            before {
                log("Hello, Kotlin before hook!")
            }

            after {
                log("Hello, Kotlin after hook!")
            }
        }

    }

    override val name: String = "ExampleKotlinHook"
}
