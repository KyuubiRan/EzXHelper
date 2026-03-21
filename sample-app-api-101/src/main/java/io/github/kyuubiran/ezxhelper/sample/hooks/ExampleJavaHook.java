package io.github.kyuubiran.ezxhelper.sample.hooks;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder;
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory;

public class ExampleJavaHook extends BaseHook {
    private static void logI(String msg) {
        Log.i("ExampleJavaHook", msg);
    }

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
                logI("Hello, Java before hook!");
            });

            hookFactory.after(param -> {
                logI("Hello, Java after hook!");
            });
        });

        var attachBaseContext = mf.filterByName("attachBaseContext")
                .filterByParamTypes(Context.class)
                .first();

        HookFactory.hookMethod(attachBaseContext, chain -> {
            Object[] args = chain.getArgs().toArray();
            args[0] = chain.getArg(0);
            logI("Hello, Java chain hook!");
            return chain.proceed(args);
        });

    }

    @Override
    @NotNull
    public String getName() {
        return "ExampleJavaHook";
    }
}
