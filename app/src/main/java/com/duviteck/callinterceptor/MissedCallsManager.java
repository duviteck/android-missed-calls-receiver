package com.duviteck.callinterceptor;

import android.content.Context;

import com.duviteck.callinterceptor.CallLogHelper.CallEntity;

/**
 * Created by duviteck on 24/08/15.
 */
public class MissedCallsManager {

  public static void onNewMissedCallsDetected(Context context, CallEntity missedCall) {
    processMissedCall(context, missedCall);
  }

  public static void onNewOutgoingAndIncomingCallDetected(Context context, CallEntity call) {
    processNonMissedCall(context, call);
  }

  public static void onNewDeclinedCallDetected(Context context, CallEntity declinedCall) {
    processNonMissedCall(context, declinedCall);
  }

  private static void processMissedCall(Context context, CallEntity missedCall) {
    RecallNotificationManager.showNotification(context, missedCall);
  }

  private static void processNonMissedCall(Context context, CallEntity call) {
    RecallNotificationManager.cancelNotification(context, call);
  }
}
