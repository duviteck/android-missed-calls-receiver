package com.duviteck.callinterceptor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.duviteck.callinterceptor.CallLogHelper.CallEntity;

import java.io.IOException;

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

    builder.setContentTitle(getNotificationTitle());
    builder.setContentText(getContactName(call));
    builder.setSmallIcon(R.mipmap.ic_launcher);

    Bitmap contactPhoto = getContactLocalPhoto(context, call.number);
    if (contactPhoto != null) {
      builder.setLargeIcon(contactPhoto);
    }

    return builder.build();
  }

  private static String getNotificationTitle() {
    return "Call back for free";
  }

  private static String getContactName(CallEntity call) {
    return TextUtils.isEmpty(call.cachedName)
        ? PhoneNumberUtils.formatNumber(call.number)  // TODO: it covers only main cases
        : call.cachedName;
  }

  public static Bitmap getContactLocalPhoto(Context context, String phoneNumber) {
    // Look for contact with specified phone number
    Cursor contactCursor = context.getContentResolver().query(
        Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)),
        new String[]{PhoneLookup.PHOTO_URI},
        null, null, null);

    if (contactCursor != null && contactCursor.moveToFirst()) {
      String photoUri = contactCursor.getString(0);
      Log.w(TAG, "found contact by [number]:" + phoneNumber + " [uri]:" + photoUri);
      contactCursor.close();

      if (photoUri != null) {
        try {
          return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(photoUri));
        } catch (IOException e) {
          Log.w(TAG, "photo file not found, [uri]:" + photoUri);
        }
      } else {
        Log.w(TAG, "contact with [number]:" + phoneNumber + " has no assigned local photo");
      }

      return null;
    } else {
      Log.w(TAG, "can't find local contact with [number]:" + phoneNumber);
      return null;
    }
  }

}
