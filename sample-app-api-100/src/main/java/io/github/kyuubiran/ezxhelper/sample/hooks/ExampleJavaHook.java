package io.github.kyuubiran.ezxhelper.sample.hooks;

import static io.github.kyuubiran.ezxhelper.xposed.api.XposedApi.log;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder;
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory;

public class ExampleJavaHook extends BaseHook {
    private ExampleJavaHook() {
        super();
    }

    public static final ExampleJavaHook INSTANCE = new ExampleJavaHook();

    @Override
    public void init() {
        var mf = MethodFinder.fromClass(Application.class);

        var onCreate = mf.filterByName("onCreate")
                .filterEmptyParam()
                .first();

        HookFactory.createMethodHook(onCreate, hookFactory -> {
            hookFactory.before(param -> {
                log("Hello, Java before hook!");
            });

            hookFactory.after(param -> {
                log("Hello, Java after hook!");
            });
        });
    }

    @Override
    @NotNull
    public String getName() {
        return "ExampleJavaHook";
    }
}
