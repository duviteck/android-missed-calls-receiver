package com.duviteck.callinterceptor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.duviteck.callinterceptor.CallLogHelper.CallEntity;

import java.util.Date;

import static java.lang.Long.valueOf;

/**
 * Created by duviteck on 24/08/15.
 */
public class CallListener extends BroadcastReceiver {

  private static final String TAG = "OhohoCallListener";
  private static final int DELAY_BEFORE_PROCESS = 1000;

  @Override
  public void onReceive(final Context context, Intent intent) {
    String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
    if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
      Log.d(TAG, "received IDLE state");

      /**
       * For now we use delayed processing, since we receive IDLE state a bit earlier than
       * system adds record about last call to CallLog. This solution seems to be bad,
       * since nobody guarantee that process will not be killed during specified delay.
       * As an alternative, Service can be started here.
       *
       * TODO: investigate Service necessity there
       * TODO: don't run DB query and other long-running operations on UI thread
       */
      Handler handler = new Handler(Looper.getMainLooper());
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          Log.d(TAG, "processing started after [delay]:" + DELAY_BEFORE_PROCESS + "ms");

          final long lastProcessedCallDate = AnyLocalStorage.getLastProcessedCallDate(context);
          Log.d(TAG, "[last check]:" + lastProcessedCallDate + " " + new Date(valueOf(lastProcessedCallDate)));

          CallEntity lastCall = CallLogHelper.getLastCall(context, lastProcessedCallDate);

          if (lastCall != null) {
            Log.d(TAG, "[last call]:" + lastCall.date + " " + new Date(valueOf(lastCall.date)));
            if (lastCall.isMissed()) {
              Log.d(TAG, "detected new MISSED call");
              MissedCallsManager.onNewMissedCallsDetected(context, lastCall);
            } else if (lastCall.isIncomingOrOutgoing()) {
              Log.d(TAG, "detected new NON-MISSED call");
              MissedCallsManager.onNewOutgoingAndIncomingCallDetected(context, lastCall);
            } else if (lastCall.isDeclined()) {
              Log.d(TAG, "detected new DECLINED call");
              MissedCallsManager.onNewDeclinedCallDetected(context, lastCall);
            }

            AnyLocalStorage.storeLastProcessedCallDate(context, lastCall.date);
          } else {
            Log.d(TAG, "no new calls found");
          }
        }
      }, DELAY_BEFORE_PROCESS);
    }
  }
}
