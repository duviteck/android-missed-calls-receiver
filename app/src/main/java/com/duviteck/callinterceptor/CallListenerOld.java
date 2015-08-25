package com.duviteck.callinterceptor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class CallListenerOld extends BroadcastReceiver {

    private static final String TAG = "OhohoCallListenerOld";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "onReceive");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new CustomPhoneStateListener(context), PhoneStateListener.LISTEN_CALL_STATE);

    }

    public class CustomPhoneStateListener extends PhoneStateListener {

        //private static final String TAG = "PhoneStateChanged";
        Context context; //Context to make Toast if required

        public CustomPhoneStateListener(Context context) {
            super();
            this.context = context;
        }



        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            String number = TextUtils.isEmpty(incomingNumber) ? "null" : incomingNumber;
            switch (state) {
//                case TelephonyManager.
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.w(TAG, "CALL_STATE_IDLE " + number);
//                    db = new DBAdapter(context);
//                    data = db.selectWidget();
//                    options = db.selectVerticalite();
//                    activer = data.getInt(data.getColumnIndex("activer"));
//                    mouvement = options.getInt(options.getColumnIndex("activer_alarme_mouvement"));
//                    verticalite = options.getInt(options.getColumnIndex("activer_alarme_verticalite"));
//                    data.close();
//                    options.close();
//                    //when Idle i.e no call
//                    Toast.makeText(context,
//                            String.format(" " + mouvement + " " + activer),
//                            Toast.LENGTH_SHORT).show();
//                    if (activer == 1) {
//                        if (mouvement == 1 || verticalite == 1) {
//                            Toast.makeText(context,
//                                    String.format("and I'm here...."),
//                                    Toast.LENGTH_SHORT).show();
//                            startService(context);
//                            startnotif(context);
//                        }
//                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.w(TAG, "CALL_STATE_OFFHOOK " + number);
                    //when Off hook i.e in call
                    //Make intent and start your service here
//                    if (activer == 1) {
//                        if (mouvement == 1 || verticalite == 1) {
//                            stopService(context);
//                        }
//                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.w(TAG, "CALL_STATE_RINGING " + number);
                    //when Ringing
//                    if (activer == 1) {
//                        if (mouvement == 1 || verticalite == 1) {
//                            Toast.makeText(context,
//                                    String.format("and I'm here...."),
//                                    Toast.LENGTH_SHORT).show();
//                            stopService(context);
//                        }
//                    }
                    break;
                default:
                    break;
            }
        }
    }
}