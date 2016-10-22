package com.zhou.lawson.opencvtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private boolean initSucceed = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_main);

    init();
  }

  private void init() {
    findViewById(R.id.button_smoothing).setOnClickListener(this);
  }

  @Override public void onResume() {
    super.onResume();
    initSucceed = OpenCVLoader.initDebug();
  }

  @Override public void onClick(View v) {
    if (!initSucceed) {
      Toast.makeText(this, "Attempt to load libs fails！", Toast.LENGTH_SHORT).show();
      return;
    }
    switch (v.getId()) {
      case R.id.button_smoothing:
        startActivity(new Intent(MainActivity.this, SmoothingProActivity.class));
        break;
      default:
        break;
    }
  }
}
