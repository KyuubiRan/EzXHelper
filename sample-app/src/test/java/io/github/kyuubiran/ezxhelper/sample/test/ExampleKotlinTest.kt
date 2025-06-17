package io.github.kyuubiran.ezxhelper.sample.test

import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleKotlinTest {

    @Test
    fun findToString() {
        val mf = MethodFinder.fromClass(ExampleKotlinTest::class)

        println("=======================")
        val m = mf
            .findSuper()
            .filterByName("toString")
            .filterEmptyParam()
            .first()

        println("Method toString = $m")
        println("=======================")

        val m2 = mf
            .filterByName("findToString")
            .filterEmptyParam()
            .first()

        println("Method findToString = $m2")
        println("=======================")
    }
}