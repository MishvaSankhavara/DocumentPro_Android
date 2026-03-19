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
        try {
            String packageName = getPackageName();
            Class rClass = null;
            String[] possibleRClasses = {
                    "com.artifex.sonui.editor.R",
                    packageName + ".R",
                    packageName + ".editor.R"
            };

            for (String rClassName : possibleRClasses) {
                try {
                    rClass = Class.forName(rClassName);
                    Log.d("MainApp", "Found R class: " + rClassName);
                    break;
                } catch (ClassNotFoundException e) {
                    // Try next
                }
            }

            if (rClass != null) {
                for (Class innerClass : rClass.getDeclaredClasses()) {
                    String inner = innerClass.getSimpleName();
                    for (Field field : innerClass.getDeclaredFields()) {
                        try {
                            String name = field.getName();
                            int id = getResources().getIdentifier(name, inner, packageName);

                            // Try variations if not found
                            if (id == 0 && name.startsWith("sodk_editor_")) {
                                id = getResources().getIdentifier(name.substring(12), inner, packageName);
                            }
                            if (id == 0 && name.startsWith("sodk_")) {
                                id = getResources().getIdentifier(name.substring(5), inner, packageName);
                            }
                            if (id == 0) {
                                id = getResources().getIdentifier(name.toLowerCase(), inner, packageName);
                            }
                            if (id == 0 && name.startsWith("sodk_editor_")) {
                                id = getResources().getIdentifier(name.substring(12).toLowerCase(), inner, packageName);
                            }

                            if (id != 0) {
                                field.setAccessible(true);
                                field.set(null, id);
                                if (inner.equals("string")) {
                                    Log.d("MainApp", "Mapped SDK field: " + name + " to ID: " + id);
                                }
                            } else {
                                if (inner.equals("string") && name.startsWith("sodk_editor_")) {
                                    Log.w("MainApp", "Resource not found for SDK field: " + inner + "." + name);
                                }
                            }
                        } catch (Exception e) {
                            // Skip individual field errors
                        }
                    }
                }
            } else {
                Log.e("MainApp", "Could not find any R class for SmartOffice initialization");
            }
        } catch (Exception e) {
            Log.e("MainApp", "Failed to initialize Artifex resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
