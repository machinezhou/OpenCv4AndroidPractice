package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 * Created by lawson on 16/10/23.
 */

public final class PyramidsActivity extends AppCompatActivity
    implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

  private ImageView whiteBoard;
  private SeekBar seekBar;
  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pyramids);

    init();
  }

  private void init() {

    whiteBoard = (ImageView) findViewById(R.id.white_board);
    seekBar = (SeekBar) findViewById(R.id.seek_bar);
    seekBar.setOnSeekBarChangeListener(this);
    seekBar.setEnabled(false);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_pyramid_down).setOnClickListener(this);
    findViewById(R.id.button_pyramid_up).setOnClickListener(this);
    findViewById(R.id.button_threshold).setOnClickListener(this);

    originalBitmap =
        BitmapFactory.decodeResource(getResources(), R.mipmap.test_image_512); //图片尺寸需为2的指数
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void operate(int command) {
    Utils.bitmapToMat(bitmap, inMat);

    switch (command) {
      case R.id.button_pyramid_down:
        Imgproc.pyrDown(inMat, outMat, new Size(inMat.size().width / 2, inMat.size().height / 2));
        /**
         * 由于尺寸发生变化，matToBitmap源码存在尺寸相等的断言，因此重新创建相同尺寸bitmap
         */
        bitmap = Bitmap.createBitmap(outMat.width(), outMat.height(), bitmap.getConfig());
        Utils.matToBitmap(outMat, bitmap);
        break;
      case R.id.button_pyramid_up:
        /**
         * 注意：此处由于尺寸不断加大，在创建bitmap的时候存在oom风险，因此需要限制向上采集的最大的尺寸
         */
        Imgproc.pyrUp(inMat, outMat, new Size(inMat.size().width * 2, inMat.size().height * 2));
        bitmap = Bitmap.createBitmap(outMat.width(), outMat.height(), bitmap.getConfig());
        Utils.matToBitmap(outMat, bitmap);
        break;
      case R.id.button_original:
      default:
        bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        break;
    }

    whiteBoard.setImageBitmap(bitmap);
  }

  private void threshold() {
    if (!seekBar.isEnabled()) {
      return;
    }

    Utils.bitmapToMat(originalBitmap, inMat);
    Mat inTemp = new Mat();
    Imgproc.cvtColor(inMat, inTemp, COLOR_BGRA2GRAY);
    Mat outTemp = new Mat(inTemp.size(), inTemp.type());
    Imgproc.threshold(inTemp, outTemp, seekBar.getProgress(), 255, THRESH_BINARY);
    Utils.matToBitmap(outTemp, bitmap);
    whiteBoard.setImageBitmap(bitmap);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.button_threshold) {
      seekBar.setEnabled(true);
      bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
      threshold();
    } else {
      seekBar.setEnabled(false);
      operate(id);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    threshold();
  }
}
