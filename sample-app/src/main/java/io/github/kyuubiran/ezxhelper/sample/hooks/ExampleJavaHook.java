package io.github.kyuubiran.ezxhelper.sample.hooks;

import android.app.Application;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import io.github.kyuubiran.ezxhelper.core.finders.MethodFinder;
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory;

public class ExampleJavaHook extends BaseHook {
    private ExampleJavaHook() {
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
                Log.i(getName(), "Hello, Java before hook!");
            });

            hookFactory.after(param -> {
                Log.i(getName(), "Hello, Java after hook!");
            });
        });
    }

    @Override
    @NotNull
    public String getName() {
        return "ExampleJavaHook";
    }
}
