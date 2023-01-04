package com.example.sample.hook

import com.github.kyuubiran.ezxhelper.init.InitFields.ezXClassLoader
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getAllClassesList
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.loadClassOrNull

object FilterClassMethodExampleHook : BaseHook() {
    override val name: String = "FilterClassMethodExampleHook"

    private val allClasses by lazy { ezXClassLoader.getAllClassesList() }

    override fun init() {
        val clz = allClasses.asSequence()
            .filter { it.startsWith("android.app") }
            .filter { it.endsWith("Activity") }
            .mapNotNull { loadClassOrNull(it) }
            .filter { it.methods.any { m -> m.name == "onCreate" } }
            .firstOrNull() ?: return

        clz.findMethod { name == "onCreate" }.hookBefore {
            Log.i("Hooked ${it.thisObject.javaClass} before onCreate")
        }
    }
}