package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGRA;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;

/**
 * Created by lawson on 16/10/25.
 */

public final class HoughActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;
  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_hough);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_hough_line).setOnClickListener(this);
    findViewById(R.id.button_hough_circle).setOnClickListener(this);

    originalBitmap =
        BitmapFactory.decodeResource(getResources(), R.mipmap.test_circle); //图片尺寸需为2的指数
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void switchFilter(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);

    switch (filter) {
      case R.id.button_hough_line:
        Mat dst = new Mat();
        Imgproc.Canny(inMat, dst, 50, 200, 3, false);
        Imgproc.cvtColor(dst, outMat, COLOR_GRAY2BGRA);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(dst, lines, 1, Math.PI / 180, 50, 50, 10);
        /**
         * HoughLinesP只会往lines里填充cols为1，即只有一列的矩阵，因此可以据此遍历
         */
        int rowsForLine = lines.rows();
        /**
         * 由于反复绘制的原因，此处有明显的延迟
         */
        for (int i = 0; i < rowsForLine; i++) {
          double[] l = lines.get(i, 0);
          Imgproc.line(outMat, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255),
              3);
        }
        break;
      case R.id.button_hough_circle:

        Imgproc.cvtColor(inMat, outMat, COLOR_BGRA2GRAY);
        Imgproc.GaussianBlur(outMat, outMat, new Size(9, 9), 2, 2);
        Mat circles = new Mat();
        /**
         * param2设置太大会返回空circles,设置太小会非常耗时
         */
        Imgproc.HoughCircles(outMat, circles, CV_HOUGH_GRADIENT, 1, outMat.rows() / 20.0, 100, 35,
            0, 0);
        Utilp.printMat(circles);
        if (circles.empty()) {
          Toast.makeText(this, "未检测到圆圈", Toast.LENGTH_SHORT).show();
        } else {
          /**
           * HoughCircles只会往circles里填充rows为1，即只有一行的矩阵，因此可以据此遍历
           */
          int colsForCircle = circles.cols();
          for (int i = 0; i < colsForCircle; i++) {
            double[] l = circles.get(0, i);
            Point center = new Point(Math.round(l[0]), Math.round(l[1]));
            int radius = (int) Math.round(l[2]);

            Imgproc.circle(outMat, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            Imgproc.circle(outMat, center, radius, new Scalar(0, 0, 255), 3, 8, 0);
          }
        }

        break;
      case R.id.button_original:
      default:
        inMat.copyTo(outMat);
        break;
    }
    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);

    currentFilter = filter;
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentFilter == id) {
      return;
    }
    switchFilter(id);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
