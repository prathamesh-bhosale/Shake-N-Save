package com.example.safetyalert;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class SOSWidget extends AppWidgetProvider {
    public static final String TOGGLE_SERVICE = "com.example.safetyalert.TOGGLE_SERVICE";
    private static boolean serviceRunning = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.s_o_s_widget);

        Intent intent = new Intent(context, SOSWidget.class);
        intent.setAction(TOGGLE_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0);

        views.setOnClickPendingIntent(R.id.sos_widget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TOGGLE_SERVICE.equals(intent.getAction())) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.s_o_s_widget);
            Intent serviceIntent = new Intent(context, ServiceMine.class);

            if (serviceRunning) {
                context.stopService(serviceIntent);
                remoteViews.setTextViewText(R.id.sos_widget_text, "SOS");
                Toast.makeText(context, "SOS service stopped", Toast.LENGTH_SHORT).show();
            } else {
                serviceIntent.setAction("start");
                ContextCompat.startForegroundService(context, serviceIntent);
                remoteViews.setTextViewText(R.id.sos_widget_text, "SOS ON");
                Toast.makeText(context, "SOS service started", Toast.LENGTH_SHORT).show();
            }
            serviceRunning = !serviceRunning;

            ComponentName componentName = new ComponentName(context, SOSWidget.class);
            AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
        } else {
            super.onReceive(context, intent);
        }
    }
}
