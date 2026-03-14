package com.artifex.sonui;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Field;

import com.artifex.solib.ConfigOptions;
import com.example.documenpro.DocumentMyApplication;

public class MainApp extends DocumentMyApplication {
    private static Context context;

    public MainApp() {
    }

    private void a() {
        ConfigOptions var1 = ConfigOptions.a();
        var1.a(true);
        var1.n(true);
        var1.b(true);
        var1.c(true);
        var1.o(false);
        var1.d(true);
        var1.e(true);
        var1.f(true);
        var1.g(true);
        var1.h(true);
        var1.i(true);
        var1.j(true);
        var1.k(true);
        var1.l(false);
        var1.m(true);
        var1.r(true);
        var1.p(true);
        var1.q(true);
        var1.t(false);
    }

    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        context = this;
        initArtifexResources();
        this.a();
        super.onCreate();
    }

    private void initArtifexResources() {
        String packageName = getPackageName();
        String sdkRPackage = "com.artifex.sonui.editor";
        String[] innerClasses = { "anim", "attr", "bool", "color", "dimen", "drawable", "id", "integer", "layout",
                "string", "style" };

        for (String inner : innerClasses) {
            try {
                Class<?> sdkRInner = Class.forName(sdkRPackage + ".R$" + inner);
                Field[] fields = sdkRInner.getDeclaredFields();
                for (Field field : fields) {
                    try {
                        String name = field.getName();
                        int id = getResources().getIdentifier(name, inner, packageName);
                        if (id != 0) {
                            field.setAccessible(true);
                            field.set(null, id);
                        } else {
                            if (inner.equals("string") && name.startsWith("sodk_editor_")) {
                                Log.w("MainApp", "Resource not found for SDK field: " + inner + "." + name);
                            }
                        }
                    } catch (Exception e) {
                        // Skip individual field errors
                    }
                }
            } catch (ClassNotFoundException e) {
                Log.e("MainApp", "SDK R inner class not found: " + inner, e);
            }
        }
    }
}
