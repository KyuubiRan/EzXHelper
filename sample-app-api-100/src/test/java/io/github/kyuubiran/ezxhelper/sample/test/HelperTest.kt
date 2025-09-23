package io.github.kyuubiran.ezxhelper.sample.test

import io.github.kyuubiran.ezxhelper.core.helper.ClassHelper.`-Static`.classHelper
import io.github.kyuubiran.ezxhelper.core.helper.ObjectHelper.`-Static`.objectHelper
import io.github.kyuubiran.ezxhelper.sample.data.ExampleChildJavaClass
import io.github.kyuubiran.ezxhelper.sample.data.ExampleJavaClass
import org.junit.Test

class HelperTest {

    @Test
    fun testHelper() {
        val clz = ExampleJavaClass::class.java
        val clzHelper = clz.classHelper()

        val staticField = clzHelper.getStaticObject("staticField")
        assert(42 == staticField)
        println("Static field value: $staticField")

        val m1 = clzHelper.invokeStaticMethodBestMatch("staticMethod", String::class.java, 1, "Hello")
        assert("Static method called with i: 1, charSequence: Hello" == m1)
        println(m1)

        val inst1 = clzHelper.newInstanceBestMatch() as ExampleJavaClass
        assert(inst1.type == "empty")
        val inst2 = clzHelper.newInstanceBestMatch(1) as ExampleJavaClass
        assert(inst2.type == "int")
        val inst3 = clzHelper.newInstanceBestMatch(1L) as ExampleJavaClass
        assert(inst3.type == "long")

        println("new instance check pass")

        println("===================")

        val objHelper = inst1.objectHelper()
        val ty = objHelper.getObject("type")
        assert(ty == "empty")

        objHelper.setObject("type", "changed")
        assert(inst1.type == "changed")
        println(inst1.type)

        val ret = objHelper.invokeMethodBestMatch("method1", null, 1, 1)
        assert(ret == 1)
        println("Method1 return value: $ret")

        val nullObj = objHelper.getObjectOrNull("nonExistentField")
        assert(nullObj == null)
        println("Non-existent field value: $nullObj")

        val failedObj = runCatching { objHelper.getObject("nonExistentField") }
        assert(failedObj.isFailure)
        println("Attempt to get non-existent field failed: ${failedObj.exceptionOrNull()?.message}")
    }

    @Test
    fun childTestHelper() {
        val child = ExampleChildJavaClass()
        val helper = child.objectHelper()

        val superField = helper.getObjectUntilSuperclass("type") { simpleName.contains("ExampleJavaClass") }
        assert(superField == "child_empty")
        println("Found super field: $superField")

        val superFieldFailed = runCatching { helper.getObjectUntilSuperclass("type") { true } }
        assert(superFieldFailed.isFailure)
        println("Attempt to get super field with false condition failed: ${superFieldFailed.exceptionOrNull()?.message}")
    }
}