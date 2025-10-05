package com.myfloatingmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import de.robv.android.xposed.XposedBridge;

public class FloatingWindowManager {
    
    public static void startFloatingWindow(ClassLoader classLoader) {
        try {
            // Get the current context
            Context context = getSystemContext();
            if (context != null) {
                // Check if auto-start is enabled
                SharedPreferences prefs = context.getSharedPreferences("floating_module_prefs", Context.MODE_PRIVATE);
                boolean autoStart = prefs.getBoolean("auto_start", false);
                
                if (autoStart) {
                    // Start the floating window service
                    Intent serviceIntent = new Intent(context, FloatingWindowService.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                    XposedBridge.log("MyFloatingModule: Floating window service started");
                }
            }
        } catch (Exception e) {
            XposedBridge.log("MyFloatingModule: Error starting floating window: " + e.getMessage());
        }
    }
    
    private static Context getSystemContext() {
        try {
            // Try to get system context through reflection
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            if (activityThread != null) {
                return (Context) activityThreadClass.getMethod("getSystemContext").invoke(activityThread);
            }
        } catch (Exception e) {
            XposedBridge.log("MyFloatingModule: Could not get system context: " + e.getMessage());
        }
        return null;
    }
}
