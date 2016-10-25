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
    findViewById(R.id.button_erode_dilate).setOnClickListener(this);
    findViewById(R.id.button_pyramid).setOnClickListener(this);
    findViewById(R.id.button_filter2d).setOnClickListener(this);
    findViewById(R.id.button_border).setOnClickListener(this);
    findViewById(R.id.button_sobel).setOnClickListener(this);
    findViewById(R.id.button_canny).setOnClickListener(this);
    findViewById(R.id.button_hough).setOnClickListener(this);
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
      case R.id.button_erode_dilate:
        startActivity(new Intent(MainActivity.this, EroDilActivity.class));
        break;
      case R.id.button_pyramid:
        startActivity(new Intent(MainActivity.this, PyramidsActivity.class));
        break;
      case R.id.button_filter2d:
        startActivity(new Intent(MainActivity.this, Filter2DActivity.class));
        break;
      case R.id.button_border:
        startActivity(new Intent(MainActivity.this, BorderActivity.class));
        break;
      case R.id.button_sobel:
        startActivity(new Intent(MainActivity.this, SobelLapActivity.class));
        break;
      case R.id.button_canny:
        startActivity(new Intent(MainActivity.this, CannyActivity.class));
        break;
      case R.id.button_hough:
        startActivity(new Intent(MainActivity.this, HoughActivity.class));
        break;
      default:
        break;
    }
  }
}
