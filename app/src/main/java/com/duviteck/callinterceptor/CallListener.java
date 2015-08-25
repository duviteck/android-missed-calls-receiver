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

import static com.duviteck.callinterceptor.AnyLocalStorage.getLastProcessedCallDate;
import static com.duviteck.callinterceptor.AnyLocalStorage.updateLastProcessedCallDate;
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
      Log.e(TAG, "received IDLE state");

      /**
       * For now we use delayed processing, since we receive IDLE state a bit earlier than
       * system adds record about last call to CallLog. This solution seems to be bad,
       * since nobody guarantee that process will not be killed during specified delay.
       * As an alternative, Service can be started here.
       *
       * TODO: investigate Service necessity there
       */
      Handler handler = new Handler(Looper.getMainLooper());
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          Log.e(TAG, "processing started after [delay]:" + DELAY_BEFORE_PROCESS + "ms");

          final long lastCheckDate = getLastProcessedCallDate(context);
          Log.e(TAG, "[last check]:" + lastCheckDate + " " + new Date(valueOf(lastCheckDate)));

          CallEntity lastCall = CallLogHelper.getLastCall(context, lastCheckDate);

          if (lastCall != null) {
            Log.e(TAG, "[last call]:" + lastCall.date + " " + new Date(valueOf(lastCall.date)));
            if (lastCall.isMissed()) {
              Log.e(TAG, "detected new MISSED call");
              MissedCallsManager.onNewMissedCallsDetected(context, lastCall);
            } else if (lastCall.isIncomingOrOutgoing()) {
              Log.e(TAG, "detected new NON-MISSED call");
              MissedCallsManager.onNewOutgoingAndIncomingCallDetected(context, lastCall);
            } else if (lastCall.isDeclined()) {
              Log.e(TAG, "detected new DECLINED call");
              MissedCallsManager.onNewDeclinedCallDetected(context, lastCall);
            }

            updateLastProcessedCallDate(context, lastCall.date);
          } else {
            Log.e(TAG, "no new calls found");
          }
        }
      }, DELAY_BEFORE_PROCESS);
    }
  }
}
