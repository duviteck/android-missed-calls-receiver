package com.duviteck.callinterceptor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by duviteck on 24/08/15.
 */
public class AnyLocalStorage {

  private static final String TEST_SHARED_PREFS = "TEST_SHARED_PREFS";
  private static final String KEY_PREFIX = "MISSED_CALLS_";
  private static final String PREFERENCE_KEY_LAST_PROCESSED_CALL_DATE = KEY_PREFIX + "LAST_PROCESSED_CALL_DATE";

  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(TEST_SHARED_PREFS, Context.MODE_PRIVATE);
  }

  public static void updateLastProcessedCallDate(Context context, long time) {
    getSharedPreferences(context).edit().putLong(PREFERENCE_KEY_LAST_PROCESSED_CALL_DATE, time).apply();
  }

  public static long getLastProcessedCallDate(Context context) {
    return getSharedPreferences(context).getLong(PREFERENCE_KEY_LAST_PROCESSED_CALL_DATE, 0L);
  }
}
