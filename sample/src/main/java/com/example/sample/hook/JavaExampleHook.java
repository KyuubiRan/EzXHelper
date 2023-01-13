package com.example.sample.hook;

import android.app.Activity;

import com.github.kyuubiran.ezxhelper.HookFactory;
import com.github.kyuubiran.ezxhelper.Log;
import com.github.kyuubiran.ezxhelper.finders.MethodFinder;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import kotlin.ranges.IntRange;

public class JavaExampleHook extends BaseHook {
    @NotNull
    @Override
    public String getName() {
        return "JavaExampleHook";
    }

    @Override
    public void init() {
        var m = MethodFinder.fromClass(Activity.class)
                .findSuper(null)
                .filterByName("onCreate")
                .filter(f -> Arrays.stream(f.getParameterTypes()).allMatch(p -> p.getName().contains("Bundle")))
                .filterByParamCount(new IntRange(0, 2))
                .first();

        var unhook = HookFactory.createMethodHook(m, hookFactory -> {
            hookFactory.before(param -> {
                Log.i("hook before", null);
                param.setResult(null);
            });

            hookFactory.after(param -> {
                Log.i("hook after", null);
                param.setResult(null);
            });

            hookFactory.replace(param -> {
                Log.i("replaced method", null);
                return null;
            });
        });

        unhook.unhook();
    }
}
