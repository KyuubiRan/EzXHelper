package io.github.kyuubiran.ezxhelper.sample;

import org.junit.Test;

import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder;

public class ExampleJavaTest {

    @Test
    public void findToString() {
        var mf = MethodFinder.fromClass(ExampleJavaTest.class);

        System.out.println("=======================");
        var m = mf.findSuper(null)
                .filterByName("toString")
                .filterEmptyParam()
                .first();

        System.out.println("Method toString: " + m);
        System.out.println("=======================");

        var m2 = mf.filterByName("findToString")
                .filterEmptyParam()
                .first();

        System.out.println("Method findToString: " + m2);
        System.out.println("=======================");

    }
}
