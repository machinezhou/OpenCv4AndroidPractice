package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;

/**
 * Created by lawson on 16/10/26.
 *
 * 直方图对比省略
 */

public final class HistogramActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_histogram);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_histogram_equalize).setOnClickListener(this);
    findViewById(R.id.button_histogram_calculate).setOnClickListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_image_512);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void switchFilter(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);

    switch (filter) {
      case R.id.button_histogram_calculate:
        List<Mat> mats = new ArrayList<>();
        Core.split(inMat, mats);

        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat histRange = new MatOfFloat(0, 256);
        MatOfInt channels = new MatOfInt(0);
        Mat hist_b = new Mat();
        Mat hist_g = new Mat();
        Mat hist_r = new Mat();
        Imgproc.calcHist(mats.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange,
            false);
        Imgproc.calcHist(mats.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange,
            false);
        Imgproc.calcHist(mats.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange,
            false);

        int hist_w = 400;
        int hist_h = 400;
        int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);
        Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
        Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

        for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
          Imgproc.line(histImage,
              new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
              new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])),
              new Scalar(255, 0, 0), 2, 8, 0);
          Imgproc.line(histImage,
              new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
              new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])),
              new Scalar(0, 255, 0), 2, 8, 0);
          Imgproc.line(histImage,
              new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
              new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])),
              new Scalar(0, 0, 255), 2, 8, 0);
        }
        /**
         * 高宽与原图不一样
         */
        bitmap = Bitmap.createBitmap(histImage.width(), histImage.height(), bitmap.getConfig());
        Utils.matToBitmap(histImage, bitmap);
        break;
      case R.id.button_histogram_equalize:
        Mat temp = new Mat();
        Imgproc.cvtColor(inMat, temp, COLOR_BGRA2GRAY);
        Imgproc.equalizeHist(temp, outMat);
        bitmap = Bitmap.createBitmap(outMat.width(), outMat.height(), bitmap.getConfig());
        Utils.matToBitmap(outMat, bitmap);
        break;
      default:
        break;
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
