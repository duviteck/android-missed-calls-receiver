package com.duviteck.callinterceptor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.duviteck.callinterceptor.CallLogHelper.CallEntity;

/**
 * Created by duviteck on 24/08/15.
 */
public class RecallNotificationManager {

  private static final String TAG = "OhohoNotification";
  private static final int DEFAULT_NOTIFICATION_ID = 37;

  public static void showNotification(Context context, CallEntity call) {
    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    String notificationTag = getNotificationTag(call);

    Notification notification = buildNotification(context, call);
    Log.w(TAG, "show notification [TAG]:" + notificationTag);
    manager.notify(notificationTag, DEFAULT_NOTIFICATION_ID, notification);
  }

  public static void cancelNotification(Context context, CallEntity call) {
    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    String notificationTag = getNotificationTag(call);

    Log.w(TAG, "cancel notification [TAG]:" + notificationTag);
    manager.cancel(notificationTag, DEFAULT_NOTIFICATION_ID);
  }

  public static String getNotificationTag(CallEntity call) {
    return call.number;
  }

  private static Notification buildNotification(Context context, CallEntity call) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());

    String contentTitle = TextUtils.isEmpty(call.cachedName)
        ? PhoneNumberUtils.formatNumber(call.number)  // TODO: it covers only main cases
        : call.cachedName;

    builder.setContentTitle(contentTitle);
    builder.setContentText("Recall for free");
    builder.setSmallIcon(R.mipmap.ic_launcher);
    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_home_logo));

    return builder.build();
  }
}
