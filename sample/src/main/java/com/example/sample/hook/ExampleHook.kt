package com.example.sample.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.github.kyuubiran.ezxhelper.utils.HookFactory.Companion.createHook
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.finders.ConstructorFinder
import com.github.kyuubiran.ezxhelper.utils.finders.FieldFinder
import com.github.kyuubiran.ezxhelper.utils.finders.MethodFinder
import java.lang.reflect.Modifier

// Example hook
object ExampleHook : BaseHook() {
    override val name: String = "ExampleHook"

    override fun init() {
        val unhook = MethodFinder.fromClass(Activity::class.java)
            .findSuper { name.endsWith("ContextWrapper") }
            .filterNonStatic()
            .filterByParamCount(2)
            .filter { name.length == 6 }
            .filterIncludeModifiers(Modifier.PUBLIC)
            .filterExcludeModifiers(Modifier.SYNCHRONIZED or Modifier.NATIVE)
            .filterByReturnType(Void.TYPE)
            .filterByName("onCreate")
            .first()
            .createHook {
                before {
                    Log.i("Hooked onCreate before")
                }

                after {
                    Log.i("Hooked onCreate after")
                }
            }
        unhook.unhook()

        val unhook2 = ConstructorFinder.fromArray(AlertDialog::class.java.declaredConstructors).run {
            filterByParamCount { count -> count < 2 }
            filterByParamTypes(Context::class.java)
            filterByExceptionTypes()
            firstOrNull()
        }?.createHook {
            before {
                Log.i("Hooked AlertDialog constructor before")
            }

            after {
                Log.i("Hooked AlertDialog constructor after")
            }
        }
        unhook2?.unhook()

        val obj = FieldFinder.fromClass(Activity::class.java)
            .filterStatic()
            .filterIncludeModifiers(Modifier.FINAL or Modifier.PUBLIC)
            .filterNonPrivate()
            .filterByName("RESULT_CANCELED")
            .filterByType(Int::class.java)
            .firstOrNull()?.get(null)
        Log.i("RESULT_CANCELED = $obj")
    }
}