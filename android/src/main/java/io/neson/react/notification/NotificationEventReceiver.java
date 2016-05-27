package io.neson.react.notification;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * Handles user's interaction on notifications.
 *
 * Sends broadcast to the application, launches the app if needed.
 */
public class NotificationEventReceiver extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "id";
    final static String ACTION = "action";
    final static String PAYLOAD = "payload";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        Log.i("ReactSystemNotification", "NotificationEventReceiver: Received: " + extras.getString(ACTION) + ", Notification ID: " + extras.getString(NOTIFICATION_ID) + ", payload: " + extras.getString(PAYLOAD));

        // If the application is not running or is not in foreground, start it with the notification
        // passed in
//        if (!applicationIsRunning(context)) {
//            String packageName = context.getApplicationContext().getPackageName();
//            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//
//            launchIntent.putExtra("initialSysNotificationId", extras.getString(NOTIFICATION_ID));
//            launchIntent.putExtra("initialSysNotificationAction", extras.getString(ACTION));
//            launchIntent.putExtra("initialSysNotificationPayload", extras.getString(PAYLOAD));
//            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            context.startActivity(launchIntent);
//            Log.i("ReactSystemNotification", "NotificationEventReceiver: FLAG_ACTIVITY_CLEAR_TOP : Launching: " + packageName);
//        } else {
            String packageName = context.getApplicationContext().getPackageName();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            launchIntent.putExtra("initialSysNotificationId", extras.getString(NOTIFICATION_ID));
            launchIntent.putExtra("initialSysNotificationAction", extras.getString(ACTION));
            launchIntent.putExtra("initialSysNotificationPayload", extras.getString(PAYLOAD));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(launchIntent);
            Log.i("ReactSystemNotification", "NotificationEventReceiver: FLAG_ACTIVITY_SINGLE_TOP :  Launching: " + packageName);
            sendBroadcast(context, extras); // If the application is already running in foreground, send a brodcast too
//        }
    }

    private void sendBroadcast(Context context, Bundle extras) {
      Intent brodcastIntent = new Intent("NotificationEvent");

      brodcastIntent.putExtra("id", extras.getString(NOTIFICATION_ID));
      brodcastIntent.putExtra("action", extras.getString(ACTION));
      brodcastIntent.putExtra("payload", extras.getString(PAYLOAD));

      context.sendBroadcast(brodcastIntent);
      Log.v("ReactSystemNotification", "NotificationEventReceiver: Broadcast Sent: NotificationEvent: " + extras.getString(ACTION) + ", Notification ID: " + extras.getString(NOTIFICATION_ID) + ", payload: " + extras.getString(PAYLOAD));
    }

    private boolean applicationIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.processName.equals(context.getApplicationContext().getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                            || processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE
//                             || processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                        for (String d: processInfo.pkgList) {
                            Log.v("ReactSystemNotification", "NotificationEventReceiver: ok: " + d);
                            return true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
