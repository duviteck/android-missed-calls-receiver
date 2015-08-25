package com.duviteck.callinterceptor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

  TextView call;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    call = (TextView) findViewById(R.id.call);
  }

  @Override
  protected void onStart() {
    super.onStart();

    call.setText(CallLogHelper.getRecentCallsInfo(this));
  }
}
