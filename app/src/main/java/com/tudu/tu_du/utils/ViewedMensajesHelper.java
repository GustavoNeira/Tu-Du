package com.tudu.tu_du.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tudu.tu_du.providers.AuthProvider;
import com.tudu.tu_du.providers.UsuariosPrivider;

import java.util.List;

public class ViewedMensajesHelper {

    public static void updateOnline(boolean status, final Context context) {
        UsuariosPrivider usuariosProvider = new UsuariosPrivider();
        AuthProvider authProvider = new AuthProvider();

        if (authProvider.getUid() != null) {
            // Actualizar en línea si la aplicación se envía al fondo o si el estado es verdadero
            usuariosProvider.ActualizarOnline(authProvider.getUid(), status);
        }
    }
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Para versiones de Android 5.0 en adelante
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            if (!appTasks.isEmpty()) {
                ActivityManager.RecentTaskInfo taskInfo = appTasks.get(0).getTaskInfo();
                if ((taskInfo.baseIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
                    return true;
                }
            }
        } else {
            // Para versiones anteriores a Android 5.0
            List<ActivityManager.RecentTaskInfo> tasks = activityManager.getRecentTasks(1, ActivityManager.RECENT_WITH_EXCLUDED);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).baseIntent.getComponent();
                if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        }

        return false;
    }
}
