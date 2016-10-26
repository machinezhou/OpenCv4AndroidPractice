package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.BORDER_CONSTANT;

/**
 * Created by lawson on 16/10/25.
 */

public final class RemappingActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentFilter = R.id.button_original;
  public static final int CV_INTER_LINEAR = 1;  //CV_INTER_LINEAR from Imgproc is private

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_remapping);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_remapping_zoom).setOnClickListener(this);
    findViewById(R.id.button_remapping_top_bottom_reverse).setOnClickListener(this);
    findViewById(R.id.button_remapping_left_right_reverse).setOnClickListener(this);
    findViewById(R.id.button_remapping_both_reverse).setOnClickListener(this);

    whiteBoard.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_dog);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void switchFilter(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);

    int rows = inMat.rows();
    int cols = inMat.cols();
    float[] pixelsX = new float[rows * cols];
    float[] pixelsY = new float[rows * cols];

    for (int j = 0; j < rows; j++) {
      for (int i = 0; i < cols; i++) {
        switch (filter) {
          case R.id.button_remapping_zoom:
            /**
             * error: 缩小一半方法显示不对
             *
             * 详情：<link>http://answers.opencv.org/question/106715/remap-android/</>
             */
            if (i > cols * 0.25 && i < cols * 0.75 && j > rows * 0.25 && j < rows * 0.75) {
              pixelsX[j * cols + i] = (float) (2 * (i - cols * 0.25) + 0.5);
              pixelsY[j * cols + i] = (float) (2 * (i - rows * 0.25) + 0.5);
            } else {
              pixelsX[j * cols + i] = 0;
              pixelsY[j * cols + i] = 0;
            }
            break;
          /**
           * warning: 其余三个显示正常，但会在top和left的位置出现一条黑线
           */
          case R.id.button_remapping_top_bottom_reverse:
            pixelsX[j * cols + i] = i;
            pixelsY[j * cols + i] = rows - j;
            break;
          case R.id.button_remapping_left_right_reverse:
            pixelsX[j * cols + i] = cols - i;
            pixelsY[j * cols + i] = j;
            break;
          case R.id.button_remapping_both_reverse:
            pixelsX[j * cols + i] = cols - i;
            pixelsY[j * cols + i] = rows - j;
            break;
          default:
            break;
        }
      }
    }

    Mat mapX = new Mat(rows, cols, CvType.CV_32FC1);
    Mat mapY = new Mat(rows, cols, CvType.CV_32FC1);
    mapX.put(0, 0, pixelsX);
    mapY.put(0, 0, pixelsY);

    Imgproc.remap(inMat, outMat, mapX, mapY, CV_INTER_LINEAR, BORDER_CONSTANT, new Scalar(0, 0, 0));
    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);

    currentFilter = filter;
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentFilter == id) {
      return;
    }
    if (R.id.button_original == id) {
      whiteBoard.setImageBitmap(originalBitmap);
      currentFilter = id;
    } else {
      switchFilter(id);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
