package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import java.util.Collections;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by lawson on 16/10/27.
 */

public final class HistBackprojActivity extends AppCompatActivity implements View.OnClickListener {

  public static final int BINS = 25;

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_hist_backproj);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_histogram_calculate).setOnClickListener(this);
    findViewById(R.id.button_backproj_calculate).setOnClickListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_hand);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void process(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);
    Mat hsvMat = new Mat();
    Imgproc.cvtColor(inMat, hsvMat, Imgproc.COLOR_BGR2HSV);
    Mat hueMat = new Mat(hsvMat.size(), hsvMat.depth());
    Core.mixChannels(Collections.singletonList(hsvMat), Collections.singletonList(hueMat),
        new MatOfInt(0, 0));
    MatOfInt histSize = new MatOfInt(BINS);
    MatOfFloat histRange = new MatOfFloat(0, 180);
    MatOfInt channels = new MatOfInt(0);
    Mat hist = new Mat();

    Imgproc.calcHist(Collections.singletonList(hueMat), channels, new Mat(), hist, histSize,
        histRange, false);
    Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX, -1, new Mat());
    Imgproc.calcBackProject(Collections.singletonList(hueMat), channels, hist, outMat, histRange,
        1);

    if (R.id.button_histogram_calculate == filter) {
      int w = 400;
      int h = 400;
      int bin_w = (int) Math.round(w / histSize.get(0, 0)[0]);
      Mat histImg = Mat.zeros(w, h, CV_8UC3);
      for (int i = 0; i < BINS; i++) {
        Imgproc.rectangle(histImg, new Point(i * bin_w, h),
            new Point((i + 1) * bin_w, h - Math.round(hist.get(i, 0)[0] * h / 255.0)),
            new Scalar(0, 0, 255), -1);
        bitmap = Bitmap.createBitmap(histImg.width(), histImg.height(), bitmap.getConfig());
        Utils.matToBitmap(histImg, bitmap);
      }
    } else {
      bitmap = Bitmap.createBitmap(outMat.width(), outMat.height(), bitmap.getConfig());
      Utils.matToBitmap(outMat, bitmap);
    }

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
      process(id);
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
