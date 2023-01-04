package com.example.sample.hook

import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.findMethods
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.github.kyuubiran.ezxhelper.utils.loadClassAny
import com.github.kyuubiran.ezxhelper.utils.mcp

object MultiExampleHook : BaseHook() {
    override val name: String = "MultiExampleHook"

    override fun init() {
        val clz = loadClass("android.widget.Toast")

        // for different method but same class
        val finders = listOf(
            clz mcp { name == "show" },
            clz mcp { name == "cancel" },
            clz mcp { name == "getDuration" }
        )

        finders.findMethods().hookBefore {
            Log.i("Hooked before Toast.show() or Toast.cancel() or Toast.getDuration()")
        }

        // for different class but same method
        val clz2 = loadClassAny(
            "androidx.appcompat.app.AppCompatActivity",
            "androidx.fragment.app.FragmentActivity",
            "androidx.activity.ComponentActivity",
            "android.app.Activity",
        )

        clz2.findMethod { name == "onCreate" }.hookBefore {
            Log.i("Hooked ${it.thisObject.javaClass} before onCreate")
        }
    }
}