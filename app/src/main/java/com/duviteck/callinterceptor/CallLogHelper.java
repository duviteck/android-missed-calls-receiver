package com.duviteck.callinterceptor;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog.Calls;
import android.telephony.PhoneNumberUtils;

import java.util.Date;

import static java.lang.String.valueOf;

/**
 * Created by duviteck on 24/08/15.
 */
public class CallLogHelper {

  private static final String[] PROJECTION = new String[] {
      Calls.TYPE,
      Calls.NUMBER,
      Calls.CACHED_NAME,
      Calls.DATE,
      Calls.DURATION
  };

  private class Column {
    static final int TYPE = 0;
    static final int NUMBER = TYPE + 1;
    static final int CACHED_NAME = NUMBER + 1;
    static final int DATE = CACHED_NAME + 1;
    static final int DURATION = DATE + 1;
  }


  public static CallEntity getLastCall(Context context, long since) {
    Cursor cursor = context.getContentResolver().query(
        Calls.CONTENT_URI,
        PROJECTION,
        Calls.DATE + " > ?",
        new String[]{valueOf(since)},
        Calls.DATE + " DESC");

    if (cursor != null) {
      CallEntity res = null;

      if (cursor.moveToFirst()) {
        int callType = cursor.getInt(Column.TYPE);
        String phoneNumber = cursor.getString(Column.NUMBER);
        String cachedName = cursor.getString(Column.CACHED_NAME);
        long callDate = cursor.getLong(Column.DATE);
        long callDuration = cursor.getLong(Column.DURATION);

        res = new CallEntity(callType, callDate, cachedName, callDuration, phoneNumber);
      }

      cursor.close();
      return res;
    }

    return null;
  }

  public static String getRecentCallsInfo(Context context) {
    StringBuilder stringBuilder = new StringBuilder();
    Cursor cursor = context.getContentResolver().query(
        Calls.CONTENT_URI,
        null,
        null,
        null,
        Calls.DATE + " DESC");
    int number = cursor.getColumnIndex(Calls.NUMBER);
    int name = cursor.getColumnIndex(Calls.CACHED_NAME);
    int type = cursor.getColumnIndex(Calls.TYPE);
    int date = cursor.getColumnIndex(Calls.DATE);
    int duration = cursor.getColumnIndex(Calls.DURATION);
    while (cursor.moveToNext()) {
      String phNumber = cursor.getString(number);
      String cachedName = cursor.getString(name);
      String callType = cursor.getString(type);
      String callDuration = cursor.getString(duration);

      String callDate = cursor.getString(date);
      Date callDayTime = new Date(Long.valueOf(callDate));

      String dir;
      int dircode = Integer.parseInt(callType);
      switch (dircode) {
        case Calls.OUTGOING_TYPE:
          dir = "OUTGOING";
          break;
        case Calls.INCOMING_TYPE:
          dir = "INCOMING";
          break;
        case Calls.MISSED_TYPE:
          dir = "MISSED";
          break;
        default:
          dir = "UNKNOWN " + dircode;
      }

      stringBuilder
          .append("\nPhone Number:--- ").append(phNumber)
          .append("\nPhone formatted:--- ").append(PhoneNumberUtils.formatNumber(phNumber))
          .append("\nCached name:--- ").append(cachedName)
          .append("\nCall Type:--- ").append(dir)
          .append("\nCall Date:--- ").append(callDayTime)
          .append("\nCall duration in sec :--- ").append(callDuration)
          .append("\n----------------------------------");
    }
    cursor.close();
    return stringBuilder.toString();
  }


  public static class CallEntity {
    final long type;  // one of CallLog.Calls.TYPE
    final long date;
    final String cachedName;
    final long duration;
    final String number;

    public CallEntity(long type, long date, String cachedName, long duration, String number) {
      this.type = type;
      this.date = date;
      this.cachedName = cachedName;
      this.duration = duration;
      this.number = number;
    }

    public boolean isMissed() {
      return type == Calls.MISSED_TYPE;
    }

    public boolean isDeclined() {
      return isOutOfType()   // Samsung case
          || isZeroLengthIncomingCall();
    }

    private boolean isOutOfType() {
      return type != Calls.MISSED_TYPE &&
          type != Calls.INCOMING_TYPE &&
          type != Calls.OUTGOING_TYPE;
    }

    private boolean isZeroLengthIncomingCall() {
      return type == Calls.INCOMING_TYPE && duration == 0;
    }

    public boolean isIncomingOrOutgoing() {
      return type == Calls.INCOMING_TYPE || type == Calls.OUTGOING_TYPE;
    }
  }
}
