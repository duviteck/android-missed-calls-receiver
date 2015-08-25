package com.duviteck.callinterceptor;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog.Calls;

import java.util.Date;

import static java.lang.String.valueOf;

/**
 * Created by duviteck on 24/08/15.
 */
public class CallLogHelper {

  private static final String[] PROJECTION = new String[] {
      Calls.TYPE,
      Calls.NUMBER,
      Calls.DATE,
      Calls.DURATION
  };

  private class Column {
    static final int TYPE = 0;
    static final int NUMBER = TYPE + 1;
    static final int DATE = NUMBER + 1;
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
        long callDate = cursor.getLong(Column.DATE);
        long callDuration = cursor.getLong(Column.DURATION);

        res = new CallEntity(callType, callDate, callDuration, phoneNumber);
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
    int type = cursor.getColumnIndex(Calls.TYPE);
    int date = cursor.getColumnIndex(Calls.DATE);
    int duration = cursor.getColumnIndex(Calls.DURATION);
    while (cursor.moveToNext()) {
      String phNumber = cursor.getString(number);
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

      stringBuilder.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
          + dir + " \nCall Date:--- " + callDayTime
          + " \nCall duration in sec :--- " + callDuration);
      stringBuilder.append("\n----------------------------------");
    }
    cursor.close();
    return stringBuilder.toString();
  }


  public static class CallEntity {
    final long type;  // one of CallLog.Calls.TYPE
    final long date;
    final long duration;
    final String number;

    public CallEntity(long type, long date, long duration, String number) {
      this.type = type;
      this.date = date;
      this.duration = duration;
      this.number = number;
    }

    public boolean isMissed() {
      return type == Calls.MISSED_TYPE;
    }

    public boolean isDeclined() {
      return type != Calls.MISSED_TYPE &&
          type != Calls.INCOMING_TYPE &&
          type != Calls.OUTGOING_TYPE &&
          type != Calls.VOICEMAIL_TYPE;
    }

    public boolean isIncomingOrOutgoing() {
      return type == Calls.INCOMING_TYPE || type == Calls.OUTGOING_TYPE;
    }
  }
}
