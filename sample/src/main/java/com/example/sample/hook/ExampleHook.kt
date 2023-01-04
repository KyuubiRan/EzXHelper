package com.example.sample.hook

import com.github.kyuubiran.ezxhelper.utils.*

// Example hook
object ExampleHook : BaseHook() {
    override val name: String = "ExampleHook"

    override fun init() {
        // findMethod example
        findMethod("android.widget.Toast") {
            name == "show"
        }.hookBefore {
            Log.i("Hooked before Toast.show()")

            // findField example
            val fText = it.thisObject.javaClass.findField { name == "mText" }
            val text = fText.get(it.thisObject)
            Log.i("Toast text: $text")

            it.thisObject.putObject(fText, "Hello EZXHelper")

            // findFieldObject example
            it.thisObject.findFieldObjectOrNull { name == "mText" }.let { t ->
                Log.i("Toast text: $t")
            }
        }

        // getMethodByDesc example
        getMethodByDesc("Landroid/widget/Toast;->show()V").hookAfter {
            Log.i("Hooked after Toast.show()")
        }

        // getFieldByDesc example
        val fTAG = getFieldByDesc("Landroid/widget/Toast;->TAG:Ljava/lang/String;")
        val tag = fTAG.get(null)
        Log.i("Toast TAG: $tag")
    }
}