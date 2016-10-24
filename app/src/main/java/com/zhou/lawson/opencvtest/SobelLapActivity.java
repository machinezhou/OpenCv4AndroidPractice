package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.core.CvType.CV_16S;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;

/**
 * Created by lawson on 16/10/24.
 */

public final class SobelLapActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sobel_lap);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_sobel).setOnClickListener(this);
    findViewById(R.id.button_laplace).setOnClickListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void sobel() {
    Utils.bitmapToMat(originalBitmap, inMat);

    Imgproc.GaussianBlur(inMat, inMat, new Size(3, 3), 0, 0, BORDER_DEFAULT);
    Mat inTemp = new Mat();
    Imgproc.cvtColor(inMat, inTemp, COLOR_BGRA2GRAY);

    /**
     * Sobel函数求完导数后会有负值，还有会大于255的值。而原图像是CV_8U，即8位无符号数，所以Sobel建立的图像位数不够，会有截断。
     * 因此要使用16位有符号的数据类型，即CV_16S,
     */
    Mat grad_x = new Mat();
    Mat grad_y = new Mat();
    Mat abs_grad_x = new Mat();
    Mat abs_grad_y = new Mat();
    Imgproc.Sobel(inTemp, grad_x, CV_16S, 1, 0, 3, 1, 0, BORDER_DEFAULT);
    Core.convertScaleAbs(grad_x, abs_grad_x);
    Imgproc.Sobel(inTemp, grad_y, CV_16S, 0, 1, 3, 1, 0, BORDER_DEFAULT);
    Core.convertScaleAbs(grad_y, abs_grad_y);
    Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, outMat);

    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);
  }

  private void laplace() {
    Utils.bitmapToMat(originalBitmap, inMat);
    Imgproc.GaussianBlur(inMat, inMat, new Size(3, 3), 0, 0, BORDER_DEFAULT);
    Mat inTemp = new Mat();
    Imgproc.cvtColor(inMat, inTemp, COLOR_BGRA2GRAY);
    Imgproc.Laplacian(inTemp, outMat, CV_16S, 3, 1, 0, BORDER_DEFAULT);
    Core.convertScaleAbs(outMat, outMat);

    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentFilter == id) {
      return;
    }

    switch (id) {
      case R.id.button_sobel:
        sobel();
        break;
      case R.id.button_laplace:
        laplace();
        break;
      case R.id.button_original:
      default:
        whiteBoard.setImageBitmap(originalBitmap);
        break;
    }

    currentFilter = id;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
